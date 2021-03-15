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

public class HindiFragment extends Fragment {
    public static Context context;

    public static RecyclerView hindiMovieRecyclerView;
    public static ArrayList<MovieItems> hindiMovieList;

    public static String urlGetMovies = CommonUtils.IP + "/pmg_android/search/getMoviesLanguageWise.php";
    public static String urlSearchMovies = CommonUtils.IP + "/pmg_android/search/searchMovies.php";
    public static String urlSingerMovies = CommonUtils.IP + "/pmg_android/search/getSingerMovies.php";
    public static String urlComposerMovies = CommonUtils.IP + "/pmg_android/search/getComposerMovies.php";

    public static JsonObject jsonObject;

    public static int hindiIndex;
    public static int searchHindiMovieIndex;

    public static boolean isSearching = false;
    public static String searchQuery = "";
    public static boolean isScrolling = false;
    public static boolean isHindiMoviesAvailable = true;

    public static int languageId = 2;

    public static boolean isLoaded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hindi, container, false);

        hindiMovieRecyclerView = view.findViewById(R.id.hindiMovieRecyclerView);

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

            hindiMovieRecyclerView.clearOnScrollListeners();

            hindiMovieRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (!recyclerView.canScrollVertically(1) && isHindiMoviesAvailable) {
                        if (isSearching) {
                            searchHindiMovieIndex = searchHindiMovieIndex + CommonUtils.queryLimit;
                            getHindiMovies(searchQuery, searchHindiMovieIndex, CommonUtils.queryLimit, urlSearchMovies);
                        } else {
                            hindiIndex = hindiIndex + CommonUtils.queryLimit;
                            getHindiMovies("", hindiIndex, CommonUtils.queryLimit, urlGetMovies);
                        }

                        isScrolling = true;
                    }
                }
            });

            hindiIndex = 0;
            searchHindiMovieIndex = 0;

            isSearching = false;
            searchQuery = "";
            isScrolling = false;
            isHindiMoviesAvailable = true;

            hindiMovieList = new ArrayList<>();

            getHindiMovies("", hindiIndex, CommonUtils.queryLimit, urlGetMovies);
        }
    }

    public static void onQuerySubmit(String query){
        hindiMovieList = new ArrayList<>();
        isHindiMoviesAvailable = true;

        if(!query.equals("")) {
            isSearching = true;
            isScrolling = false;

            searchHindiMovieIndex = 0;

            if(query.startsWith("SingerId : ")){
                CommonUtils.isSearching = true;
                searchQuery = query.substring(11);
                getSingerMovies(searchQuery, searchHindiMovieIndex, urlSingerMovies);
            }else if(query.startsWith("ComposerId : ")) {
                CommonUtils.isSearching = true;
                searchQuery = query.substring(13);
                getComposerMovies(searchQuery, searchHindiMovieIndex, urlComposerMovies);
            }else {
                CommonUtils.isSearching = false;
                searchQuery = query;
                getHindiMovies(searchQuery, searchHindiMovieIndex, CommonUtils.queryLimit, urlSearchMovies);
            }

        }else {
            isSearching = false;
            searchQuery = "";
            isScrolling = true;
            CommonUtils.isSearching = false;

            hindiIndex = 0;

            getHindiMovies(searchQuery, hindiIndex, CommonUtils.queryLimit, urlGetMovies);
        }
    }

    public static void onQueryChange(String newText){
        hindiMovieList = new ArrayList<>();
        isHindiMoviesAvailable = true;

        if(!newText.equals("")) {
            isSearching = true;
            isScrolling = false;

            searchHindiMovieIndex = 0;

            if(newText.startsWith("SingerId : ")){
                CommonUtils.isSearching = true;
                searchQuery = newText.substring(11);
                getSingerMovies(searchQuery, searchHindiMovieIndex, urlSingerMovies);
            }else if(newText.startsWith("ComposerId : ")) {
                CommonUtils.isSearching = true;
                searchQuery = newText.substring(13);
                getComposerMovies(searchQuery, searchHindiMovieIndex, urlComposerMovies);
            }else {
                CommonUtils.isSearching = false;
                searchQuery = newText;
                getHindiMovies(searchQuery, searchHindiMovieIndex, CommonUtils.queryLimit, urlSearchMovies);
            }

        }else {
            isSearching = false;
            searchQuery = "";
            isScrolling = true;
            CommonUtils.isSearching = false;

            hindiIndex = 0;

            getHindiMovies(searchQuery, hindiIndex, CommonUtils.queryLimit, urlGetMovies);
        }
    }

    public static void getHindiMovies(String searchQuery, int index, int limit, String url){
        jsonObject = new JsonObject();
        jsonObject.addProperty("languageId", languageId);

        if(!searchQuery.equals(""))
            jsonObject.addProperty("movieName", searchQuery);

        jsonObject.addProperty("index", index);
        jsonObject.addProperty("limit", limit);

        PostHindiMovies postHindiMovies = new PostHindiMovies(context, url, index);
        postHindiMovies.checkServerAvailability(2);
    }

    public static void getSingerMovies(String searchQuery, int index, String url){
        jsonObject = new JsonObject();
        jsonObject.addProperty("singerId", searchQuery);
        jsonObject.addProperty("languageId", languageId);

        PostHindiMovies postHindiMovies = new PostHindiMovies(context, url, index);
        postHindiMovies.checkServerAvailability(2);
    }

    public static void getComposerMovies(String searchQuery, int index, String url){
        jsonObject = new JsonObject();
        jsonObject.addProperty("composerId", searchQuery);
        jsonObject.addProperty("languageId", languageId);

        PostHindiMovies postHindiMovies = new PostHindiMovies(context, url, index);
        postHindiMovies.checkServerAvailability(2);
    }

    public static class PostHindiMovies extends PostRequest{
        String url;
        int index;

        public PostHindiMovies(Context context, String url, int index){
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
            if(index == 0) hindiMovieList = new ArrayList<>();

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

                            hindiMovieList.add(item);

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                    populateHindiMovies();

                }else if(isScrolling){
                    Toast.makeText(context, "No More Data", Toast.LENGTH_SHORT).show();
                    isHindiMoviesAvailable = false;
                }else if(isSearching){
                    Toast.makeText(context, "No Match Found", Toast.LENGTH_SHORT).show();
                    isHindiMoviesAvailable = false;
                    populateHindiMovies();
                }

            }catch(JSONException e){
                e.printStackTrace();
            }
        }

        public void populateHindiMovies(){
            hindiMovieRecyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            linearLayoutManager.scrollToPosition(index);
            MovieAdapter hindiMovieAdapter = new MovieAdapter(new ArrayList<>(hindiMovieList));
            hindiMovieRecyclerView.setLayoutManager(linearLayoutManager);
            hindiMovieRecyclerView.setAdapter(null);
            hindiMovieRecyclerView.setAdapter(hindiMovieAdapter);

            hindiMovieAdapter.setOnItemClickListener(position -> {
                CommonUtils.isSearching = false;
                LibraryFragment.viewPager1.setCurrentItem(0);
                LibraryFragment.searchView.setQuery("MovieId : " + hindiMovieAdapter.getData().get(position).getMovieId(), true);
            });
        }
    }

}
