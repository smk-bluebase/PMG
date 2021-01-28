package bluebase.in.pioneermusicgym;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SongsFragment extends Fragment {
    Context context;

    ListView songsListView;
    ProgressDialog progressDialog;
    ArrayList<LibraryItems> itemList = new ArrayList<>();

    String url = CommonUtils.IP + "/PMG/pmg_android/";
    String urlGetSongs = CommonUtils.IP + "/PMG/pmg_android/library_manager/getSongs.php";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);

        songsListView = view.findViewById(R.id.songsListView);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getContext();

        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        PostGetSongs postGetSongs = new PostGetSongs(context);
        postGetSongs.checkServerAvailability(2);
    }

    private class PostGetSongs extends PostRequest{
        public PostGetSongs(Context context){
            super(context);
        }

        public void serverAvailability(boolean isServerAvailable) {
            if (isServerAvailable) {
                super.postRequest(urlGetSongs, new JsonObject());
            } else {
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }

        @Override
        public void onFinish(JSONArray jsonArray) {
            System.out.println("jsonArray : " + jsonArray);
            progressDialog.dismiss();

            try{
                JSONObject jsonObject = (JSONObject) jsonArray.get(0);
                System.out.println("jsonObject : " + jsonObject);

                if(jsonObject.getBoolean("status")){
                    JSONArray jsonArray1 = jsonObject.getJSONArray("songs");

                    for (int i = 0; i < jsonArray1.length(); i++){
                        JSONArray jsonArray2 = (JSONArray) jsonArray1.get(i);

                        LibraryItems item = new LibraryItems();
                        item.setSongTitle(jsonArray2.get(0).toString());
                        item.setArtistName(jsonArray2.get(1).toString());
                        item.setComposerName(jsonArray2.get(2).toString());
                        item.setAlbumName(jsonArray2.get(3).toString());
                        item.setMovieName(jsonArray2.get(4).toString());
                        item.setDuration(jsonArray2.get(5).toString());

                        itemList.add(item);
                    }

                    LibraryAdapter libraryAdapter = new LibraryAdapter(context, itemList);
                    songsListView.setAdapter(libraryAdapter);

                }else {
                    Toast.makeText(context,"Username or Password Incorrect",Toast.LENGTH_SHORT).show();
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

}
