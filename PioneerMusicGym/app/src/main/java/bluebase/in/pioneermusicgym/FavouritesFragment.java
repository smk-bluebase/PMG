package bluebase.in.pioneermusicgym;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FavouritesFragment extends Fragment {
    Context context;

    RecyclerView favouritesRecyclerView;
    ArrayList<SongItems> favouritesList;
    SongAdapter favouritesAdapter;

    RelativeLayout favouritesRelativeLayout;
    GetFavouriteSongs getFavouriteSongs;

    boolean isSearch = false;
    String queryString = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        height = (int) (height / 1.53);

        ImageView background = view.findViewById(R.id.background);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 200, height);
        background.setLayoutParams(layoutParams);

        favouritesRelativeLayout = view.findViewById(R.id.favouritesRelativeLayout);

        favouritesRecyclerView = view.findViewById(R.id.favouritesRecyclerView);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getContext();

        favouritesList = new ArrayList<>();

        CommonUtils.startDatabaseHelper(context);

        int songIds[] = CommonUtils.dataBaseHelper.selectFavourites();

        CommonUtils.closeDataBaseHelper();

        getFavouriteSongs = new GetFavouriteSongs(context, songIds);
        getFavouriteSongs.checkServerAvailability(2);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchViewItem = menu.findItem(R.id.search);

        final android.widget.SearchView searchView = (android.widget.SearchView) MenuItemCompat.getActionView(searchViewItem);

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setIconified(false);
        searchView.setQueryHint("Search Favourites");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();

                isSearch = true;
                queryString = query;

                boolean contains = false;
                for (SongItems item : favouritesList){
                    if(item.getSongTitle().equals(query)){
                        contains = true;
                        break;
                    }
                }

                if(contains){
                    favouritesAdapter.getFilter().filter(query);
                }else{
                    Toast.makeText(context, "No Match found",Toast.LENGTH_LONG).show();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                isSearch = true;
                queryString = newText;
                favouritesAdapter.getFilter().filter(newText);
                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchViewItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                favouritesAdapter.getFilter().filter("");
                isSearch = false;
                queryString = "";
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private class GetFavouriteSongs extends GetSongs{
        int[] songIds;

        public GetFavouriteSongs(Context context, int[] songIds) {
            super(context);
            this.songIds = songIds;
        }

        @Override
        public void serverAvailability(boolean isServerAvailable) {
            if(!isServerAvailable){
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
            }else {
                getFavouriteSongs.getSongDetails(songIds);
            }
        }

        @Override
        public void onPostUpdate() {
            populateFavourites();

            enableSwipeToDeleteAndUndo();
        }
    }

    private void populateFavourites(){
        CommonUtils.startDatabaseHelper(context);
        favouritesList = new ArrayList<>();

        JSONArray jsonArray = CommonUtils.dataBaseHelper.selectSongMaster();

        for(int i = 0; i < jsonArray.length(); i++){
            try{
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                SongItems item = new SongItems();

                item.setSongId(jsonObject.getInt("songId"));
                item.setSongTitle(jsonObject.getString("title"));
                item.setMovieName(jsonObject.getString("movieName"));
                item.setMovieSinger(jsonObject.getString("movieSinger"));
                item.setYear(jsonObject.getString("year"));
                item.setDuration(jsonObject.getString("duration"));

                favouritesList.add(item);

            }catch(JSONException e){
                e.printStackTrace();
            }
        }

        CommonUtils.closeDataBaseHelper();

        favouritesRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        favouritesAdapter = new SongAdapter(new ArrayList<>(favouritesList));
        favouritesRecyclerView.setLayoutManager(linearLayoutManager);
        favouritesRecyclerView.setAdapter(null);
        favouritesRecyclerView.setAdapter(favouritesAdapter);

        favouritesAdapter.setOnItemClickListener(position -> {
            CommonUtils.fromNotification = false;
            MusicPlayerFragment.stopPlayer(MusicPlayerFragment.musicPlayerIntent);

            CommonUtils.songId = favouritesAdapter.getData().get(position).getSongId();

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

    private void positionFavourites(int position, int originalPosition){
        if(isSearch){
            favouritesRecyclerView.scrollToPosition(position);
            favouritesAdapter.getFilter().filter(queryString);
        }else {
            favouritesRecyclerView.getLayoutManager().scrollToPosition(originalPosition - 1);
        }
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(context) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                int position = viewHolder.getAdapterPosition();
                SongItems item = favouritesAdapter.getData().get(position);
                int originalPosition = favouritesAdapter.getPosition(item);

                try {
                    CommonUtils.startDatabaseHelper(context);
                    CommonUtils.dataBaseHelper.deleteFromSongMaster(item.getSongId());
                    CommonUtils.dataBaseHelper.deleteFromFavourites(item.getSongId());
                    CommonUtils.closeDataBaseHelper();

                    populateFavourites();

                    positionFavourites(position, originalPosition);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Snackbar snackbar = Snackbar.make(favouritesRecyclerView, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", view -> {
                    try {
                        CommonUtils.startDatabaseHelper(context);
                        CommonUtils.dataBaseHelper.insertSongMaster(item.getSongId(), item.getSongTitle(), item.getMovieName(), item.getMovieSinger(), item.getYear(), item.getDuration());
                        CommonUtils.dataBaseHelper.insertIntoFavourites(originalPosition, item.getSongId());
                        CommonUtils.closeDataBaseHelper();

                        populateFavourites();

                        positionFavourites(position, originalPosition);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(favouritesRecyclerView);
    }

}
