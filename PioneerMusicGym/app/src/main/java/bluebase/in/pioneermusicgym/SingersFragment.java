package bluebase.in.pioneermusicgym;

import android.content.Context;
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

public class SingersFragment extends Fragment {
    public static Context context;

    public static RecyclerView singerRecyclerView;
    public static ArrayList<SingerItems> singerList;
    public static SingerAdapter singerAdapter;

    public static String urlGetSingers = CommonUtils.IP + "/PMG/pmg_android/search/getSingers.php";
    public static String urlSearchSingers = CommonUtils.IP + "/PMG/pmg_android/search/searchSingers.php";

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
        View view = inflater.inflate(R.layout.fragment_singers, container, false);

        singerRecyclerView = view.findViewById(R.id.singerRecyclerView);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getContext();

        singerRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(!recyclerView.canScrollVertically(1)){
                    if(isSearching){
                        searchLowerLimit = searchUpperLimit;
                        searchUpperLimit = searchUpperLimit + CommonUtils.queryLimit;

                        getSingers(searchQuery, searchLowerLimit, searchUpperLimit, urlSearchSingers);
                    }else{
                        lowerLimit = upperLimit;
                        upperLimit = upperLimit + CommonUtils.queryLimit;

                        getSingers("", lowerLimit, upperLimit, urlGetSingers);
                    }
                }
            }
        });

        singerList = new ArrayList<>();

        lowerLimit = 0;
        upperLimit = CommonUtils.queryLimit;

        getSingers("", lowerLimit, upperLimit, urlGetSingers);

    }

    public static void onQuerySubmit(String query){
        singerList = new ArrayList<>();

        if(!query.equals("")) {
            isSearching = true;
            searchQuery = query;

            searchLowerLimit = 0;
            searchUpperLimit = CommonUtils.queryLimit;

            getSingers(searchQuery, searchLowerLimit, searchUpperLimit, urlSearchSingers);
        }else {
            isSearching = false;
            searchQuery = "";

            lowerLimit = 0;
            upperLimit = CommonUtils.queryLimit;

            getSingers(searchQuery, lowerLimit, upperLimit, urlGetSingers);
        }
    }

    public static void onQueryChange(String newText){
        singerList = new ArrayList<>();

        if(!newText.equals("")) {
            isSearching = true;
            searchQuery = newText;

            searchLowerLimit = 0;
            searchUpperLimit = CommonUtils.queryLimit;

            getSingers(searchQuery, searchLowerLimit, searchUpperLimit, urlSearchSingers);
        }else {
            isSearching = false;
            searchQuery = "";

            lowerLimit = 0;
            upperLimit = CommonUtils.queryLimit;

            getSingers(searchQuery, lowerLimit, upperLimit, urlGetSingers);
        }
    }

    public static void getSingers(String searchQuery, int lowerLimit, int upperLimit, String url){
        jsonObject = new JsonObject();
        jsonObject.addProperty("singerName", searchQuery);
        jsonObject.addProperty("lowerLimit", lowerLimit);
        jsonObject.addProperty("upperLimit", upperLimit);

        PostSingers postSingers = new PostSingers(context, url);
        postSingers.checkServerAvailability(2);
    }

    public static class PostSingers extends PostRequest{
        String url;

        public PostSingers(Context context, String url){
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
                    JSONArray singers = jsonObject.getJSONArray("singers");

                    for(int i = 0; i < singers.length(); i++){
                        JSONArray singer = singers.getJSONArray(i);

                        int singerId = 0;
                        String singerTitle = " ";
                        int numberOfSongs = 0;
                        int numberOfMovies = 0;

                        try {
                            if(!singer.getString(0).equals("")) singerId = singer.getInt(0);
                            if(!singer.getString(1).equals("")) singerTitle = singer.getString(1);
                            if(!singer.getString(2).equals("")) numberOfSongs = singer.getInt(2);
                            if(!singer.getString(3).equals("")) numberOfMovies = singer.getInt(3);

                            SingerItems item = new SingerItems();

                            item.setSingerId(singerId);
                            item.setSingerTitle(singerTitle);
                            item.setNumberOfSongs(numberOfSongs);
                            item.setNumberOfMovies(numberOfMovies);

                            singerList.add(item);

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                    singerRecyclerView.setHasFixedSize(true);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                    singerAdapter = new SingerAdapter(singerList);
                    singerRecyclerView.setLayoutManager(linearLayoutManager);
                    singerRecyclerView.setAdapter(null);
                    singerRecyclerView.setAdapter(singerAdapter);
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
