package bluebase.in.pioneermusicgym;

import android.content.Context;
import android.content.res.TypedArray;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FavouritesFragment extends Fragment {
    Context context;

    RecyclerView favouritesRecyclerView;
    ArrayList<FavouriteItems> favouritesList = new ArrayList<>();
    FavouriteAdapter favouriteAdapter;

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

        favouritesRecyclerView = view.findViewById(R.id.favouritesRecyclerView);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getContext();

        populateFavourites();

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

                if(favouriteAdapter.getItemCount() > 0) {
                    boolean contains = false;
                    for (FavouriteItems item : favouritesList) {
                        if (item.getSongTitle().equals(query)) {
                            contains = true;
                            break;
                        }
                    }

                    if (contains) {
                        favouriteAdapter.getFilter().filter(query);
                    } else {
                        Toast.makeText(context, "No Match found", Toast.LENGTH_LONG).show();
                    }
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(favouriteAdapter.getItemCount() > 0)
                    favouriteAdapter.getFilter().filter(newText);
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
//                favouriteAdapter.getFilter().filter("");
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void populateFavourites(){
//        CommonUtils.startDatabaseHelper(context);
//        favouritesList = new ArrayList<>();
//
//        JSONArray jsonArray = CommonUtils.dataBaseHelper.selectFavourites();
//
//        int j = 0;
//
//        for(int i = 0; i < jsonArray.length(); i++){
//            try{
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//
//                FavouriteItems item = new FavouriteItems();
//                item.setSongId(jsonObject.getInt("songId"));
//                item.setSongTitle(jsonObject.getString("songTitle"));
//                item.setAlbumName(jsonObject.getString("albumName"));
//                item.setMovieName(jsonObject.getString("movieName"));
//                item.setAlbumSinger(jsonObject.getString("albumSinger"));
//                item.setMovieSinger(jsonObject.getString("movieSinger"));
//                item.setYear(jsonObject.getString("year"));
//                item.setDuration(jsonObject.getString("duration"));
//                item.setBackgroundColor(colors[j]);
//
//                if (j == 0) j = 1;
//                else j = 0;
//
//                favouritesList.add(item);
//
//            }catch(JSONException e){
//                e.printStackTrace();
//            }
//        }
//
//        CommonUtils.closeDataBaseHelper();
//
//        favouritesRecyclerView.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
//        favouriteAdapter = new FavouriteAdapter(favouritesList);
//        favouritesRecyclerView.setLayoutManager(linearLayoutManager);
//        favouritesRecyclerView.setAdapter(null);
//        favouritesRecyclerView.setAdapter(favouriteAdapter);
    }

}
