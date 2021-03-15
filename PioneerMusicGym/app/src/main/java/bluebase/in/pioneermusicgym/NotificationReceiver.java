package bluebase.in.pioneermusicgym;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
       String action = intent.getStringExtra("action");

       switch(action){
           case "previous":
               backwardSkip();
               break;
           case "pause":
               play();
               break;
           case "next":
               forwardSkip();
               break;
           default:
               // Do Nothing!
       }

    }

    private void backwardSkip(){
        int position = MusicPlayerUtils.findPosition(CommonUtils.songId);
        if (position != -1 && position != 0 && CommonUtils.isBound && !CommonUtils.isMusicPlayerOpen) {
            SongQueueItems item = CommonUtils.songQueue.get(position - 1);

            CommonUtils.songId = item.getSongId();
            String languageCodeStr = item.getLanguageCode();
            String fileLocation = item.getFileLocation();

            CommonUtils.musicPlayerService.setUrl(MusicPlayerUtils.getSongURL(languageCodeStr, fileLocation));
            CommonUtils.musicPlayerService.setSongTitle(item.getSongTitle());
            CommonUtils.musicPlayerService.setStopPlayer();
            CommonUtils.musicPlayerService.setPitchPlayer(120);
            CommonUtils.musicPlayerService.setTempoPlayer(50);
            CommonUtils.isPlaying = false;
        }else {
            MusicPlayerFragment.backwardSkip.performClick();
        }
    }

    private void play(){
        if(!CommonUtils.isMusicPlayerOpen) {
            if (!CommonUtils.isPlaying && CommonUtils.isBound) {
                CommonUtils.musicPlayerService.setPlayPlayer();
                CommonUtils.isPlaying = true;
            } else {
                CommonUtils.musicPlayerService.setPausePlayer();
                CommonUtils.isPlaying = false;
            }
        }else {
            MusicPlayerFragment.playButton.performClick();
        }
    }

    private void forwardSkip(){
        int position = MusicPlayerUtils.findPosition(CommonUtils.songId);

        if (position != -1 && (position + 1) < CommonUtils.songQueue.size() && CommonUtils.isBound && !CommonUtils.isMusicPlayerOpen) {
            SongQueueItems item = CommonUtils.songQueue.get(position + 1);

            CommonUtils.songId = item.getSongId();
            String languageCodeStr = item.getLanguageCode();
            String fileLocation = item.getFileLocation();

            CommonUtils.musicPlayerService.setUrl(MusicPlayerUtils.getSongURL(languageCodeStr, fileLocation));
            CommonUtils.musicPlayerService.setSongTitle(item.getSongTitle());
            CommonUtils.musicPlayerService.setStopPlayer();
            CommonUtils.musicPlayerService.setPitchPlayer(120);
            CommonUtils.musicPlayerService.setTempoPlayer(50);
            CommonUtils.isPlaying = false;
        }else {
            MusicPlayerFragment.forwardSkip.performClick();
        }
    }

}
