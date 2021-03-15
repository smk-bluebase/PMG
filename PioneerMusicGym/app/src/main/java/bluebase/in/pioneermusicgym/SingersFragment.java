package bluebase.in.pioneermusicgym;

import android.app.ProgressDialog;
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

import java.lang.reflect.Array;
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

    public static String urlGetSingers = CommonUtils.IP + "/pmg_android/search/getSingers.php";
    public static String urlSearchSingers = CommonUtils.IP + "/pmg_android/search/searchSingers.php";

    public static JsonObject jsonObject;

    public static int singerIndex;
    public static int searchSingerIndex;

    public static boolean isSearching = false;
    public static String searchQuery = "";
    public static boolean isScrolling = false;
    public static boolean isSingerAvailable = true;

    public static boolean isLoaded = false;

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

        isLoaded = true;

        if(CommonUtils.isHomeSearching) ArtistsFragment.onLoaded(0);
    }

    public static void onOpen(){
         if(isLoaded) {
            LibraryFragment.searchView.setQuery("", false);

            singerRecyclerView.clearOnScrollListeners();

            singerRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (!recyclerView.canScrollVertically(1) && isSingerAvailable) {
                        if (isSearching) {
                            searchSingerIndex = searchSingerIndex + CommonUtils.queryLimit;
                            getSingers(searchQuery, searchSingerIndex, CommonUtils.queryLimit, urlSearchSingers);
                        } else {
                            singerIndex = singerIndex + CommonUtils.queryLimit;
                            getSingers("", singerIndex, CommonUtils.queryLimit, urlGetSingers);
                        }

                        isScrolling = true;
                    }
                }
            });

            singerIndex = 0;
            searchSingerIndex = 0;

            isSearching = false;
            searchQuery = "";
            isScrolling = false;
            isSingerAvailable = true;

            singerList = new ArrayList<>();

            getSingers("", singerIndex, CommonUtils.queryLimit, urlGetSingers);
        }
    }

    public static void onQuerySubmit(String query){
        singerList = new ArrayList<>();
        isSingerAvailable = true;

        if(!query.equals("")) {
            isSearching = true;
            searchQuery = query;
            isScrolling = false;

            searchSingerIndex = 0;

            getSingers(searchQuery, searchSingerIndex, CommonUtils.queryLimit, urlSearchSingers);
        }else {
            isSearching = false;
            searchQuery = "";
            isScrolling = true;

            singerIndex = 0;

            getSingers(searchQuery, singerIndex, CommonUtils.queryLimit, urlGetSingers);
        }
    }

    public static void onQueryChange(String newText){
        singerList = new ArrayList<>();
        isSingerAvailable = true;

        if(!newText.equals("")) {
            isSearching = true;
            searchQuery = newText;
            isScrolling = false;

            searchSingerIndex = 0;

            getSingers(searchQuery, searchSingerIndex, CommonUtils.queryLimit, urlSearchSingers);
        }else {
            isSearching = false;
            searchQuery = "";
            isScrolling = true;

            singerIndex = 0;

            getSingers(searchQuery, singerIndex, CommonUtils.queryLimit, urlGetSingers);
        }
    }

    public static void getSingers(String searchQuery, int index, int limit, String url){
        jsonObject = new JsonObject();
        jsonObject.addProperty("singerName", searchQuery);
        jsonObject.addProperty("index", index);
        jsonObject.addProperty("limit", limit);

        PostSingers postSingers = new PostSingers(context, url, index);
        postSingers.checkServerAvailability(2);
    }

    public static class PostSingers extends PostRequest{
        String url;
        int index;

        public PostSingers(Context context, String url, int index){
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
            if(index == 0) singerList = new ArrayList<>();

            try{
                JSONObject jsonObject1 =  jsonArray.getJSONObject(0);

                if(jsonObject1.getBoolean("status")){
                    JSONArray singers = jsonObject1.getJSONArray("singers");

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

                    populateSingers();

                }else if(isScrolling){
                    Toast.makeText(context, "No More Data", Toast.LENGTH_SHORT).show();
                    isSingerAvailable = false;
                }else if(isSearching){
                    Toast.makeText(context, "No Match Found", Toast.LENGTH_SHORT).show();
                    isSingerAvailable = false;
                    populateSingers();
                }

            }catch(JSONException e){
                e.printStackTrace();
            }
        }

        public void populateSingers(){
            singerRecyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            linearLayoutManager.scrollToPosition(index);
            singerAdapter = new SingerAdapter(new ArrayList<>(singerList));
            singerRecyclerView.setLayoutManager(linearLayoutManager);
            singerRecyclerView.setAdapter(null);
            singerRecyclerView.setAdapter(singerAdapter);

            singerAdapter.setOnItemClickListener(position -> {
                CommonUtils.isSearching = true;
                LibraryFragment.viewPager1.setCurrentItem(2);
                LibraryFragment.searchView.setQuery("SingerId : " + singerAdapter.getData().get(position).getSingerId(), true);
            });
        }
    }

}
