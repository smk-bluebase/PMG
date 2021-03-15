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

public abstract class GetSongs {
    Context context;

    Boolean[] progress;

    String urlGetSong = CommonUtils.IP + "/pmg_android/search/getSong.php";

    Handler handler = new Handler();
    JsonObject jsonObject;
    ProgressDialog progressDialog;

    public GetSongs(Context context){
        this.context = context;

        CommonUtils.startDatabaseHelper(context);
    }

    public void checkServerAvailability(int time) {
        AsyncCheckAvailability asyncCheckAvailability = new AsyncCheckAvailability();
        asyncCheckAvailability.execute(String.valueOf(time));
    }

    public void getSongDetails(int[] songs){
        progress = new Boolean[songs.length];
        Arrays.fill(progress, false);

        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        CommonUtils.dataBaseHelper.deleteSongMaster();

        new Thread(() -> {
            int i = 0;
            boolean isUpdateSuccessful = true;
            while(i < songs.length){
                try {
                    if (isUpdateSuccessful){
                        PostSongDetails postSongDetails = new PostSongDetails(context, i, songs[i]);
                        postSongDetails.checkServerAvailability(2);
                        isUpdateSuccessful = false;
                    }

                    if (progress[i]) {
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

    private class PostSongDetails extends PostRequest{
        int progressId;
        int songId;

        public PostSongDetails(Context context, int progressId, int songId){
            super(context);
            this.progressId = progressId;
            this.songId = songId;
        }

        public void serverAvailability(boolean isServerAvailable){
            if(isServerAvailable){
                jsonObject = new JsonObject();
                jsonObject.addProperty("songId", songId);

                super.postRequest(urlGetSong, jsonObject);
            }else {
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
            }
        }

        public void onFinish(JSONArray jsonArray) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                if(jsonObject.getBoolean("status")) {
                    JSONArray song = jsonObject.getJSONArray("song");

                    int songId = 0;
                    String songTitle = " ";
                    String movieName = " ";
                    String movieSinger = " ";
                    String year = " ";
                    String duration = " ";

                    try {
                        if(!song.getString(0).equals("")) songId = song.getInt(0);
                        if(!song.getString(1).equals("")) songTitle = song.getString(1);
                        if(!song.getString(2).equals("")) movieName = song.getString(2);
                        if(!song.getString(3).equals("")) movieSinger = song.getString(3);
                        if(!song.getString(4).equals("")) year = song.getString(4);
                        if(!song.getString(5).equals("")) duration = song.getString(5);

                        CommonUtils.dataBaseHelper.insertSongMaster(songId, songTitle, movieName, movieSinger, year, duration);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

            }catch(JSONException e){
                e.printStackTrace();
            }

            progress[progressId] = true;
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
