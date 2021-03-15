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
    public static String urlSingers = CommonUtils.IP + "/pmg_android/search/getSingers.php";
    public static String urlComposers = CommonUtils.IP + "/pmg_android/search/getComposers.php";
    public static String urlMovies = CommonUtils.IP + "/pmg_android/search/getMovies.php";

    public static Context context;
    public static Boolean[] progress = new Boolean[4];

    private Handler handler = new Handler();
    public static JsonObject jsonObject;
    ProgressDialog progressDialog;
    public static ProgressDialog progressDialog1;
    public static boolean isUpdateAll = false;
    public static boolean isSingerAvailable = true;
    public static boolean isComposerAvailable = true;
    public static boolean isMovieAvailable = true;

    public UpdateLocalDatabase(Context context){
        this.context = context;

        CommonUtils.startDatabaseHelper(context);
    }

    public void checkServerAvailability(int time) {
        AsyncCheckAvailability asyncCheckAvailability = new AsyncCheckAvailability();
        asyncCheckAvailability.execute(String.valueOf(time));
    }

    public static void updateSingers(int index, int limit){
        isUpdateAll = false;

        jsonObject = new JsonObject();
        jsonObject.addProperty("index", index);
        jsonObject.addProperty("limit", limit);

        PostSingerTable postSingerTable = new PostSingerTable(context, index);
        postSingerTable.checkServerAvailability(2);
    }

    public static void updateComposers(int index, int limit){
        isUpdateAll = false;

        jsonObject = new JsonObject();
        jsonObject.addProperty("index", index);
        jsonObject.addProperty("limit", limit);

        PostComposerTable postComposerTable = new PostComposerTable(context, index);
        postComposerTable.checkServerAvailability(2);
    }

    public static void updateMovies(int index, int limit){
        isUpdateAll = false;

        jsonObject = new JsonObject();
        jsonObject.addProperty("index", index);
        jsonObject.addProperty("limit", limit);

        PostMovieTable postMovieTable = new PostMovieTable(context, index);
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
        jsonObject.addProperty("index", 0);
        jsonObject.addProperty("limit", CommonUtils.homeQueryLimit);

        new Thread(() -> {
            int i = 0;
            boolean isProgress = true;
            boolean isUpdateSuccessful = true;
            while(i < 3){
                try {
                    if (isUpdateSuccessful){
                        switch (i) {
                            case 0:
                                CommonUtils.dataBaseHelper.deleteSingers();
                                PostSingerTable postSingerTable = new PostSingerTable(context, 0);
                                postSingerTable.checkServerAvailability(2);
                                break;
                            case 1:
                                CommonUtils.dataBaseHelper.deleteComposers();
                                PostComposerTable postComposerTable = new PostComposerTable(context, 0);
                                postComposerTable.checkServerAvailability(2);
                                break;
                            case 2:
                                CommonUtils.dataBaseHelper.deleteMovies();
                                PostMovieTable postMovieTable = new PostMovieTable(context, 0);
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

    public static class PostSingerTable extends PostRequest{
        int index;

        public PostSingerTable(Context context, int index){
            super(context);
            this.index = index;
        }

        public void serverAvailability(boolean isServerAvailable){
            if(isServerAvailable){
                super.postRequest(urlSingers, jsonObject);
            }else {
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
                if(isUpdateAll) progressDialog1.dismiss();
            }
        }

        public void onFinish(JSONArray jsonArray) {
            if(isUpdateAll) progressDialog1.dismiss();

            try {
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                if(jsonObject.getBoolean("status")) {
                    JSONArray singers = jsonObject.getJSONArray("singers");

                    for (int i = 0; i < singers.length(); i++) {
                        JSONArray singer = singers.getJSONArray(i);

                        int singerId = 0;
                        String singerName = " ";
                        int numberOfSongs = 0;
                        int numberOfMovies = 0;

                        singerId = singer.getInt(0);
                        singerName = singer.getString(1);
                        numberOfSongs = singer.getInt(2);
                        numberOfMovies = singer.getInt(3);

                        CommonUtils.dataBaseHelper.insertSingers(singerId, singerName, numberOfSongs, numberOfMovies);
                    }

                    isSingerAvailable = true;
                    if(!isUpdateAll) HomeFragment.populateSingers(index);
                }else {
                    Toast.makeText(context, "No More Data", Toast.LENGTH_SHORT).show();
                    isSingerAvailable = false;
                }

            }catch(JSONException e){
                e.printStackTrace();
            }

            progress[0] = true;
        }

    }

    public static class PostComposerTable extends PostRequest{
        int index;

        public PostComposerTable(Context context, int index){
            super(context);
            this.index = index;
        }

        public void serverAvailability(boolean isServerAvailable){
            if(isServerAvailable){
                super.postRequest(urlComposers, jsonObject);
            }else {
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
                if(isUpdateAll) progressDialog1.dismiss();
            }
        }

        public void onFinish(JSONArray jsonArray){
            if(isUpdateAll) progressDialog1.dismiss();

            try {
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                if(jsonObject.getBoolean("status")) {
                    JSONArray composers = jsonObject.getJSONArray("composers");

                    for (int i = 0; i < composers.length(); i++) {
                        JSONArray composer = composers.getJSONArray(i);

                        int composerId = 0;
                        String composerName = " ";
                        int numberOfSongs = 0;
                        int numberOfMovies = 0;

                        composerId = composer.getInt(0);
                        composerName = composer.getString(1);
                        numberOfSongs = composer.getInt(2);
                        numberOfMovies = composer.getInt(3);

                        CommonUtils.dataBaseHelper.insertComposers(composerId, composerName, numberOfSongs, numberOfMovies);
                    }

                    isComposerAvailable = true;
                    if(!isUpdateAll) HomeFragment.populateComposers(index);
                }else {
                    Toast.makeText(context, "No More Data", Toast.LENGTH_SHORT).show();
                    isComposerAvailable = false;
                }

            }catch(JSONException e){
                e.printStackTrace();
            }

            progress[1] = true;
        }

    }

    public static class PostMovieTable extends PostRequest{
        int index;

        public PostMovieTable(Context context, int index){
            super(context);
            this.index = index;
        }

        public void serverAvailability(boolean isServerAvailable){
            if(isServerAvailable){
                super.postRequest(urlMovies, jsonObject);
            }else {
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
                if(isUpdateAll) progressDialog1.dismiss();
            }
        }

        public void onFinish(JSONArray jsonArray){
            if(isUpdateAll) progressDialog1.dismiss();

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

                    isMovieAvailable = true;
                    if(!isUpdateAll) HomeFragment.populateMovies(index);
                }else {
                    Toast.makeText(context, "No More Data", Toast.LENGTH_SHORT).show();
                    isMovieAvailable = false;
                }

            }catch(JSONException e){
                e.printStackTrace();
            }

            progress[2] = true;
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