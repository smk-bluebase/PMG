package bluebase.in.pioneermusicgym;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;


public abstract class UpdateLocalDatabase {

    private String urlSingers = CommonUtils.IP + "/PMG/pmg_android/search/getSingers.php";
    private String urlComposers = CommonUtils.IP + "/PMG/pmg_android/search/getComposers.php";
    private String urlAlbums = CommonUtils.IP + "/PMG/pmg_android/search/getAlbums.php";
    private String urlMovies = CommonUtils.IP + "/PMG/pmg_android/search/getMovies.php";

    Context context;
    Boolean[] progress = new Boolean[4];

    private Handler handler = new Handler();
    public static JsonObject jsonObject;
    ProgressDialog progressDialog;
    ProgressDialog progressDialog1;

    public UpdateLocalDatabase(Context context){
        this.context = context;

        CommonUtils.startDatabaseHelper(context);
    }

    public void checkServerAvailability(int time) {
        AsyncCheckAvailability asyncCheckAvailability = new AsyncCheckAvailability();
        asyncCheckAvailability.execute(String.valueOf(time));
    }

    public void updateSingers(){
        progressDialog1 = new ProgressDialog(context);
        progressDialog1.setCancelable(false);
        progressDialog1.setMessage("Loading...");
        progressDialog1.show();

        int maxId = CommonUtils.dataBaseHelper.singersMaxId();

        jsonObject = new JsonObject();
        jsonObject.addProperty("lowerLimit", maxId);
        jsonObject.addProperty("upperLimit", maxId + 10);

        PostSingerTable postSingerTable = new PostSingerTable(context);
        postSingerTable.checkServerAvailability(2);
    }

    public void updateComposers(){
        progressDialog1 = new ProgressDialog(context);
        progressDialog1.setCancelable(false);
        progressDialog1.setMessage("Loading...");
        progressDialog1.show();

        int maxId = CommonUtils.dataBaseHelper.composersMaxId();

        jsonObject = new JsonObject();
        jsonObject.addProperty("lowerLimit", maxId);
        jsonObject.addProperty("upperLimit", maxId + 10);

        PostComposerTable postComposerTable = new PostComposerTable(context);
        postComposerTable.checkServerAvailability(2);
    }

    public void updateAlbums(){
        progressDialog1 = new ProgressDialog(context);
        progressDialog1.setCancelable(false);
        progressDialog1.setMessage("Loading...");
        progressDialog1.show();

        int maxId = CommonUtils.dataBaseHelper.albumsMaxId();

        jsonObject = new JsonObject();
        jsonObject.addProperty("lowerLimit", maxId);
        jsonObject.addProperty("upperLimit", maxId + 10);

        PostAlbumTable postAlbumTable = new PostAlbumTable(context);
        postAlbumTable.checkServerAvailability(2);
    }

    public void updateMovies(){
        progressDialog1.setCancelable(false);
        progressDialog1.setMessage("Loading...");
        progressDialog1.show();

        int maxId = CommonUtils.dataBaseHelper.moviesMaxId();

        jsonObject = new JsonObject();
        jsonObject.addProperty("lowerLimit", maxId);
        jsonObject.addProperty("upperLimit", maxId + 10);

        PostMovieTable postMovieTable = new PostMovieTable(context);
        postMovieTable.checkServerAvailability(2);
    }

