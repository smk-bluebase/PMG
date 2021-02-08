package bluebase.in.pioneermusicgym;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeFragment extends Fragment {
    Context context;

    int[] colors;
    UpdateLocal updateLocal;

    RecyclerView singerRecyclerView;
    RecyclerView composerRecyclerView;
    RecyclerView movieRecyclerView;
    RecyclerView playlistRecyclerView;

    ArrayList<HomeItems> singerList = new ArrayList<>();
    ArrayList<HomeItems> composerList = new ArrayList<>();
    ArrayList<HomeItems> movieList = new ArrayList<>();
    ArrayList<HomeItems> playlistList = new ArrayList<>();

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

        TypedArray typedArray = context.getResources().obtainTypedArray(R.array.homeColor);
        colors = new int[typedArray.length()];
        for (int i = 0; i < typedArray.length(); i++) {
            colors[i] = typedArray.getColor(i, 0);
        }
        typedArray.recycle();

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
            populateSingers();
            populateComposers();
            populateMovies();
            populatePlaylists();
            CommonUtils.closeDataBaseHelper();
        }

    }

    private void populateSingers(){
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

                if(jsonObject.getInt("numberOfAlbums") > jsonObject.getInt("numberOfMovies")) {
                    if(jsonObject.getInt("numberOfAlbums") != 1) item.setSubmenu2(jsonObject.getInt("numberOfAlbums") + " Albums");
                    else item.setSubmenu2("1 Album");
                }else {
                    if(jsonObject.getInt("numberOfMovies") != 1) item.setSubmenu2(jsonObject.getInt("numberOfMovies") + " Movies");
                    else item.setSubmenu2("1 Movie");
                }

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
        HomeAdapter artistAdapter = new HomeAdapter(singerList);
        singerRecyclerView.setLayoutManager(linearLayoutManager);
        singerRecyclerView.setAdapter(artistAdapter);
    }

    private void populateComposers(){
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

                if(jsonObject.getInt("numberOfAlbums") > jsonObject.getInt("numberOfMovies")) {
                    if(jsonObject.getInt("numberOfAlbums") != 1) item.setSubmenu2(jsonObject.getInt("numberOfAlbums") + " Albums");
                    else item.setSubmenu2("1 Album");
                }else {
                    if(jsonObject.getInt("numberOfMovies") != 1) item.setSubmenu2(jsonObject.getInt("numberOfMovies") + " Movies");
                    else item.setSubmenu2("1 Movie");
                }

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
        HomeAdapter composerAdapter = new HomeAdapter(composerList);
        composerRecyclerView.setLayoutManager(linearLayoutManager);
        composerRecyclerView.setAdapter(composerAdapter);
    }

    private void populateMovies(){
        int j = 0;
        boolean isReverse = false;

        JSONArray jsonArray = CommonUtils.dataBaseHelper.selectMovies();

        for(int i = 0; i <= jsonArray.length() ; i++){
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
        HomeAdapter movieAdapter = new HomeAdapter(movieList);
        movieRecyclerView.setLayoutManager(linearLayoutManager);
        movieRecyclerView.setAdapter(movieAdapter);
    }

    private void populatePlaylists(){
        int j = 0;
        boolean isReverse = false;

        JSONArray jsonArray = CommonUtils.dataBaseHelper.selectAlbums();

        for(int i = 0; i < jsonArray.length(); i++){
            try {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                HomeItems item = new HomeItems();
                item.setId(jsonObject.getInt("playlistId"));
                item.setTitle(jsonObject.getString("playlistName"));

                if(jsonObject.getInt("numberOfSongs") != 1) item.setSubMenu1(jsonObject.getInt("numberOfSongs") + " Songs");
                else item.setSubMenu1("1 Song");

                item.setSubmenu2(jsonObject.getString("year"));
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
        HomeAdapter albumAdapter = new HomeAdapter(playlistList);
        playlistRecyclerView.setLayoutManager(linearLayoutManager);
        playlistRecyclerView.setAdapter(albumAdapter);
    }


}
