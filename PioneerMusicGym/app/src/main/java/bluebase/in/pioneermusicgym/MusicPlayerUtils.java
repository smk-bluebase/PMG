package bluebase.in.pioneermusicgym;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static bluebase.in.pioneermusicgym.MusicPlayerFragment.hindi;
import static bluebase.in.pioneermusicgym.MusicPlayerFragment.malayalam;
import static bluebase.in.pioneermusicgym.MusicPlayerFragment.materialButtonToggleGroup;
import static bluebase.in.pioneermusicgym.MusicPlayerFragment.tamil;

public class MusicPlayerUtils {

    public static int playlistId = 0;

    public static int findPosition(int songId){
        int i = 0;
        for(SongQueueItems item : CommonUtils.songQueue){
            if(item.getSongId() == songId){
                return i;
            }

            i += 1;
        }

        return -1;
    }

    public static String getSongURL(String languageCodeStr, String fileLocation){
        String languageFolder = "";

        switch (languageCodeStr) {
            case "ta":
                languageFolder = "tamil";
                break;
            case "hi":
                languageFolder = "hindi";
                break;
            case "ma":
                languageFolder = "malayalam";
                break;
            default:
                languageFolder = "tamil";
        }

        String urlSong = CommonUtils.IP + "/songs/" + languageFolder + "/" + fileLocation;

        return urlSong;
    }

    public static String calculatePitchText(int progress){
        DecimalFormat df = new DecimalFormat("0.0");

        String pitchStr;
        if(progress == 120){
            pitchStr = "12.0";
        }else if(progress >= 0) {
            pitchStr = "+" + df.format(progress / 10.0);
        }else {
            pitchStr = "-" + df.format(Math.abs(progress) / 10.0);
        }

        return pitchStr;
    }

    public static void addToPlaylist(Context context){
        List playlistsList = new ArrayList<>();

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.add_to_playlist, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);

        RecyclerView playlistsRecyclerView = promptView.findViewById(R.id.playlistsRecyclerView);

        CommonUtils.startDatabaseHelper(context);

        JSONArray jsonArray = CommonUtils.dataBaseHelper.selectPlaylists();

        for(int i = 0; i < jsonArray.length(); i++){
            try{
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                PlaylistItems item = new PlaylistItems();
                item.setPlaylistId(jsonObject.getInt("playlistId"));
                item.setPlaylistTitle(jsonObject.getString("playlistTitle"));
                item.setNumberOfSongs(-1);
                item.setCreatedOn("");

                playlistsList.add(item);

            }catch(JSONException e){
                e.printStackTrace();
            }
        }

        CommonUtils.closeDataBaseHelper();

        if(jsonArray.length() > 0) {
            playlistsRecyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            PlaylistAdapter playlistAdapter = new PlaylistAdapter(new ArrayList<>(playlistsList));
            playlistsRecyclerView.setLayoutManager(linearLayoutManager);
            playlistsRecyclerView.setAdapter(null);
            playlistsRecyclerView.setAdapter(playlistAdapter);
        }

        alertDialogBuilder.setCancelable(false)
            .setPositiveButton("OK", (dialog, id) -> {
                CommonUtils.startDatabaseHelper(context);

                int[] songsIds = CommonUtils.dataBaseHelper.selectPlaylistSongs(playlistId);

                if(findIndex(songsIds, CommonUtils.songId) == -1){
                    CommonUtils.dataBaseHelper.insertPlaylistSongs(playlistId, CommonUtils.songId);
                    Toast.makeText(context, "Added to Playlist", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "Already added", Toast.LENGTH_SHORT).show();
                }

                CommonUtils.closeDataBaseHelper();

                dialog.cancel();
            })
            .setNegativeButton("Cancel", (dialog, id) -> {
                dialog.cancel();
            });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public static int findIndex(int arr[], int t) {
        if (arr == null) return -1;

        int len = arr.length;
        int i = 0;

        while (i < len) {
            if (arr[i] == t) return i;
            else i = i + 1;
        }

        return -1;
    }

    public static void setMaterialToggleLanguage(String languageCodeStr){
        switch(languageCodeStr){
            case "ta":
                tamil.setVisibility(View.VISIBLE);
                hindi.setVisibility(View.GONE);
                malayalam.setVisibility(View.GONE);
                materialButtonToggleGroup.clearChecked();
                materialButtonToggleGroup.check(R.id.tamil);
                break;
            case "ma":
                malayalam.setVisibility(View.VISIBLE);
                tamil.setVisibility(View.GONE);
                hindi.setVisibility(View.GONE);
                materialButtonToggleGroup.clearChecked();
                materialButtonToggleGroup.check(R.id.malayalam);
                break;
            case "hi":
                hindi.setVisibility(View.VISIBLE);
                tamil.setVisibility(View.GONE);
                malayalam.setVisibility(View.GONE);
                materialButtonToggleGroup.clearChecked();
                materialButtonToggleGroup.check(R.id.hindi);
                break;
            default:
                tamil.setVisibility(View.VISIBLE);
                hindi.setVisibility(View.GONE);
                malayalam.setVisibility(View.GONE);
                materialButtonToggleGroup.clearChecked();
                materialButtonToggleGroup.check(R.id.tamil);
        }
    }

}