    public void updateHome(){
        progressDialog1 = new ProgressDialog(context);
        Arrays.fill(progress, false);

        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        jsonObject = new JsonObject();
        jsonObject.addProperty("lowerLimit", 0);
        jsonObject.addProperty("upperLimit", 10);

        new Thread(() -> {
            int i = 0;
            boolean isProgress = true;
            boolean isUpdateSuccessful = true;
            while(i < 4){
                try {
                    if (isUpdateSuccessful){
                        switch (i) {
                            case 0:
                                CommonUtils.dataBaseHelper.deleteSingers();
                                PostSingerTable postSingerTable = new PostSingerTable(context);
                                postSingerTable.checkServerAvailability(2);
                                break;
                            case 1:
                                CommonUtils.dataBaseHelper.deleteComposers();
                                PostComposerTable postComposerTable = new PostComposerTable(context);
                                postComposerTable.checkServerAvailability(2);
                                break;
                            case 2:
                                CommonUtils.dataBaseHelper.deleteAlbums();
                                PostAlbumTable postAlbumTable = new PostAlbumTable(context);
                                postAlbumTable.checkServerAvailability(2);
                                break;
                            case 3:
                                CommonUtils.dataBaseHelper.deleteMovies();
                                PostMovieTable postMovieTable = new PostMovieTable(context);
                                postMovieTable.checkServerAvailability(2);
                                break;
                            default:
                                break;
                        }
                        isUpdateSuccessful = false;
                    }

                    if (progress[i]) {
                        if(isProgress){
                            isProgress = false;
                        }else{
                            isProgress = true;
                        }
                        isUpdateSuccessful = true;
                        i++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            progressDialog.dismiss();
            handler.post(() -> onPostUpdate());
        }).start();

    }

    private class PostSingerTable extends PostRequest{
        public PostSingerTable(Context context){
            super(context);
        }

        public void serverAvailability(boolean isServerAvailable){
            if(isServerAvailable){
                super.postRequest(urlSingers, jsonObject);
            }else {
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
                progressDialog1.dismiss();
            }
        }

        public void onFinish(JSONArray jsonArray) {
            progressDialog1.dismiss();

            try {
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                if(jsonObject.getBoolean("status")) {
                    JSONArray singers = jsonObject.getJSONArray("singers");

                    for (int i = 0; i < singers.length(); i++) {
                        JSONArray singer = singers.getJSONArray(i);

                        int singerId = 0;
                        String singerName = " ";
                        int numberOfSongs = 0;
                        int numberOfAlbums = 0;
                        int numberOfMovies = 0;

                        singerId = singer.getInt(0);
                        singerName = singer.getString(1);
                        numberOfSongs = singer.getInt(2);
                        numberOfAlbums = singer.getInt(3);
                        numberOfMovies = singer.getInt(4);

                        CommonUtils.dataBaseHelper.insertSingers(singerId, singerName, numberOfSongs, numberOfAlbums, numberOfMovies);
                    }
                }

            }catch(JSONException e){
                e.printStackTrace();
            }

            progress[0] = true;
        }

    }

    private class PostComposerTable extends PostRequest{
        public PostComposerTable(Context context){
            super(context);
        }

        public void serverAvailability(boolean isServerAvailable){
            if(isServerAvailable){
                super.postRequest(urlComposers, jsonObject);
            }else {
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
                progressDialog1.dismiss();
            }
        }

        public void onFinish(JSONArray jsonArray){
            progressDialog1.dismiss();

            try {
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                if(jsonObject.getBoolean("status")) {
                    JSONArray composers = jsonObject.getJSONArray("composers");

                    for (int i = 0; i < composers.length(); i++) {
                        JSONArray composer = composers.getJSONArray(i);

                        int composerId = 0;
                        String composerName = " ";
                        int numberOfSongs = 0;
                        int numberOfAlbums = 0;
                        int numberOfMovies = 0;

                        composerId = composer.getInt(0);
                        composerName = composer.getString(1);
                        numberOfSongs = composer.getInt(2);
                        numberOfAlbums = composer.getInt(3);
                        numberOfMovies = composer.getInt(4);

                        CommonUtils.dataBaseHelper.insertComposers(composerId, composerName, numberOfSongs, numberOfAlbums, numberOfMovies);
                    }
                }

            }catch(JSONException e){
                e.printStackTrace();
            }

            progress[1] = true;
        }

    }

    private class PostAlbumTable extends PostRequest{
        public PostAlbumTable(Context context){
            super(context);
        }

        public void serverAvailability(boolean isServerAvailable){
            if(isServerAvailable){
                super.postRequest(urlAlbums, jsonObject);
            }else {
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
                progressDialog1.dismiss();
            }
        }

        public void onFinish(JSONArray jsonArray){
            progressDialog1.dismiss();

            try {
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                if(jsonObject.getBoolean("status")) {
                    JSONArray albums = jsonObject.getJSONArray("albums");

                    for (int i = 0; i < albums.length(); i++) {
                        JSONArray album = albums.getJSONArray(i);

                        int albumId = 0;
                        String albumName = " ";
                        String year = " ";
                        int numberOfSongs = 0;

                        albumId = album.getInt(0);
                        albumName = album.getString(1);
                        year = album.getString(2);
                        numberOfSongs = album.getInt(3);

                        CommonUtils.dataBaseHelper.insertAlbums(albumId, albumName, year, numberOfSongs);
                    }
                }

            }catch(JSONException e){
                e.printStackTrace();
            }

            progress[2] = true;
        }

    }

    private class PostMovieTable extends PostRequest{
        public PostMovieTable(Context context){
            super(context);
        }

        public void serverAvailability(boolean isServerAvailable){
            if(isServerAvailable){
                super.postRequest(urlMovies, jsonObject);
            }else {
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
                progressDialog1.dismiss();
            }
        }

        public void onFinish(JSONArray jsonArray){
            progressDialog1.dismiss();

            try {
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                if(jsonObject.getBoolean("status")) {
                    JSONArray movies = jsonObject.getJSONArray("movies");

                    for (int i = 0; i < movies.length(); i++) {
                        JSONArray movie = movies.getJSONArray(i);

                        int movieId = 0;
                        String movieName = " ";
                        String year = " ";
                        int numberOfSongs = 0;

                        movieId = movie.getInt(0);
                        movieName = movie.getString(1);
                        year = movie.getString(2);
                        numberOfSongs = movie.getInt(3);

                        CommonUtils.dataBaseHelper.insertMovies(movieId, movieName, year, numberOfSongs);
                    }
                }

            }catch(JSONException e){
                e.printStackTrace();
            }

            progress[3] = true;
        }

    }

    private class AsyncCheckAvailability extends AsyncTask<String, Boolean, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL(CommonUtils.IP);
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    int time = Integer.parseInt(strings[0]);
                    urlc.setConnectTimeout(time * 1000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        Log.wtf("Connection", "Success !");
                        return true;
                    } else {
                        return false;
                    }
                } catch (MalformedURLException e1) {
                    return false;
                } catch (IOException e) {
                    return false;
                }
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean isServerAvailable) {
            serverAvailability(isServerAvailable);
        }

    }

    public abstract void serverAvailability(boolean isServerAvailable);

    public abstract void onPostUpdate();

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        CommonUtils.closeDataBaseHelper();
    }

}