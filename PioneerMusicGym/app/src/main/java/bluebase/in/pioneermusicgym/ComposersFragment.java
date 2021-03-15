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

    public static String urlGetComposers = CommonUtils.IP + "/pmg_android/search/getComposers.php";
    public static String urlSearchComposers = CommonUtils.IP + "/pmg_android/search/searchComposers.php";

    public static JsonObject jsonObject;

    public static int composerIndex;
    public static int searchComposerIndex;

    public static boolean isSearching = false;
    public static String searchQuery = "";
    public static boolean isScrolling = false;
    public static boolean isComposerAvailable = true;

    public static boolean isLoaded = false;

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

        isLoaded = true;

        if(CommonUtils.isHomeSearching) ArtistsFragment.onLoaded(1);
    }

    public static void onOpen(){
        if(isLoaded) {
            LibraryFragment.searchView.setQuery("", false);

            composerRecyclerView.clearOnScrollListeners();

            composerRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (!recyclerView.canScrollVertically(1) && isComposerAvailable) {
                        if (isSearching) {
                            searchComposerIndex = searchComposerIndex + CommonUtils.queryLimit;
                            getComposers(searchQuery, searchComposerIndex, CommonUtils.queryLimit, urlSearchComposers);
                        } else {
                            composerIndex = composerIndex + CommonUtils.queryLimit;
                            getComposers("", composerIndex, CommonUtils.queryLimit, urlGetComposers);
                        }

                        isScrolling = true;
                    }
                }
            });

            composerIndex = 0;
            searchComposerIndex = 0;

            isSearching = false;
            searchQuery = "";
            isScrolling = false;
            isComposerAvailable = true;

            composerList = new ArrayList<>();

            getComposers("", composerIndex, CommonUtils.queryLimit, urlGetComposers);
        }
    }

    public static void onQuerySubmit(String query){
        composerList = new ArrayList<>();
        isComposerAvailable = true;

        if(!query.equals("")) {
            isSearching = true;
            searchQuery = query;
            isScrolling = false;

            searchComposerIndex = 0;

            getComposers(searchQuery, searchComposerIndex, CommonUtils.queryLimit, urlSearchComposers);
        }else {
            isSearching = false;
            searchQuery = "";
            isScrolling = true;

            composerIndex = 0;

            getComposers(searchQuery, composerIndex, CommonUtils.queryLimit, urlGetComposers);
        }
    }

    public static void onQueryChange(String newText){
        composerList = new ArrayList<>();
        isComposerAvailable = true;

        if(!newText.equals("")) {
            isSearching = true;
            searchQuery = newText;
            isScrolling = false;

            searchComposerIndex = 0;

            getComposers(searchQuery, searchComposerIndex, CommonUtils.queryLimit, urlSearchComposers);
        }else {
            isSearching = false;
            searchQuery = "";
            isScrolling = true;

            composerIndex = 0;

            getComposers(searchQuery, composerIndex, CommonUtils.queryLimit, urlGetComposers);
        }
    }

    public static void getComposers(String searchQuery, int index, int limit, String url){
        jsonObject = new JsonObject();
        jsonObject.addProperty("composerName", searchQuery);
        jsonObject.addProperty("index", index);
        jsonObject.addProperty("limit", limit);

        PostComposers postComposers = new PostComposers(context, url, index);
        postComposers.checkServerAvailability(2);
    }

    public static class PostComposers extends PostRequest{
        String url;
        int index;

        public PostComposers(Context context, String url, int index){
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
            if(index == 0) composerList = new ArrayList<>();

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

                    populateComposers();

                }else if(isScrolling){
                    Toast.makeText(context, "No More Data", Toast.LENGTH_SHORT).show();
                    isComposerAvailable = false;
                }else if(isSearching){
                    Toast.makeText(context, "No Match Found", Toast.LENGTH_SHORT).show();
                    isComposerAvailable = false;
                    populateComposers();
                }

            }catch(JSONException e){
                e.printStackTrace();
            }
        }

        public void populateComposers(){
            composerRecyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            linearLayoutManager.scrollToPosition(index);
            composerAdapter = new ComposerAdapter(new ArrayList<>(composerList));
            composerRecyclerView.setLayoutManager(linearLayoutManager);
            composerRecyclerView.setAdapter(null);
            composerRecyclerView.setAdapter(composerAdapter);

            composerAdapter.setOnItemClickListener(position -> {
                CommonUtils.isSearching = true;
                LibraryFragment.viewPager1.setCurrentItem(2);
                LibraryFragment.searchView.setQuery("ComposerId : " + composerAdapter.getData().get(position).getComposerId(), true);
            });
        }
    }

}
