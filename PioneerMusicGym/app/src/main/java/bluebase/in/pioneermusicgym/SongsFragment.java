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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SongsFragment extends Fragment {
    public static Context context;

    public static RecyclerView songsRecyclerView;
    public static ArrayList<SongItems> songsList;
    public static SongAdapter songAdapter;

    public static String urlGetSongs = CommonUtils.IP + "/pmg_android/search/getSongs.php";
    public static String urlSearchSongs = CommonUtils.IP + "/pmg_android/search/searchSongs.php";
    public static String urlMovieSongs = CommonUtils.IP + "/pmg_android/search/getMovieSongs.php";

    public static JsonObject jsonObject;

    public static int songIndex;
    public static int searchSongIndex;

    public static boolean isSearching = false;
    public static String searchQuery = "";
    public static boolean isScrolling = false;
    public static boolean isSongsAvailable = true;

    public static boolean isLoaded = false;


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

        isLoaded = true;

        if(CommonUtils.isHomeSearching) LibraryFragment.onLoaded();
        else onOpen();
    }

    public static void onOpen(){
        if(isLoaded) {
            LibraryFragment.searchView.setQuery("", false);

            songsRecyclerView.clearOnScrollListeners();

            songsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (!recyclerView.canScrollVertically(1) && isSongsAvailable) {
                        if (isSearching) {
                            searchSongIndex = searchSongIndex + CommonUtils.queryLimit;
                            getSongs(searchQuery, searchSongIndex, CommonUtils.queryLimit, urlSearchSongs);
                        } else {
                            songIndex = songIndex + CommonUtils.queryLimit;
                            getSongs("", songIndex, CommonUtils.queryLimit, urlGetSongs);
                        }

                        isScrolling = true;
                    }
                }
            });

            songIndex = 0;
            searchSongIndex = 0;

            isSearching = false;
            searchQuery = "";
            isScrolling = false;
            isSongsAvailable = true;

            songsList = new ArrayList<>();

            getSongs("", songIndex, CommonUtils.queryLimit, urlGetSongs);
        }
    }

    public static void onQuerySubmit(String query){
        songsList = new ArrayList<>();
        isSongsAvailable = true;

        if(!query.equals("")) {
            isSearching = true;
            isScrolling = false;

            searchSongIndex = 0;

            if(query.startsWith("MovieId : ")){
                searchQuery = query.substring(10);
                getMovieSongs(searchQuery, searchSongIndex, urlMovieSongs);
            }else{
                searchQuery = query;
                getSongs(searchQuery, searchSongIndex, CommonUtils.queryLimit, urlSearchSongs);
            }

        }else {
            isSearching = false;
            searchQuery = "";
            isScrolling = true;

            songIndex = 0;

            getSongs(searchQuery, songIndex, CommonUtils.queryLimit, urlGetSongs);
        }
    }

    public static void onQueryChange(String newText){
        songsList = new ArrayList<>();
        isSongsAvailable = true;

        if(!newText.equals("")) {
            isSearching = true;
            isScrolling = false;

            searchSongIndex = 0;

            if(newText.startsWith("MovieId : ")){
                searchQuery = newText.substring(10);
                getMovieSongs(searchQuery, searchSongIndex, urlMovieSongs);
            }else{
                searchQuery = newText;
                getSongs(searchQuery, searchSongIndex, CommonUtils.queryLimit, urlSearchSongs);
            }

        }else {
            isSearching = false;
            searchQuery = "";
            isScrolling = true;

            songIndex = 0;

            getSongs(searchQuery, songIndex, CommonUtils.queryLimit, urlGetSongs);
        }
    }

    public static void getSongs(String searchQuery, int index, int limit, String url){
        jsonObject = new JsonObject();
        jsonObject.addProperty("songName", searchQuery);
        jsonObject.addProperty("index", index);
        jsonObject.addProperty("limit", limit);

        PostGetSongs postGetSongs = new PostGetSongs(context, url, index);
        postGetSongs.checkServerAvailability(2);
    }

    public static void getMovieSongs(String searchQuery, int index, String url){
        jsonObject = new JsonObject();
        jsonObject.addProperty("movieId", searchQuery);

        PostGetSongs postGetSongs = new PostGetSongs(context, url, index);
        postGetSongs.checkServerAvailability(2);
    }

    public static class PostGetSongs extends PostRequest{
        String url;
        int index;

        public PostGetSongs(Context context, String url, int index){
            super(context);
            this.url = url;
            this.index = index;
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
            if(index == 0) songsList = new ArrayList<>();

            try{
                JSONObject jsonObject =  jsonArray.getJSONObject(0);

                if(jsonObject.getBoolean("status")) {
                    JSONArray songs = jsonObject.getJSONArray("songs");

                    for (int i = 0; i < songs.length(); i++) {
                        JSONArray song = songs.getJSONArray(i);

                        int songId = 0;
                        String songTitle = "";
                        String movieName = "";
                        String movieSinger = "";
                        String year = "";
                        String duration = "";

                        try {
                            if (!song.getString(0).equals("")) songId = song.getInt(0);
                            if (!song.getString(1).equals("")) songTitle = song.getString(1);
                            if (!song.getString(2).equals("")) movieName = song.getString(2);
                            if (!song.getString(3).equals("")) movieSinger = song.getString(3);
                            if (!song.getString(4).equals("")) year = song.getString(4);
                            if (!song.getString(5).equals("")) duration = song.getString(5);

                            SongItems item = new SongItems();

                            item.setSongId(songId);
                            item.setSongTitle(songTitle);
                            item.setMovieName(movieName);
                            item.setMovieSinger(movieSinger);
                            item.setYear(year);
                            item.setDuration(duration);

                            songsList.add(item);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    populateSongs();

                }else if(isScrolling){
                    Toast.makeText(context, "No More Data", Toast.LENGTH_SHORT).show();
                    isSongsAvailable = false;
                }else if(isSearching){
                    Toast.makeText(context, "No Match Found", Toast.LENGTH_SHORT).show();
                    isSongsAvailable = false;
                    populateSongs();
                }

            }catch(JSONException e){
                e.printStackTrace();
            }
        }

        public void populateSongs(){
            songsRecyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            linearLayoutManager.scrollToPosition(index);
            songAdapter = new SongAdapter(new ArrayList<>(songsList));
            songsRecyclerView.setLayoutManager(linearLayoutManager);
            songsRecyclerView.setAdapter(null);
            songsRecyclerView.setAdapter(songAdapter);

            songAdapter.setOnItemClickListener(position -> {
                CommonUtils.fromNotification = false;
                MusicPlayerFragment.stopPlayer(MusicPlayerFragment.musicPlayerIntent);

                CommonUtils.songId = songAdapter.getData().get(position).getSongId();

                SongQueueItems item = new SongQueueItems();
                item.setSongId(CommonUtils.songId);

                if (!CommonUtils.isPresent(item))
                    CommonUtils.songQueue.add(item);

                ((MainActivity) context).getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                ((MainActivity) context).getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack("musicPlayerFragment")
                        .replace(R.id.fragment_container, new MusicPlayerFragment(), "musicPlayerFragment")
                        .commit();

                MainActivity.navigationView.setCheckedItem(R.id.nav_music_player);
            });
        }
    }

}
