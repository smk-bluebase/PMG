package bluebase.in.pioneermusicgym;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SongsFragment extends Fragment {
    public static Context context;

    public static RecyclerView songsRecyclerView;
    public static ArrayList<SongItems> songsList;
    public static SongAdapter songAdapter;

    public static String urlGetSongs = CommonUtils.IP + "/PMG/pmg_android/search/getSongs.php";
    public static String urlSearchSongs = CommonUtils.IP + "/PMG/pmg_android/search/searchSongs.php";

    public static JsonObject jsonObject;

    public static int lowerLimit;
    public static int upperLimit;
    public static int searchLowerLimit;
    public static int searchUpperLimit;

    public static boolean isSearching = false;
    public static String searchQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);

        songsRecyclerView = view.findViewById(R.id.songsRecyclerView);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getContext();

        songsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(!recyclerView.canScrollVertically(1)){
                    if(isSearching){
                        searchLowerLimit = searchUpperLimit;
                        searchUpperLimit = searchUpperLimit + CommonUtils.queryLimit;

                        getSongs(searchQuery, searchLowerLimit, searchUpperLimit, urlSearchSongs);
                    }else{
                        lowerLimit = upperLimit;
                        upperLimit = upperLimit + CommonUtils.queryLimit;

                        getSongs("", lowerLimit, upperLimit, urlGetSongs);
                    }
                }
            }
        });

        songsList = new ArrayList<>();

        lowerLimit = 0;
        upperLimit = CommonUtils.queryLimit;

        getSongs("", lowerLimit, upperLimit, urlGetSongs);
    }

    public static void onQuerySubmit(String query){
        songsList = new ArrayList<>();

        if(!query.equals("")) {
            isSearching = true;
            searchQuery = query;

            searchLowerLimit = 0;
            searchUpperLimit = CommonUtils.queryLimit;

            getSongs(searchQuery, searchLowerLimit, searchUpperLimit, urlSearchSongs);
        }else {
            isSearching = false;
            searchQuery = "";

            lowerLimit = 0;
            upperLimit = CommonUtils.queryLimit;

            getSongs(searchQuery, lowerLimit, upperLimit, urlGetSongs);
        }
    }

    public static void onQueryChange(String newText){
        songsList = new ArrayList<>();

        if(!newText.equals("")) {
            isSearching = true;
            searchQuery = newText;

            searchLowerLimit = 0;
            searchUpperLimit = CommonUtils.queryLimit;

            getSongs(searchQuery, searchLowerLimit, searchUpperLimit, urlSearchSongs);
        }else {
            isSearching = false;
            searchQuery = "";

            lowerLimit = 0;
            upperLimit = CommonUtils.queryLimit;

            getSongs(searchQuery, lowerLimit, upperLimit, urlGetSongs);
        }
    }

    public static void getSongs(String searchQuery, int lowerLimit, int upperLimit, String url){
        jsonObject = new JsonObject();
        jsonObject.addProperty("songName", searchQuery);
        jsonObject.addProperty("lowerLimit", lowerLimit);
        jsonObject.addProperty("upperLimit", upperLimit);

        PostGetSongs postGetSongs = new PostGetSongs(context, url);
        postGetSongs.checkServerAvailability(2);
    }

    public static class PostGetSongs extends PostRequest{
        String url;

        public PostGetSongs(Context context, String url){
            super(context);
            this.url = url;
        }

        @Override
        public void serverAvailability(boolean isServerAvailable) {
            if (isServerAvailable) {
                super.postRequest(url, jsonObject);
            } else {
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFinish(JSONArray jsonArray) {
            try{
                JSONObject jsonObject =  jsonArray.getJSONObject(0);

                if(jsonObject.getBoolean("status")){
                    JSONArray songs = jsonObject.getJSONArray("songs");

                    int j = 0;

                    for(int i = 0; i < songs.length(); i++){
                        JSONArray song = songs.getJSONArray(i);

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

                            SongItems item = new SongItems();

                            item.setSongId(songId);
                            item.setSongTitle(songTitle);
                            item.setMovieName(movieName);
                            item.setMovieSinger(movieSinger);
                            item.setYear(year);
                            item.setDuration(duration);

                            if (j == 0) j = 1;
                            else j = 0;

                            songsList.add(item);

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                    songsRecyclerView.setHasFixedSize(true);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                    songAdapter = new SongAdapter(songsList);
                    songsRecyclerView.setLayoutManager(linearLayoutManager);
                    songsRecyclerView.setAdapter(null);
                    songsRecyclerView.setAdapter(songAdapter);
                }else if(isSearching){
                    Toast.makeText(context, "No Match Found", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "No Data", Toast.LENGTH_SHORT).show();
                }

            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

}
