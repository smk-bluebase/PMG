package bluebase.in.pioneermusicgym;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class MalayalamFragment extends Fragment {
    public static Context context;

    public static RecyclerView malayalamMovieRecyclerView;
    public static ArrayList<MovieItems> malayalamMovieList;

    public static String urlGetMovies = CommonUtils.IP + "/pmg_android/search/getMoviesLanguageWise.php";
    public static String urlSearchMovies = CommonUtils.IP + "/pmg_android/search/searchMovies.php";
    public static String urlSingerMovies = CommonUtils.IP + "/pmg_android/search/getSingerMovies.php";
    public static String urlComposerMovies = CommonUtils.IP + "/pmg_android/search/getComposerMovies.php";

    public static JsonObject jsonObject;

    public static int malayalamIndex;
    public static int searchMalayalamMovieIndex;

    public static boolean isSearching = false;
    public static String searchQuery = "";
    public static boolean isScrolling = false;
    public static boolean isMalayalamMoviesAvailable = true;

    public static int languageId = 3;

    public static boolean isLoaded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_malayalam, container, false);

        malayalamMovieRecyclerView = view.findViewById(R.id.malayalamMovieRecyclerView);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getContext();

        isLoaded = true;
    }

    public static void onOpen(){
        if(isLoaded) {
            LibraryFragment.searchView.setQuery("", false);

            malayalamMovieRecyclerView.clearOnScrollListeners();

            malayalamMovieRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (!recyclerView.canScrollVertically(1) && isMalayalamMoviesAvailable) {
                        if (isSearching) {
                            searchMalayalamMovieIndex = searchMalayalamMovieIndex + CommonUtils.queryLimit;
                            getMalayalamMovies(searchQuery, searchMalayalamMovieIndex, CommonUtils.queryLimit, urlSearchMovies);
                        } else {
                            malayalamIndex = malayalamIndex + CommonUtils.queryLimit;
                            getMalayalamMovies("", malayalamIndex, CommonUtils.queryLimit, urlGetMovies);
                        }

                        isScrolling = true;
                    }
                }
            });

            malayalamIndex = 0;
            searchMalayalamMovieIndex = 0;

            isSearching = false;
            searchQuery = "";
            isScrolling = false;
            isMalayalamMoviesAvailable = true;

            malayalamMovieList = new ArrayList<>();

            getMalayalamMovies("", malayalamIndex, CommonUtils.queryLimit, urlGetMovies);
        }
    }

    public static void onQuerySubmit(String query){
        malayalamMovieList = new ArrayList<>();
        isMalayalamMoviesAvailable = true;

        if(!query.equals("")) {
            isSearching = true;
            isScrolling = false;

            searchMalayalamMovieIndex = 0;

            if(query.startsWith("SingerId : ")){
                CommonUtils.isSearching = true;
                searchQuery = query.substring(11);
                getSingerMovies(searchQuery, searchMalayalamMovieIndex, urlSingerMovies);
            }else if(query.startsWith("ComposerId : ")) {
                CommonUtils.isSearching = true;
                searchQuery = query.substring(13);
                getComposerMovies(searchQuery, searchMalayalamMovieIndex, urlComposerMovies);
            }else {
                CommonUtils.isSearching = false;
                searchQuery = query;
                getMalayalamMovies(searchQuery, searchMalayalamMovieIndex, CommonUtils.queryLimit, urlSearchMovies);
            }

        }else {
            isSearching = false;
            searchQuery = "";
            isScrolling = true;
            CommonUtils.isSearching = false;

            malayalamIndex = 0;

            getMalayalamMovies(searchQuery, malayalamIndex, CommonUtils.queryLimit, urlGetMovies);
        }
    }

    public static void onQueryChange(String newText){
        malayalamMovieList = new ArrayList<>();
        isMalayalamMoviesAvailable = true;

        if(!newText.equals("")) {
            isSearching = true;
            isScrolling = false;

            searchMalayalamMovieIndex = 0;

            if(newText.startsWith("SingerId : ")){
                CommonUtils.isSearching = true;
                searchQuery = newText.substring(11);
                getSingerMovies(searchQuery, searchMalayalamMovieIndex, urlSingerMovies);
            }else if(newText.startsWith("ComposerId : ")) {
                CommonUtils.isSearching = true;
                searchQuery = newText.substring(13);
                getComposerMovies(searchQuery, searchMalayalamMovieIndex, urlComposerMovies);
            }else {
                CommonUtils.isSearching = false;
                searchQuery = newText;
                getMalayalamMovies(searchQuery, searchMalayalamMovieIndex, CommonUtils.queryLimit, urlSearchMovies);
            }

        }else {
            isSearching = false;
            searchQuery = "";
            isScrolling = true;
            CommonUtils.isSearching = false;

            malayalamIndex = 0;

            getMalayalamMovies(searchQuery, malayalamIndex, CommonUtils.queryLimit, urlGetMovies);
        }
    }

    public static void getMalayalamMovies(String searchQuery, int index, int limit, String url){
        jsonObject = new JsonObject();
        jsonObject.addProperty("languageId", languageId);

        if(!searchQuery.equals(""))
            jsonObject.addProperty("movieName", searchQuery);

        jsonObject.addProperty("index", index);
        jsonObject.addProperty("limit", limit);

        PostMalayalamMovies postMalayalamMovies = new PostMalayalamMovies(context, url, index);
        postMalayalamMovies.checkServerAvailability(2);
    }

    public static void getSingerMovies(String searchQuery, int index, String url){
        jsonObject = new JsonObject();
        jsonObject.addProperty("singerId", searchQuery);
        jsonObject.addProperty("languageId", languageId);

        PostMalayalamMovies postMalayalamMovies = new PostMalayalamMovies(context, url, index);
        postMalayalamMovies.checkServerAvailability(2);
    }

    public static void getComposerMovies(String searchQuery, int index, String url){
        jsonObject = new JsonObject();
        jsonObject.addProperty("composerId", searchQuery);
        jsonObject.addProperty("languageId", languageId);

        PostMalayalamMovies postMalayalamMovies = new PostMalayalamMovies(context, url, index);
        postMalayalamMovies.checkServerAvailability(2);
    }

    public static class PostMalayalamMovies extends PostRequest{
        String url;
        int index;

        public PostMalayalamMovies(Context context, String url, int index){
            super(context);
            this.url = url;
            this.index = index;
        }

        @Override
        public void serverAvailability(boolean isServerAvailable){
            if(isServerAvailable){
                super.postRequest(url, jsonObject);
            }else {
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFinish(JSONArray jsonArray){
            if(index == 0) malayalamMovieList = new ArrayList<>();

            try{
                JSONObject jsonObject =  jsonArray.getJSONObject(0);

                if(jsonObject.getBoolean("status")){
                    JSONArray movies = jsonObject.getJSONArray("movies");

                    for(int i = 0; i < movies.length(); i++){
                        JSONArray movie = movies.getJSONArray(i);

                        int movieId = 0;
                        String movieTitle = " ";
                        String year = " ";
                        int numberOfSongs = 0;

                        try {
                            if(!movie.getString(0).equals("")) movieId = movie.getInt(0);
                            if(!movie.getString(1).equals("")) movieTitle = movie.getString(1);
                            if(!movie.getString(2).equals("")) year = movie.getString(2);
                            if(!movie.getString(3).equals("")) numberOfSongs = movie.getInt(3);

                            MovieItems item = new MovieItems();

                            item.setMovieId(movieId);
                            item.setMovieTitle(movieTitle);
                            item.setYear(year);
                            item.setNumberOfSongs(numberOfSongs);

                            malayalamMovieList.add(item);

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                    populateMalayalamMovies();

                }else if(isScrolling){
                    Toast.makeText(context, "No More Data", Toast.LENGTH_SHORT).show();
                    isMalayalamMoviesAvailable = false;
                }else if(isSearching){
                    Toast.makeText(context, "No Match Found", Toast.LENGTH_SHORT).show();
                    isMalayalamMoviesAvailable = false;
                    populateMalayalamMovies();
                }

            }catch(JSONException e){
                e.printStackTrace();
            }
        }

        public void populateMalayalamMovies(){
            malayalamMovieRecyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            linearLayoutManager.scrollToPosition(index);
            MovieAdapter malayalamMovieAdapter = new MovieAdapter(new ArrayList<>(malayalamMovieList));
            malayalamMovieRecyclerView.setLayoutManager(linearLayoutManager);
            malayalamMovieRecyclerView.setAdapter(null);
            malayalamMovieRecyclerView.setAdapter(malayalamMovieAdapter);

            malayalamMovieAdapter.setOnItemClickListener(position -> {
                CommonUtils.isSearching = false;
                LibraryFragment.viewPager1.setCurrentItem(0);
                LibraryFragment.searchView.setQuery("MovieId : " + malayalamMovieAdapter.getData().get(position).getMovieId(), true);
            });
        }
    }

}
