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

public class ComposersFragment extends Fragment {
    public static Context context;

    public static RecyclerView composerRecyclerView;
    public static ArrayList<ComposerItems> composerList;
    public static ComposerAdapter composerAdapter;

    public static String urlGetComposers = CommonUtils.IP + "/PMG/pmg_android/search/getComposers.php";
    public static String urlSearchComposers = CommonUtils.IP + "/PMG/pmg_android/search/searchComposers.php";

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
        View view = inflater.inflate(R.layout.fragment_composers, container, false);

        composerRecyclerView = view.findViewById(R.id.composerRecyclerView);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getContext();

        composerRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(!recyclerView.canScrollVertically(1)){
                    if(isSearching){
                        searchLowerLimit = searchUpperLimit;
                        searchUpperLimit = searchUpperLimit + CommonUtils.queryLimit;

                        getComposers(searchQuery, searchLowerLimit, searchUpperLimit, urlSearchComposers);
                    }else{
                        lowerLimit = upperLimit;
                        upperLimit = upperLimit + CommonUtils.queryLimit;

                        getComposers("", lowerLimit, upperLimit, urlGetComposers);
                    }
                }
            }
        });

        composerList = new ArrayList<>();

        lowerLimit = 0;
        upperLimit = CommonUtils.queryLimit;

        getComposers("", lowerLimit, upperLimit, urlGetComposers);

    }

    public static void onQuerySubmit(String query){
        composerList = new ArrayList<>();

        if(!query.equals("")) {
            isSearching = true;
            searchQuery = query;

            searchLowerLimit = 0;
            searchUpperLimit = CommonUtils.queryLimit;

            getComposers(searchQuery, searchLowerLimit, searchUpperLimit, urlSearchComposers);
        }else {
            isSearching = false;
            searchQuery = "";

            lowerLimit = 0;
            upperLimit = CommonUtils.queryLimit;

            getComposers(searchQuery, lowerLimit, upperLimit, urlGetComposers);
        }
    }

    public static void onQueryChange(String newText){
        composerList = new ArrayList<>();

        if(!newText.equals("")) {
            isSearching = true;
            searchQuery = newText;

            searchLowerLimit = 0;
            searchUpperLimit = CommonUtils.queryLimit;

            getComposers(searchQuery, searchLowerLimit, searchUpperLimit, urlSearchComposers);
        }else {
            isSearching = false;
            searchQuery = "";

            lowerLimit = 0;
            upperLimit = CommonUtils.queryLimit;

            getComposers(searchQuery, lowerLimit, upperLimit, urlGetComposers);
        }
    }

    public static void getComposers(String searchQuery, int lowerLimit, int upperLimit, String url){
        jsonObject = new JsonObject();
        jsonObject.addProperty("composerName", searchQuery);
        jsonObject.addProperty("lowerLimit", lowerLimit);
        jsonObject.addProperty("upperLimit", upperLimit);

        PostComposers postComposers = new PostComposers(context, url);
        postComposers.checkServerAvailability(2);
    }

    public static class PostComposers extends PostRequest{
        String url;

        public PostComposers(Context context, String url){
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
                    JSONArray composers = jsonObject.getJSONArray("composers");

                    for(int i = 0; i < composers.length(); i++){
                        JSONArray composer = composers.getJSONArray(i);

                        int composerId = 0;
                        String composerTitle = " ";
                        int numberOfSongs = 0;
                        int numberOfMovies = 0;

                        try {
                            if(!composer.getString(0).equals("")) composerId = composer.getInt(0);
                            if(!composer.getString(1).equals("")) composerTitle = composer.getString(1);
                            if(!composer.getString(2).equals("")) numberOfSongs = composer.getInt(2);
                            if(!composer.getString(3).equals("")) numberOfMovies = composer.getInt(3);

                            ComposerItems item = new ComposerItems();

                            item.setComposerId(composerId);
                            item.setComposerTitle(composerTitle);
                            item.setNumberOfSongs(numberOfSongs);
                            item.setNumberOfMovies(numberOfMovies);

                            composerList.add(item);

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                    composerRecyclerView.setHasFixedSize(true);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                    composerAdapter = new ComposerAdapter(composerList);
                    composerRecyclerView.setLayoutManager(linearLayoutManager);
                    composerRecyclerView.setAdapter(null);
                    composerRecyclerView.setAdapter(composerAdapter);
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
