package bluebase.in.pioneermusicgym;

import android.app.SearchManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeFragment extends Fragment {
    public static Context context;

    public static int[] colors;
    UpdateLocal updateLocal;

    SearchView searchHomeSearchView;

    public static RecyclerView singerRecyclerView;
    public static RecyclerView composerRecyclerView;
    public static RecyclerView movieRecyclerView;
    public static RecyclerView playlistRecyclerView;

    public static ArrayList<HomeItems> singerList = new ArrayList<>();
    public static ArrayList<HomeItems> composerList = new ArrayList<>();
    public static ArrayList<HomeItems> movieList = new ArrayList<>();
    public static ArrayList<HomeItems> playlistList = new ArrayList<>();

    int singerIndex = 0;
    int composerIndex = 0;
    int movieIndex = 0;

    String urlSearchAll = CommonUtils.IP + "/pmg_android/search/searchAll.php";

    JsonObject jsonObject;

    String[] columns = new String[]{ BaseColumns._ID, "searchHome", "searchCategory" };

    MatrixCursor cursor = new MatrixCursor(columns);

    List<SearchItem> searchItemList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        height = (int) (height / 1.53);

        ImageView background = view.findViewById(R.id.background);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 200, height);
        background.setLayoutParams(layoutParams);

        searchHomeSearchView = view.findViewById(R.id.searchHomeSearchView);

        singerRecyclerView = view.findViewById(R.id.singerRecyclerView);
        composerRecyclerView = view.findViewById(R.id.composerRecyclerView);
        movieRecyclerView = view.findViewById(R.id.movieRecyclerView);
        playlistRecyclerView = view.findViewById(R.id.playlistRecyclerView);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getContext();

        CommonUtils.isHomeSearching = false;
        CommonUtils.searchQuery = "";

        TypedArray typedArray = context.getResources().obtainTypedArray(R.array.homeColor);
        colors = new int[typedArray.length()];
        for (int i = 0; i < typedArray.length(); i++) {
            colors[i] = typedArray.getColor(i, 0);
        }
        typedArray.recycle();

        SearchManager searchManager = (SearchManager) context.getSystemService(Context.SEARCH_SERVICE);

        searchHomeSearchView.setSearchableInfo(searchManager.getSearchableInfo(((MainActivity) context).getComponentName()));

        searchHomeSearchView.setSuggestionsAdapter(new SearchSuggestionAdapter(context, cursor));

        searchHomeSearchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchHomeSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.length() > 1) getHomeSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length() > 1) getHomeSearch(newText);
                return false;
            }
        });

        searchHomeSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionClick(int position) {
                CommonUtils.isHomeSearching = true;

                String searchHome = cursor.getString(cursor.getColumnIndex("searchHome"));
                String searchCategory = cursor.getString(cursor.getColumnIndex("searchCategory"));

                if(searchCategory.equals("singers")){
                    for(SearchItem item : searchItemList){
                        if(item.getSearchResult() == searchHome)
                            CommonUtils.searchQuery = "SingerName : " + item.getSearchResult();
                    }
                }else if(searchCategory.equals("composers")){
                    for(SearchItem item : searchItemList){
                        if(item.getSearchResult() == searchHome){
                            CommonUtils.searchQuery = "ComposerName : " + item.getSearchResult();
                        }
                    }
                }else if(searchCategory.equals("movies")){
                    for(SearchItem item : searchItemList){
                        if(item.getSearchResult() == searchHome)
                            CommonUtils.searchQuery = "MovieId : " + item.getId();
                    }
                }

                moveToLibrary();

                return false;
            }

            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }
        });

        updateLocal = new UpdateLocal(context);
        updateLocal.checkServerAvailability(2);

    }

    private class UpdateLocal extends UpdateLocalDatabase{
        public UpdateLocal(Context context){
            super(context);
        }

        @Override
        public void serverAvailability(boolean isServerAvailable) {
            if(!isServerAvailable){
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
            }else {
                updateLocal.updateHome();
            }
        }

        @Override
        public void onPostUpdate() {
            CommonUtils.startDatabaseHelper(context);
            populateSingers(0);
            populateComposers(0);
            populateMovies(0);
            populatePlaylists();
            if(CommonUtils.dataBaseHelper.getSettingsCount() == 0) CommonUtils.dataBaseHelper.insertSettings();
            CommonUtils.closeDataBaseHelper();

            singerRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if(!recyclerView.canScrollHorizontally(1) && UpdateLocalDatabase.isSingerAvailable){
                        singerIndex = singerIndex + CommonUtils.homeQueryLimit;

                        UpdateLocalDatabase.isSingerAvailable = false;
                        UpdateLocalDatabase.updateSingers(singerIndex, CommonUtils.homeQueryLimit);
                    }
                }
            });

            composerRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if(!recyclerView.canScrollHorizontally(1) && UpdateLocalDatabase.isComposerAvailable){
                        composerIndex = composerIndex + CommonUtils.homeQueryLimit;

                        UpdateLocalDatabase.isComposerAvailable = false;
                        UpdateLocalDatabase.updateComposers(composerIndex, CommonUtils.homeQueryLimit);
                    }
                }
            });

            movieRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if(!recyclerView.canScrollHorizontally(1) && UpdateLocalDatabase.isMovieAvailable){
                        movieIndex = movieIndex + CommonUtils.homeQueryLimit;

                        UpdateLocalDatabase.isMovieAvailable = false;
                        UpdateLocalDatabase.updateMovies(movieIndex, CommonUtils.homeQueryLimit);
                    }
                }
            });

            GetNotification.getNewNotification(context);

        }

    }

    public static void moveToLibrary(){
        ((MainActivity) context).getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        ((MainActivity) context).getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack("libraryFragment")
                .replace(R.id.fragment_container, new LibraryFragment(), "libraryFragment")
                .commit();
        CommonUtils.openTab = 4;
        MainActivity.navigationView.setCheckedItem(R.id.nav_library);
    }

    public static void populateSingers(int index){
        singerList = new ArrayList<>();

        int j = 0;
        boolean isReverse = false;

        JSONArray jsonArray = CommonUtils.dataBaseHelper.selectSingers();

        for(int i = 0; i < jsonArray.length(); i++){
            try {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                HomeItems item = new HomeItems();
                item.setId(jsonObject.getInt("singerId"));
                item.setTitle(jsonObject.getString("singerName"));

                if(jsonObject.getInt("numberOfSongs") != 1) item.setSubMenu1(jsonObject.getInt("numberOfSongs") + " Songs");
                else item.setSubMenu1("1 Song");

                if(jsonObject.getInt("numberOfMovies") != 1) item.setSubmenu2(jsonObject.getInt("numberOfMovies") + " Movies");
                else item.setSubmenu2("1 Movie");

                item.setBackgroundColor(colors[j]);

                if (j == 0) isReverse = false;
                else if (j == 4) isReverse = true;

                if (!isReverse) j++;
                else --j;

                singerList.add(item);

            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        singerRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        linearLayoutManager.scrollToPosition(index);
        HomeAdapter singerAdapter = new HomeAdapter(new ArrayList<>(singerList));
        singerRecyclerView.setLayoutManager(linearLayoutManager);
        singerRecyclerView.setAdapter(null);
        singerRecyclerView.setAdapter(singerAdapter);

        singerAdapter.setOnItemClickListener(position -> {
            CommonUtils.isHomeSearching = true;
            CommonUtils.searchQuery = "SingerName : " + singerAdapter.getData().get(position).getTitle();
            moveToLibrary();
        });
    }

    public static void populateComposers(int index){
        composerList = new ArrayList<>();

        int j = 0;
        boolean isReverse = false;

        JSONArray jsonArray = CommonUtils.dataBaseHelper.selectComposers();

        for(int i = 0; i < jsonArray.length(); i++){
            try {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                HomeItems item = new HomeItems();
                item.setId(jsonObject.getInt("composerId"));
                item.setTitle(jsonObject.getString("composerName"));

                if(jsonObject.getInt("numberOfSongs") != 1) item.setSubMenu1(jsonObject.getInt("numberOfSongs") + " Songs");
                else item.setSubMenu1("1 Song");

                if(jsonObject.getInt("numberOfMovies") != 1) item.setSubmenu2(jsonObject.getInt("numberOfMovies") + " Movies");
                else item.setSubmenu2("1 Movie");

                item.setBackgroundColor(colors[j]);

                if(j == 0) isReverse = false;
                else if (j == 4) isReverse = true;

                if(!isReverse) j++;
                else --j;

                composerList.add(item);

            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        composerRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        linearLayoutManager.scrollToPosition(index);
        HomeAdapter composerAdapter = new HomeAdapter(new ArrayList<>(composerList));
        composerRecyclerView.setLayoutManager(linearLayoutManager);
        composerRecyclerView.setAdapter(null);
        composerRecyclerView.setAdapter(composerAdapter);

        composerAdapter.setOnItemClickListener(position -> {
            CommonUtils.isHomeSearching = true;
            CommonUtils.searchQuery = "ComposerName : " + composerAdapter.getData().get(position).getTitle();
            moveToLibrary();
        });
    }

    public static void populateMovies(int index){
        movieList = new ArrayList<>();

        int j = 0;
        boolean isReverse = false;

        JSONArray jsonArray = CommonUtils.dataBaseHelper.selectMovies();

        for(int i = 0; i < jsonArray.length(); i++){
            try {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                HomeItems item = new HomeItems();
                item.setId(jsonObject.getInt("movieId"));
                item.setTitle(jsonObject.getString("movieName"));

                if(jsonObject.getInt("numberOfSongs") != 1) item.setSubMenu1(jsonObject.getInt("numberOfSongs") + " Songs");
                else item.setSubMenu1("1 Song");

                item.setSubmenu2(jsonObject.getString("year"));
                item.setBackgroundColor(colors[j]);

                if(j == 0) isReverse = false;
                else if (j == 4) isReverse = true;

                if(!isReverse) j++;
                else --j;

                movieList.add(item);

            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        movieRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        linearLayoutManager.scrollToPosition(index);
        HomeAdapter movieAdapter = new HomeAdapter(new ArrayList<>(movieList));
        movieRecyclerView.setLayoutManager(linearLayoutManager);
        movieRecyclerView.setAdapter(null);
        movieRecyclerView.setAdapter(movieAdapter);

        movieAdapter.setOnItemClickListener(position -> {
            CommonUtils.isHomeSearching = true;
            CommonUtils.searchQuery = "MovieId : " + movieAdapter.getData().get(position).getId();
            moveToLibrary();
        });
    }

    private void populatePlaylists(){
        playlistList = new ArrayList<>();

        int j = 0;
        boolean isReverse = false;

        JSONArray jsonArray = CommonUtils.dataBaseHelper.selectPlaylists();

        for(int i = 0; i < jsonArray.length(); i++){
            try {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                HomeItems item = new HomeItems();
                item.setId(jsonObject.getInt("playlistId"));
                item.setTitle(jsonObject.getString("playlistTitle"));

                int numberOfSongs = CommonUtils.dataBaseHelper.selectPlaylistSongsCount(jsonObject.getInt("playlistId"));
                if(numberOfSongs != 1) item.setSubMenu1(numberOfSongs + " Songs");
                else item.setSubMenu1("1 Song");

                try{
                    Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(jsonObject.getString("createdOn"));
                    item.setSubmenu2(new SimpleDateFormat("MMM, yyyy", Locale.ENGLISH).format(date));
                }catch(ParseException e){
                    e.printStackTrace();
                }

                item.setBackgroundColor(colors[j]);

                if(j == 0) isReverse = false;
                else if (j == 4) isReverse = true;

                if(!isReverse) j++;
                else --j;

                playlistList.add(item);

            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        playlistRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        HomeAdapter playlistAdapter = new HomeAdapter(new ArrayList<>(playlistList));
        playlistRecyclerView.setLayoutManager(linearLayoutManager);
        playlistRecyclerView.setAdapter(playlistAdapter);

        playlistAdapter.setOnItemClickListener(position -> {
            getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            MainActivity.navigationView.setCheckedItem(R.id.nav_playlists);

            CommonUtils.startDatabaseHelper(context);

            CommonUtils.playlistId = playlistAdapter.getData().get(position).getId();

            CommonUtils.songIds = CommonUtils.dataBaseHelper.selectPlaylistSongs(CommonUtils.playlistId);

            CommonUtils.closeDataBaseHelper();

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack("playlistSongFragment")
                    .replace(R.id.fragment_container, new PlaylistSongFragment(), "playlistSongFragment")
                    .commit();
        });
    }

    private void populateAdapter(){
        cursor = new MatrixCursor(columns);
        for (int i = 0; i < searchItemList.size(); i++) {
            cursor.addRow(new Object[]{i, searchItemList.get(i).getSearchResult(), searchItemList.get(i).getCategory()});
        }

        searchHomeSearchView.getSuggestionsAdapter().changeCursor(cursor);
        searchHomeSearchView.getSuggestionsAdapter().notifyDataSetChanged();
    }

    private void getHomeSearch(String query){
        jsonObject = new JsonObject();
        jsonObject.addProperty("search", query);

        PostHomeSearch postHomeSearch = new PostHomeSearch(context);
        postHomeSearch.checkServerAvailability(0);
    }

    private class PostHomeSearch extends PostRequest{
        public PostHomeSearch(Context context){
            super(context);
        }

        @Override
        public void serverAvailability(boolean isServerAvailable) {
            if(isServerAvailable){
                super.postRequest(urlSearchAll, jsonObject);
            }else {
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFinish(JSONArray jsonArray) {
            try{
                JSONObject jsonObject =  jsonArray.getJSONObject(0);
                searchItemList = new ArrayList<>();

                if(jsonObject.getBoolean("status")){

                    JSONArray searchResults = jsonObject.getJSONArray("searchResults");

                    for(int i = 0; i < searchResults.length(); i++){
                        JSONArray result = searchResults.getJSONArray(i);

                        SearchItem searchItem = new SearchItem();
                        searchItem.setSearchResult(result.getString(0));
                        searchItem.setId(result.getInt(1));
                        searchItem.setCategory(result.getString(2));

                        searchItemList.add(searchItem);
                    }

                }else {
                    Toast.makeText(context, "No Data", Toast.LENGTH_SHORT).show();
                }

                populateAdapter();

            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

}
