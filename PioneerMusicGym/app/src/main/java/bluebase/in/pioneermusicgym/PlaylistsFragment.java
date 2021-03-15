package bluebase.in.pioneermusicgym;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PlaylistsFragment extends Fragment {
    Context context;

    RecyclerView playlistsRecyclerView;
    ArrayList<PlaylistItems> playlistsList;
    PlaylistAdapter playlistAdapter;

    Dialog dialog;
    FloatingActionButton addPlaylist;

    RelativeLayout playlistRelativeLayout;

    boolean isSearch = false;
    String queryString = "";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlists, container, false);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        height = (int) (height / 1.53);

        ImageView background = view.findViewById(R.id.background);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 200, height);
        background.setLayoutParams(layoutParams);

        playlistRelativeLayout = view.findViewById(R.id.playlistRelativeLayout);

        playlistsRecyclerView = view.findViewById(R.id.playlistsRecyclerView);

        addPlaylist = view.findViewById(R.id.addPlaylistButton);

        addPlaylist.setOnClickListener(v -> {
            dialog = new Dialog(context);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.new_playlist);

            final EditText newPlaylistName = dialog.findViewById(R.id.newPlaylistName);
            Button submit = dialog.findViewById(R.id.submit);

            submit.setOnClickListener(v1 -> {
                CommonUtils.startDatabaseHelper(context);

                if(newPlaylistName.getText().toString().length() > 0) {
                    if(CommonUtils.dataBaseHelper.ifPlaylistNameExists(newPlaylistName.getText().toString())) {
                        String playlistTitle = " ";
                        String createdOn = " ";

                        try {
                            playlistTitle = newPlaylistName.getText().toString();

                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = new Date();
                            createdOn = dateFormat.format(date);

                            CommonUtils.dataBaseHelper.insertPlaylists(playlistTitle, createdOn);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        dialog.dismiss();
                        populatePlaylists();
                    }else {
                        Toast.makeText(context, "Playlist Name Exists", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(context, "Enter new Playlist Name", Toast.LENGTH_SHORT).show();
                }

                CommonUtils.closeDataBaseHelper();

            });

            dialog.show();
        });

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getContext();

        playlistsList = new ArrayList<>();

        populatePlaylists();

        enableSwipeToDeleteAndUndo();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchViewItem = menu.findItem(R.id.search);

        final android.widget.SearchView searchView = (android.widget.SearchView) MenuItemCompat.getActionView(searchViewItem);

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setIconified(false);
        searchView.setQueryHint("Search Playlists");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();

                isSearch = true;
                queryString = query;

                boolean contains = false;
                for (PlaylistItems item : playlistsList){
                    if(item.getPlaylistTitle().equals(query)){
                        contains = true;
                        break;
                    }
                }

                if(contains){
                    playlistAdapter.getFilter().filter(query);
                }else{
                    Toast.makeText(context, "No Match found",Toast.LENGTH_LONG).show();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                isSearch = true;
                queryString = newText;
                playlistAdapter.getFilter().filter(newText);
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
                playlistAdapter.getFilter().filter("");
                isSearch = false;
                queryString = "";
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void populatePlaylists(){
        CommonUtils.startDatabaseHelper(context);
        playlistsList = new ArrayList<>();

        JSONArray jsonArray = CommonUtils.dataBaseHelper.selectPlaylists();

        for(int i = 0; i < jsonArray.length(); i++){
            try{
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                int count = CommonUtils.dataBaseHelper.selectPlaylistSongsCount(jsonObject.getInt("playlistId"));

                PlaylistItems item = new PlaylistItems();
                item.setPlaylistId(jsonObject.getInt("playlistId"));
                item.setPlaylistTitle(jsonObject.getString("playlistTitle"));
                item.setNumberOfSongs(count);
                item.setCreatedOn(jsonObject.getString("createdOn"));

                playlistsList.add(item);

            }catch(JSONException e){
                e.printStackTrace();
            }
        }

        CommonUtils.closeDataBaseHelper();

        playlistsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        playlistAdapter = new PlaylistAdapter(new ArrayList<>(playlistsList));
        playlistsRecyclerView.setLayoutManager(linearLayoutManager);
        playlistsRecyclerView.setAdapter(null);
        playlistsRecyclerView.setAdapter(playlistAdapter);

        playlistAdapter.setOnItemClickListener(position -> {
            CommonUtils.startDatabaseHelper(context);

            CommonUtils.playlistId = playlistAdapter.getData().get(position).getPlaylistId();

            CommonUtils.songIds = CommonUtils.dataBaseHelper.selectPlaylistSongs(CommonUtils.playlistId);

            CommonUtils.closeDataBaseHelper();

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack("playlistSongFragment")
                    .replace(R.id.fragment_container, new PlaylistSongFragment(), "playlistSongFragment")
                    .commit();
        });
    }

    private void positionPlaylist(int position, int originalPosition){
        if(isSearch){
            playlistsRecyclerView.scrollToPosition(position);
            playlistAdapter.getFilter().filter(queryString);
        }else {
            playlistsRecyclerView.getLayoutManager().scrollToPosition(originalPosition);
        }
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(context) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                int position = viewHolder.getAdapterPosition();
                PlaylistItems item = playlistAdapter.getData().get(position);
                int originalPosition = playlistAdapter.getPosition(item);

                try {
                    CommonUtils.startDatabaseHelper(context);
                    CommonUtils.dataBaseHelper.deleteFromPlaylists(item.getPlaylistId());
                    CommonUtils.closeDataBaseHelper();

                    populatePlaylists();

                    positionPlaylist(position - 1, originalPosition - 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setCancelable(true);
                alertDialogBuilder.setTitle("Pioneer Music Gym");
                alertDialogBuilder.setMessage("Do you want to delete this playlist?");
                alertDialogBuilder.setPositiveButton(android.R.string.ok,
                        (dialog, id) -> {
                            dialog.cancel();

                            CommonUtils.startDatabaseHelper(context);
                            CommonUtils.dataBaseHelper.deleteFromPlaylistSongs(item.getPlaylistId());
                            CommonUtils.closeDataBaseHelper();

                        });

                alertDialogBuilder.setNegativeButton(android.R.string.cancel,
                        (dialog, id) -> {
                            dialog.cancel();

                            try {
                                CommonUtils.startDatabaseHelper(context);

                                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = new Date();
                                String createdOn = dateFormat.format(date);
                                CommonUtils.dataBaseHelper.insertIntoPlaylists(item.getPlaylistId(), item.getPlaylistTitle(), createdOn);

                                CommonUtils.closeDataBaseHelper();

                                populatePlaylists();

                                positionPlaylist(position - 1, originalPosition - 1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        });
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(playlistsRecyclerView);
    }

}
