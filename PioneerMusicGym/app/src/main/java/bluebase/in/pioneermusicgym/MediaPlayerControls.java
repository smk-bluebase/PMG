package bluebase.in.pioneermusicgym;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Build;
import android.os.Handler;

import java.io.IOException;

import androidx.annotation.RequiresApi;

public class MediaPlayerControls {
    Context context;
    MediaPlayer mediaPlayer;
    private Handler handler;
    String url;
    Thread musicThread;

    public MediaPlayerControls(Context context){
        this.context = context;
        handler = new Handler();
    }

    public void playSong(String url){
        this.url = url;
        if(mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );

            try {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(url);
                mediaPlayer.setOnPreparedListener(this::onPrepared);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            try {
                mediaPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void onPrepared(MediaPlayer player) {
        mediaPlayer.start();
        int duration = mediaPlayer.getDuration() / 1000;
        MusicPlayerFragment.songSeekBar.setMax(duration);
        MusicPlayerFragment.duration.setText(generateTime(duration));

        musicThread = new Thread(new Runnable() {
            int progress = 0;
            boolean isProgress = false;

            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    MusicPlayerFragment.songSeekBar.setProgress(currentPosition);

                    if(currentPosition > 0) {
                        if ((currentPosition % 25) == 0 && !isProgress) {
                            isProgress = true;
                            mediaPlayer.setVolume(0, 0);
                        } else if (progress == 1) {
                            progress = 0;
                            isProgress = false;
                            mediaPlayer.setVolume(1, 1);
                        }
                    }

                    if(isProgress) progress += 1;

                    if (currentPosition == duration) stopPlayer();
                    else handler.postDelayed(this::run, 1000);
                }
            }
        });

        musicThread.start();

    }

    public void pausePlayer(){
        if(mediaPlayer != null){
            mediaPlayer.pause();
        }
    }

    public void stopPlayer(){
        if(mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
            MusicPlayerFragment.songSeekBar.setProgress(0);
            CommonUtils.isPlaying = false;
            MusicPlayerFragment.playButton.setBackground(context.getDrawable(R.drawable.play_button));
        }
    }

    public void seekTo(int progress){
        if(mediaPlayer != null){
            mediaPlayer.seekTo(progress * 1000);
        }
    }

    public void seekForward(){
        if(mediaPlayer != null){
            int progress = mediaPlayer.getCurrentPosition() / 1000;
            mediaPlayer.seekTo((progress + 10) * 1000);
            MusicPlayerFragment.songSeekBar.setProgress(progress + 10);
        }
    }

    public void seekBackward(){
        if(mediaPlayer != null){
            int progress = mediaPlayer.getCurrentPosition() / 1000;
            mediaPlayer.seekTo((progress - 10) * 1000);
            MusicPlayerFragment.songSeekBar.setProgress(progress - 10);
        }
    }

    public void replay(){
        stopPlayer();
        if(mediaPlayer != null) mediaPlayer.seekTo(0);
    }

    public String generateTime(int seconds){
        int minutes = seconds / 60;
        String minutesStr = String.valueOf(minutes);
        if(minutesStr.length() == 1) minutesStr = "0" + minutesStr;
        String secondsStr = String.valueOf(seconds - (60 * minutes));
        if(secondsStr.length() == 1) secondsStr = "0" + secondsStr;
        return minutesStr + ":" + secondsStr;
    }

    public void setPitch(float pitch){
        if(mediaPlayer != null) {
            PlaybackParams params = new PlaybackParams();
            params.setPitch(pitch);
            mediaPlayer.setPlaybackParams(params);
        }
    }

    public void setTempo(float tempo) {
        if (mediaPlayer != null) {
            PlaybackParams params = new PlaybackParams();
            params.setSpeed(tempo);
            mediaPlayer.setPlaybackParams(params);
        }
    }

    public int getMaxProgress(){
        return mediaPlayer.getDuration() / 1000;
    }

    public int getProgress(){
        return mediaPlayer.getCurrentPosition() / 1000;
    }

}
