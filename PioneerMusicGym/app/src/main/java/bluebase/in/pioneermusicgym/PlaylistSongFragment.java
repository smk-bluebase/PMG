package bluebase.in.pioneermusicgym;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class PlaylistSongFragment extends Fragment {
    Context context;

    int songIds[];

    RecyclerView playlistSongsRecyclerView;
    ArrayList<SongItems> songsList;
    SongAdapter songAdapter;

    RelativeLayout playlistSongsRelativeLayout;
    GetPlaylistSongs getPlaylistSongs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlists_songs, container, false);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        height = (int) (height / 1.53);

        ImageView background = view.findViewById(R.id.background);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 200, height);
        background.setLayoutParams(layoutParams);

        playlistSongsRelativeLayout = view.findViewById(R.id.playlistSongsRelativeLayout);

        playlistSongsRecyclerView = view.findViewById(R.id.playlistSongsRecyclerView);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getContext();

        songIds = CommonUtils.songIds;

        songsList = new ArrayList<>();

        getPlaylistSongs = new GetPlaylistSongs(context, songIds);
        getPlaylistSongs.checkServerAvailability(2);
    }

    private class GetPlaylistSongs extends GetSongs{
        int[] songIds;

        public GetPlaylistSongs(Context context, int[] songIds) {
            super(context);
            this.songIds = songIds;
        }

        @Override
        public void serverAvailability(boolean isServerAvailable) {
            if(!isServerAvailable){
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
            }else {
                getPlaylistSongs.getSongDetails(songIds);
            }
        }

        @Override
        public void onPostUpdate() {
            populateSongs();

            enableSwipeToDeleteAndUndo();
        }
    }

    private void populateSongs(){
        CommonUtils.startDatabaseHelper(context);
        songsList = new ArrayList<>();

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

                songsList.add(item);

            }catch(JSONException e){
                e.printStackTrace();
            }
        }

        CommonUtils.closeDataBaseHelper();

        playlistSongsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        songAdapter = new SongAdapter(new ArrayList<>(songsList));
        playlistSongsRecyclerView.setLayoutManager(linearLayoutManager);
        playlistSongsRecyclerView.setAdapter(null);
        playlistSongsRecyclerView.setAdapter(songAdapter);

        songAdapter.setOnItemClickListener(position -> {
            CommonUtils.fromNotification = false;
            MusicPlayerFragment.stopPlayer(MusicPlayerFragment.musicPlayerIntent);

            CommonUtils.songId = songAdapter.getData().get(position).getSongId();

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

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(context) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                int position = viewHolder.getAdapterPosition();
                SongItems item = songAdapter.getData().get(position);
                int originalPosition = songAdapter.getPosition(item);

                try {
                    CommonUtils.startDatabaseHelper(context);
                    CommonUtils.dataBaseHelper.deleteFromSongMaster(item.getSongId());
                    CommonUtils.dataBaseHelper.deleteFromPlaylistSongs(CommonUtils.playlistId, item.getSongId());
                    CommonUtils.closeDataBaseHelper();

                    populateSongs();

                    playlistSongsRecyclerView.getLayoutManager().scrollToPosition(originalPosition - 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Snackbar snackbar = Snackbar.make(playlistSongsRecyclerView, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", view -> {
                    try {
                        CommonUtils.startDatabaseHelper(context);
                        CommonUtils.dataBaseHelper.insertSongMaster(item.getSongId(), item.getSongTitle(), item.getMovieName(), item.getMovieSinger(), item.getYear(), item.getDuration());
                        CommonUtils.dataBaseHelper.insertPlaylistSongs(CommonUtils.playlistId, item.getSongId());
                        CommonUtils.closeDataBaseHelper();

                        populateSongs();

                        playlistSongsRecyclerView.getLayoutManager().scrollToPosition(originalPosition - 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(playlistSongsRecyclerView);
    }

}
