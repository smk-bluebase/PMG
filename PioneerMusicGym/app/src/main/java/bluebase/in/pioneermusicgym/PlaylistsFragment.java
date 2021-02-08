package bluebase.in.pioneermusicgym;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PlaylistsFragment extends Fragment {
    Context context;

    RecyclerView playlistsRecyclerView;
    ArrayList<PlaylistItems> playlistsList = new ArrayList<>();
    PlaylistAdapter playlistAdapter;

    String urlGetPlaylists = CommonUtils.IP + "/PMG/pmg_android/search/getPlaylists.php";

    ProgressDialog progressDialog;
    JsonObject jsonObject;

    int lowerLimit;
    int upperLimit;

    Dialog dialog;
    FloatingActionButton addPlaylist;


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
                    int playlistId = CommonUtils.dataBaseHelper.playlistLocalMaxId();
                    String playlistTitle = " ";
                    int numberOfSongs = 0;
                    String createdOn = " ";
                    int isLocal = 1;

                    try {
                        playlistTitle = newPlaylistName.getText().toString();

                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date();
                        createdOn = dateFormat.format(date);

                        CommonUtils.dataBaseHelper.insertPlaylists(playlistId, playlistTitle, numberOfSongs, createdOn, isLocal);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    dialog.dismiss();
                    populatePlaylists();

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

        lowerLimit = 0;
        upperLimit = 30;

        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        jsonObject = new JsonObject();
        jsonObject.addProperty("lowerLimit", lowerLimit);
        jsonObject.addProperty("upperLimit", upperLimit);

        PostPlaylists postPlaylists = new PostPlaylists(context);
        postPlaylists.checkServerAvailability(2);
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
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void populatePlaylists(){
        CommonUtils.startDatabaseHelper(context);
        playlistsList = new ArrayList<>();

        JSONArray jsonArray = CommonUtils.dataBaseHelper.selectPlaylists();

        int j = 0;

        for(int i = 0; i < jsonArray.length(); i++){
            try{
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                PlaylistItems item = new PlaylistItems();
                item.setPlaylistId(jsonObject.getInt("playlistId"));
                item.setPlaylistTitle(jsonObject.getString("playlistTitle"));
                item.setNumberOfSongs(jsonObject.getString("numberOfSongs"));
                item.setCreatedOn(jsonObject.getString("createdOn"));

                if (j == 0) j = 1;
                else j = 0;

                playlistsList.add(item);

            }catch(JSONException e){
                e.printStackTrace();
            }
        }

        CommonUtils.closeDataBaseHelper();

        playlistsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        playlistAdapter = new PlaylistAdapter(playlistsList);
        playlistsRecyclerView.setLayoutManager(linearLayoutManager);
        playlistsRecyclerView.setAdapter(null);
        playlistsRecyclerView.setAdapter(playlistAdapter);
    }

    private class PostPlaylists extends PostRequest{
        public PostPlaylists(Context context){
            super(context);
        }

        @Override
        public void serverAvailability(boolean isServerAvailable) {
            if(isServerAvailable){
                super.postRequest(urlGetPlaylists, jsonObject);
            }else{
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }

        @Override
        public void onFinish(JSONArray jsonArray) {
            try{
                JSONObject jsonObject =  jsonArray.getJSONObject(0);

                CommonUtils.startDatabaseHelper(context);
                CommonUtils.dataBaseHelper.deleteFromPlaylists();

                if(jsonObject.getBoolean("status")){
                    JSONArray playlists = jsonObject.getJSONArray("playlists");

                    for(int i = 0; i < playlists.length(); i++){
                        JSONArray playlist = playlists.getJSONArray(i);

                        int playlistId = 0;
                        String playlistTitle = " ";
                        int numberOfSongs = 0;
                        String createdOn = " ";
                        int isLocal = 0;

                        try {
                            playlistId = Integer.parseInt(playlist.getString(0));
                            playlistTitle = playlist.getString(1);
                            numberOfSongs = Integer.parseInt(playlist.getString(2));
                            createdOn = playlist.getString(3);

                            CommonUtils.dataBaseHelper.insertPlaylists(playlistId, playlistTitle, numberOfSongs, createdOn, isLocal);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }else{
                    Toast.makeText(context, "No Data", Toast.LENGTH_SHORT).show();
                }

                CommonUtils.closeDataBaseHelper();

                populatePlaylists();

                progressDialog.dismiss();

            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

}
