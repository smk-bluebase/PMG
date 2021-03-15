package bluebase.in.pioneermusicgym;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class MusicPlayerFragment extends Fragment {
    public static Context context;

    String urlGetSongDetails = CommonUtils.IP + "/pmg_android/search/getSongDetails.php";

    JsonObject jsonObject;
    ProgressDialog progressDialog;

    public static String fileLocation;

    public static TextView songTitle;

    RelativeLayout songDescriptionRelativeLayout;
    TextView artistName;
    TextView composerName;
    TextView movieName;
    TextView description;
    TextView year;
    TextView languageCode;

    public static Button playSongButton;

    public static Intent musicPlayerIntent;

    RelativeLayout musicControlsMasterRelativeLayout;
    RelativeLayout musicControlsRelativeLayout;
    TextView playbackTime;
    public static SeekBar songSeekBar;
    public static TextView duration;
    Button replayButton;
    public static Button backwardSkip;
    Button backwardSeek;
    public static Button playButton;
    Button forwardSeek;
    public static Button forwardSkip;
    Button favouritesButton;
    TextView pitch;
    SeekBar pitchSeekBar;
    Button pitchRestore;
    TextView tempo;
    SeekBar tempoSeekBar;
    Button tempoRestore;

    Button optionsButton;
    Button addToPlaylist;

    String durationStr = "";

    PDFView pdfView;

    boolean isFullLayoutOpen = false;

    public static String languageCodeStr = "ta";

    public static MaterialButtonToggleGroup materialButtonToggleGroup;

    String lyricsLocation = "empty.pdf";
    String englishLyricsLocation = "empty.pdf";

    public static MaterialButton tamil;
    public static MaterialButton hindi;
    public static MaterialButton malayalam;

    Button pitchMinus;
    Button pitchAdd;
    Button tempoMinus;
    Button tempoAdd;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_player, container, false);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        height = (int) (height / 1.53);

        ImageView background = view.findViewById(R.id.background);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 200, height);
        background.setLayoutParams(layoutParams);

        songTitle = view.findViewById(R.id.songTitle);

        songDescriptionRelativeLayout = view.findViewById(R.id.songDescriptionRelativeLayout);

        artistName = view.findViewById(R.id.artistName);
        composerName = view.findViewById(R.id.composerName);
        movieName = view.findViewById(R.id.movieName);
        description = view.findViewById(R.id.description);
        year = view.findViewById(R.id.year);
        languageCode = view.findViewById(R.id.languageCode);

        playSongButton = view.findViewById(R.id.playSongButton);

        musicControlsMasterRelativeLayout = view.findViewById(R.id.musicControlsMasterRelativeLayout);
        musicControlsRelativeLayout = view.findViewById(R.id.musicControlsRelativeLayout);
        playbackTime = view.findViewById(R.id.playbackTime);
        songSeekBar = view.findViewById(R.id.songSeekBar);
        duration = view.findViewById(R.id.duration);
        replayButton = view.findViewById(R.id.replayButton);
        backwardSkip = view.findViewById(R.id.backwardSkip);
        backwardSeek = view.findViewById(R.id.backwardSeek);
        playButton = view.findViewById(R.id.playButton);
        forwardSeek = view.findViewById(R.id.forwardSeek);
        forwardSkip = view.findViewById(R.id.forwardSkip);
        favouritesButton = view.findViewById(R.id.favouritesButton);

        pitch = view.findViewById(R.id.pitch);
        pitchSeekBar = view.findViewById(R.id.pitchSeekBar);
        pitchRestore = view.findViewById(R.id.pitchRestore);

        tempo = view.findViewById(R.id.tempo);
        tempoSeekBar = view.findViewById(R.id.tempoSeekBar);
        tempoRestore = view.findViewById(R.id.tempoRestore);

        optionsButton = view.findViewById(R.id.optionsButton);
        addToPlaylist = view.findViewById(R.id.addToPlaylist);

        pdfView = view.findViewById(R.id.pdfView);

        materialButtonToggleGroup = view.findViewById(R.id.languageToggleGroup);

        tamil = view.findViewById(R.id.tamil);
        hindi = view.findViewById(R.id.hindi);
        malayalam = view.findViewById(R.id.malayalam);

        pitchAdd = view.findViewById(R.id.pitchAdd);
        pitchMinus = view.findViewById(R.id.pitchMinus);
        tempoAdd = view.findViewById(R.id.tempoAdd);
        tempoMinus = view.findViewById(R.id.tempoMinus);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getContext();

        songDescriptionRelativeLayout.setVisibility(View.VISIBLE);
        playSongButton.setVisibility(View.VISIBLE);

        musicControlsRelativeLayout.setVisibility(View.INVISIBLE);
        optionsButton.setVisibility(View.INVISIBLE);
        addToPlaylist.setVisibility(View.INVISIBLE);

        RelativeLayout.LayoutParams relativeLayoutLayoutParams = (RelativeLayout.LayoutParams) musicControlsMasterRelativeLayout.getLayoutParams();
        relativeLayoutLayoutParams.setMargins(0, 0, 0 , -(CommonUtils.getPXFromDP(context, 190)));
        musicControlsMasterRelativeLayout.setLayoutParams(relativeLayoutLayoutParams);
        musicControlsMasterRelativeLayout.requestLayout();

        Animation slide_down = AnimationUtils.loadAnimation(context, R.anim.slide_down);

        Animation slide_up = AnimationUtils.loadAnimation(context, R.anim.slide_up);

        playSongButton.setOnClickListener(v -> {
            if(fileLocation != null) {
                songDescriptionRelativeLayout.setVisibility(View.INVISIBLE);
                playSongButton.setVisibility(View.INVISIBLE);
                musicControlsRelativeLayout.setVisibility(View.VISIBLE);
                optionsButton.setVisibility(View.VISIBLE);
                addToPlaylist.setVisibility(View.VISIBLE);
                duration.setText(durationStr);
                if(CommonUtils.isBound && !CommonUtils.fromNotification) CommonUtils.musicPlayerService.setStopPlayer();
            }else {
                Toast.makeText(context, "No Song Selected", Toast.LENGTH_SHORT).show();
            }
        });

        addToPlaylist.setOnClickListener(v -> {
            MusicPlayerUtils.addToPlaylist(context);
        });

        optionsButton.setOnClickListener(v -> {
            if(!isFullLayoutOpen){
                isFullLayoutOpen = true;
                RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) musicControlsMasterRelativeLayout.getLayoutParams();
                layoutParams1.setMargins(0, 0, 0 , 0);
                musicControlsMasterRelativeLayout.setLayoutParams(layoutParams1);
                musicControlsMasterRelativeLayout.requestLayout();
                musicControlsMasterRelativeLayout.startAnimation(slide_up);
                optionsButton.animate().rotation(180).start();
            }else {
                isFullLayoutOpen = false;
                musicControlsMasterRelativeLayout.startAnimation(slide_down);
                RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) musicControlsMasterRelativeLayout.getLayoutParams();
                layoutParams2.setMargins(0, 0, 0 , -(CommonUtils.getPXFromDP(context, 190)));
                musicControlsMasterRelativeLayout.setLayoutParams(layoutParams2);
                musicControlsMasterRelativeLayout.requestLayout();
                optionsButton.animate().rotation(0).start();
            }
        });

        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser && CommonUtils.isBound) CommonUtils.musicPlayerService.setSeekToPlayer(progress);
                playbackTime.setText(CommonUtils.isBound ? CommonUtils.musicPlayerService.getSongTimeElapsed(progress) : "00:00");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do Nothing!
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do Nothing!
            }
        });

        replayButton.setOnClickListener(v -> {
            if(CommonUtils.isBound) CommonUtils.musicPlayerService.setReplayPlayer();
            playButton.performClick();
        });

        backwardSkip.setOnClickListener(v -> {
            try {
                int position = MusicPlayerUtils.findPosition(CommonUtils.songId);
                if (position != -1 && position != 0 && CommonUtils.isBound) {
                    SongQueueItems item = CommonUtils.songQueue.get(position - 1);

                    CommonUtils.songId = item.getSongId();
                    songTitle.setText(item.getSongTitle());
                    languageCodeStr = item.getLanguageCode();
                    fileLocation = item.getFileLocation();
                    lyricsLocation = item.getLyricsLocation();
                    englishLyricsLocation = item.getEnglishLyricsLocation();
                    duration.setText(item.getDuration());

                    openPDF(languageCodeStr, lyricsLocation, false);

                    CommonUtils.musicPlayerService.setUrl(MusicPlayerUtils.getSongURL(languageCodeStr, fileLocation));
                    CommonUtils.musicPlayerService.setSongTitle(item.getSongTitle());
                    CommonUtils.musicPlayerService.setStopPlayer();
                    pitchRestore.performClick();
                    tempoRestore.performClick();
                    MusicPlayerUtils.setMaterialToggleLanguage(languageCodeStr);

                } else {
                    Toast.makeText(context, "Go Forward", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        backwardSeek.setOnClickListener(v -> {
            if(CommonUtils.isBound) CommonUtils.musicPlayerService.setSeekBackwardPlayer();
        });

        playButton.setOnClickListener(v -> {
            if(!CommonUtils.isServiceStarted){
                musicPlayerIntent = new Intent(context, MusicPlayerService.class);
                ContextCompat.startForegroundService(context, musicPlayerIntent);
                context.bindService(musicPlayerIntent, connection, Context.BIND_AUTO_CREATE);
                CommonUtils.isServiceStarted = true;

                playButton.setBackground(context.getDrawable(R.drawable.pause_button));

                CommonUtils.isPlaying = true;
            }else {
                if (!CommonUtils.isPlaying) {
                    CommonUtils.musicPlayerService.setUrl(MusicPlayerUtils.getSongURL(languageCodeStr, fileLocation));
                    CommonUtils.musicPlayerService.setSongTitle(CommonUtils.songQueue.get(MusicPlayerUtils.findPosition(CommonUtils.songId)).getSongTitle());
                    CommonUtils.musicPlayerService.setPlayPlayer();

                    playButton.setBackground(context.getDrawable(R.drawable.pause_button));

                    CommonUtils.isPlaying = true;
                } else {
                    CommonUtils.musicPlayerService.setPausePlayer();

                    playButton.setBackground(context.getDrawable(R.drawable.play_button));

                    CommonUtils.isPlaying = false;
                }
            }
        });

        forwardSeek.setOnClickListener(v -> {
            if(CommonUtils.isBound) CommonUtils.musicPlayerService.setSeekForwardPlayer();
        });

        forwardSkip.setOnClickListener(v -> {
            try {
                int position = MusicPlayerUtils.findPosition(CommonUtils.songId);
                if (position != -1 && (position + 1) < CommonUtils.songQueue.size() && CommonUtils.isBound) {
                    SongQueueItems item = CommonUtils.songQueue.get(position + 1);

                    CommonUtils.songId = item.getSongId();
                    songTitle.setText(item.getSongTitle());
                    languageCodeStr = item.getLanguageCode();
                    fileLocation = item.getFileLocation();
                    lyricsLocation = item.getLyricsLocation();
                    englishLyricsLocation = item.getEnglishLyricsLocation();
                    duration.setText(item.getDuration());

                    openPDF(languageCodeStr, lyricsLocation, false);

                    CommonUtils.musicPlayerService.setUrl(MusicPlayerUtils.getSongURL(languageCodeStr, fileLocation));
                    CommonUtils.musicPlayerService.setSongTitle(item.getSongTitle());
                    CommonUtils.musicPlayerService.setStopPlayer();
                    pitchRestore.performClick();
                    tempoRestore.performClick();
                    MusicPlayerUtils.setMaterialToggleLanguage(languageCodeStr);

                } else {
                    Toast.makeText(context, "No More Songs", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        pitchSeekBar.setMax(240);
        pitchSeekBar.setProgress(120, true);

        pitchSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pitch.setText(MusicPlayerUtils.calculatePitchText(progress - 120));
                if(CommonUtils.isBound) CommonUtils.musicPlayerService.setPitchPlayer(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do Nothing!
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do Nothing!
            }
        });

        pitchRestore.setOnClickListener(v -> {
            pitchSeekBar.setProgress(120, true);
        });

        pitchAdd.setOnClickListener(v -> {
            if(pitchSeekBar.getProgress() + 1 <= 240) pitchSeekBar.setProgress(pitchSeekBar.getProgress() + 1);
            else pitchSeekBar.setProgress(240);
        });

        pitchMinus.setOnClickListener(v -> {
            if(pitchSeekBar.getProgress() - 1 >= 0) pitchSeekBar.setProgress(pitchSeekBar.getProgress() - 1);
            else pitchSeekBar.setProgress(0);
        });

        tempoSeekBar.setMax(100);
        tempoSeekBar.setProgress(50, true);

        tempoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tempo.setText((progress + 50) + "%");
                if(CommonUtils.isBound) CommonUtils.musicPlayerService.setTempoPlayer(progress + 50);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do Nothing!
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do Nothing!
            }
        });

        tempoRestore.setOnClickListener(v -> {
            tempoSeekBar.setProgress(50, true);
        });

        tempoAdd.setOnClickListener(v -> {
            if(tempoSeekBar.getProgress() + 1 <= 100) tempoSeekBar.setProgress(tempoSeekBar.getProgress() + 1);
            else tempoSeekBar.setProgress(100);
        });

        tempoMinus.setOnClickListener(v -> {
            if(tempoSeekBar.getProgress() - 1 >= 0) tempoSeekBar.setProgress(tempoSeekBar.getProgress() - 1);
            else tempoSeekBar.setProgress(0);
        });

        favouritesButton.setOnClickListener(v -> {
            CommonUtils.startDatabaseHelper(context);

            if(!CommonUtils.dataBaseHelper.checkSongAvailability(CommonUtils.songId)){
                CommonUtils.dataBaseHelper.insertFavourites(CommonUtils.songId);
                Toast.makeText(context, "Added to Favourites", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(context, "Already added", Toast.LENGTH_SHORT).show();
            }

            CommonUtils.closeDataBaseHelper();
        });

        getSongDetails(CommonUtils.songId);

        materialButtonToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if(isChecked){
               if(checkedId == R.id.tamil || checkedId == R.id.malayalam || checkedId == R.id.hindi){
                   openPDF(languageCodeStr, lyricsLocation, false);
               }else if(checkedId == R.id.english){
                    openPDF(languageCodeStr, englishLyricsLocation, true);
               }
            }
        });

        CommonUtils.isMusicPlayerOpen = true;

    }

    private void getSongDetails(int songId){
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        jsonObject = new JsonObject();
        jsonObject.addProperty("songId", songId);

        GetSongDetails getSongDetails = new GetSongDetails(context);
        getSongDetails.checkServerAvailability(2);
    }

    private void openPDF(String languageCode, String lyricsLocation,  boolean isEnglish){
        String languageFolder = "";

        switch(languageCode){
            case "ta":
                languageFolder = "tamil";
                break;
            case "hi":
                languageFolder = "hindi";
                break;
            case "ma":
                languageFolder = "malayalam";
                break;
            default:
                languageFolder = "tamil";
        }

        if(!isEnglish) {
            String urlLyrics = "";
            try {
                urlLyrics = CommonUtils.IP + "/lyrics/" + languageFolder + "/" + URLEncoder.encode(lyricsLocation, "UTF-8");
                urlLyrics = urlLyrics.replace("+", "%20");
            } catch (Exception e) {
                e.printStackTrace();
            }

            GetPDF getPDF = new GetPDF(context, urlLyrics);
            getPDF.checkServerAvailability(2);
        }else {
            String urlEnglishLyrics = "";
            try {
                urlEnglishLyrics = CommonUtils.IP + "/lyrics/english/" + languageFolder + "/" + URLEncoder.encode(lyricsLocation, "UTF-8");
                urlEnglishLyrics = urlEnglishLyrics.replace("+", "%20");
            } catch (Exception e) {
                e.printStackTrace();
            }

            GetPDF getPDF = new GetPDF(context, urlEnglishLyrics);
            getPDF.checkServerAvailability(2);
        }

    }

    private class GetPDF extends GetPDFFile{
        String urlLyrics;

        public GetPDF(Context context, String urlLyrics){
            super(context);
            this.urlLyrics = urlLyrics;
        }

        @Override
        public void serverAvailability(boolean isServerAvailable) {
            if(isServerAvailable){
                super.getPDFFile(urlLyrics);
            }else {
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onPostUpdate(InputStream inputStream) {
            pdfView.fromStream(inputStream)
                    .enableSwipe(true)
                    .swipeHorizontal(true)
                    .enableDoubletap(true)
                    .defaultPage(0)
                    .enableAnnotationRendering(true)
                    .password(null)
                    .scrollHandle(new DefaultScrollHandle(context))
                    .enableAntialiasing(true)
                    .spacing(0)
                    .pageFitPolicy(FitPolicy.BOTH)
                    .pageSnap(true)
                    .autoSpacing(true)
                    .pageFling(true)
                    .load();
        }
    }

    public static ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            MusicPlayerService.LocalBinder binder = (MusicPlayerService.LocalBinder) service;
            CommonUtils.musicPlayerService = binder.getService();
            CommonUtils.isBound = true;

            CommonUtils.musicPlayerService.setUrl(MusicPlayerUtils.getSongURL(languageCodeStr, fileLocation));
            CommonUtils.musicPlayerService.setSongTitle(CommonUtils.songQueue.get(MusicPlayerUtils.findPosition(CommonUtils.songId)).getSongTitle());
            songTitle.setText(CommonUtils.songQueue.get(MusicPlayerUtils.findPosition(CommonUtils.songId)).getSongTitle());
            CommonUtils.musicPlayerService.setPlayPlayer();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            CommonUtils.isBound = false;
        }
    };

    public static void stopPlayer(Intent musicPlayerIntent){
        if(CommonUtils.isBound) {
            CommonUtils.musicPlayerService.setStopPlayer();
            CommonUtils.musicPlayerService.stopService(musicPlayerIntent);
            context.unbindService(connection);
            CommonUtils.isServiceStarted = false;
            CommonUtils.isBound = false;
        }
    }

    private class GetSongDetails extends PostRequest{
        public GetSongDetails(Context context){
            super(context);
        }

        @Override
        public void serverAvailability(boolean isServerAvailable) {
            if(isServerAvailable){
                super.postRequest(urlGetSongDetails, jsonObject);
            }else {
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }

        @Override
        public void onFinish(JSONArray jsonArray) {
            progressDialog.dismiss();

            try{
                JSONObject jsonObject = (JSONObject) jsonArray.get(0);

                if(jsonObject.getBoolean("status")){
                    JSONArray songDetails = jsonObject.getJSONArray("songDetails");

                    if(!songDetails.getString(0).equals("")) songTitle.setText(songDetails.getString(0));

                    if(!songDetails.getString(3).equals("")) artistName.setText(songDetails.getString(3));
                    else artistName.setText("Artist Name");

                    if(!songDetails.getString(4).equals("")) composerName.setText(songDetails.getString(4));
                    else composerName.setText("Composer Name");

                    if(!songDetails.getString(2).equals("")) movieName.setText(songDetails.getString(2));
                    else movieName.setText("Movie Name");

                    if(!songDetails.getString(1).equals("")) description.setText(songDetails.getString(1));
                    else description.setVisibility(View.INVISIBLE);

                    if(!songDetails.getString(6).equals("")) year.setText(songDetails.getString(6));
                    else year.setText("Year");

                    if(!songDetails.getString(5).equals("")){
                        languageCode.setText(songDetails.getString(5));
                        languageCodeStr = songDetails.getString(5);

                        MusicPlayerUtils.setMaterialToggleLanguage(languageCodeStr);
                    }

                    if(!songDetails.getString(7).equals("")) durationStr = songDetails.getString(7);
                    duration.setText(durationStr);

                    if(!songDetails.getString(8).equals("")) fileLocation = songDetails.getString(8);
                    else fileLocation = "empty.mp3";

                    lyricsLocation = "empty.pdf";
                    if(!songDetails.getString(9).equals("")) lyricsLocation = songDetails.getString(9);
                    else {
                        materialButtonToggleGroup.clearChecked();
                        materialButtonToggleGroup.check(R.id.english);
                    }

                    englishLyricsLocation = "empty.pdf";
                    if(!songDetails.getString(10).equals("")) englishLyricsLocation = songDetails.getString(10);

                    openPDF(languageCodeStr, lyricsLocation, false);

                    int position = MusicPlayerUtils.findPosition(CommonUtils.songId);

                    if(position != -1) {
                        CommonUtils.songQueue.get(position).setSongTitle(songDetails.getString(0));
                        CommonUtils.songQueue.get(position).setLanguageCode(languageCodeStr);
                        CommonUtils.songQueue.get(position).setFileLocation(fileLocation);
                        CommonUtils.songQueue.get(position).setLyricsLocation(lyricsLocation);
                        CommonUtils.songQueue.get(position).setEnglishLyricsLocation(englishLyricsLocation);
                        CommonUtils.songQueue.get(position).setDuration(durationStr);
                    }

                    if(CommonUtils.fromNotification){
                        playSongButton.performClick();
                        songSeekBar.setMax(CommonUtils.musicPlayerService.getMaxProgress());
                        songSeekBar.setProgress(CommonUtils.musicPlayerService.getProgress());
                        if(CommonUtils.isPlaying) playButton.setBackground(context.getDrawable(R.drawable.pause_button));
                        else playButton.setBackground(context.getDrawable(R.drawable.play_button));
                        duration.setText(CommonUtils.musicPlayerService.getDurationTime());
                        pitchRestore.performClick();
                        tempoRestore.performClick();
                    }

                }else{
                    Toast.makeText(context,"Song Not Found",Toast.LENGTH_SHORT).show();
                }

            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        CommonUtils.isMusicPlayerOpen = false;
    }
}
