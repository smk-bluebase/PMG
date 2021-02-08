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
    public static MovieAdapter movieAdapter;

    public static String urlGetMovies = CommonUtils.IP + "/PMG/pmg_android/search/getMovies.php";
    public static String urlSearchMovies = CommonUtils.IP + "/PMG/pmg_android/search/searchMovies.php";

    public static JsonObject jsonObject;

    public static int lowerLimit;
    public static int upperLimit;
    public static int searchLowerLimit;
    public static int searchUpperLimit;

    public static boolean isSearching = false;
    public static String searchQuery = "";

    public static int languageId = 3;

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

        hindiMovieRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(!recyclerView.canScrollVertically(1)){
                    if(isSearching){
                        searchLowerLimit = searchUpperLimit;
                        searchUpperLimit = searchUpperLimit + CommonUtils.queryLimit;

                        getHindiMovies(searchQuery, searchLowerLimit, searchUpperLimit, urlSearchMovies);
                    }else{
                        lowerLimit = upperLimit;
                        upperLimit = upperLimit + CommonUtils.queryLimit;

                        getHindiMovies("", lowerLimit, upperLimit, urlGetMovies);
                    }
                }
            }
        });

        hindiMovieList = new ArrayList<>();

        lowerLimit = 0;
        upperLimit = CommonUtils.queryLimit;

        getHindiMovies("", lowerLimit, upperLimit, urlGetMovies);

    }

    public static void onQuerySubmit(String query){
        hindiMovieList = new ArrayList<>();

        if(!query.equals("")) {
            isSearching = true;
            searchQuery = query;

            searchLowerLimit = 0;
            searchUpperLimit = CommonUtils.queryLimit;

            getHindiMovies(searchQuery, searchLowerLimit, searchUpperLimit, urlSearchMovies);
        }else {
            isSearching = false;
            searchQuery = "";

            lowerLimit = 0;
            upperLimit = CommonUtils.queryLimit;

            getHindiMovies(searchQuery, lowerLimit, upperLimit, urlGetMovies);
        }
    }

    public static void onQueryChange(String newText){
        hindiMovieList = new ArrayList<>();

        if(!newText.equals("")) {
            isSearching = true;
            searchQuery = newText;

            searchLowerLimit = 0;
            searchUpperLimit = CommonUtils.queryLimit;

            getHindiMovies(searchQuery, searchLowerLimit, searchUpperLimit, urlSearchMovies);
        }else {
            isSearching = false;
            searchQuery = "";

            lowerLimit = 0;
            upperLimit = CommonUtils.queryLimit;

            getHindiMovies(searchQuery, lowerLimit, upperLimit, urlGetMovies);
        }
    }

    public static void getHindiMovies(String searchQuery, int lowerLimit, int upperLimit, String url){
        jsonObject = new JsonObject();
        jsonObject.addProperty("languageId", languageId);

        if(!searchQuery.equals(""))
            jsonObject.addProperty("movieName", searchQuery);

        jsonObject.addProperty("lowerLimit", lowerLimit);
        jsonObject.addProperty("upperLimit", upperLimit);

        PostHindiMovies postHindiMovies = new PostHindiMovies(context, url);
        postHindiMovies.checkServerAvailability(2);
    }

    public static class PostHindiMovies extends PostRequest{
        String url;

        public PostHindiMovies(Context context, String url){
            super(context);
            this.url = url;
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

                    hindiMovieRecyclerView.setHasFixedSize(true);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                    movieAdapter = new MovieAdapter(hindiMovieList);
                    hindiMovieRecyclerView.setLayoutManager(linearLayoutManager);
                    hindiMovieRecyclerView.setAdapter(null);
                    hindiMovieRecyclerView.setAdapter(movieAdapter);
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
