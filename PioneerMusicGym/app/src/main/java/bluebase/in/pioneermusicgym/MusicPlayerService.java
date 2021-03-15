package bluebase.in.pioneermusicgym;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static bluebase.in.pioneermusicgym.App.CHANNEL_2_ID;

public class MusicPlayerService extends Service {
    MediaPlayerControls mediaPlayerControls;
    Context context;
    String url;
    String songTitle;

    PendingIntent pausePendingIntent;

    private final IBinder binder = new LocalBinder();

    public static NotificationCompat.Builder notificationBuilder;
    public static Notification notification;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        mediaPlayerControls = new MediaPlayerControls(context);
    }

    public class LocalBinder extends Binder {
        MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent activityIntent = new Intent(context, MainActivity.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activityIntent.putExtra("isNotification", true);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent previousIntent = new Intent(context, NotificationReceiver.class);
        previousIntent.putExtra("action", "previous");
        PendingIntent previousPendingIntent = PendingIntent.getBroadcast(context, 1, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseIntent = new Intent(context, NotificationReceiver.class);
        pauseIntent.putExtra("action", "pause");
        pausePendingIntent = PendingIntent.getBroadcast(context, 2, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(context, NotificationReceiver.class);
        nextIntent.putExtra("action", "next");
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 3, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap smallIcon = BitmapFactory.decodeResource(getResources(), R.drawable.pmg_notificaion_logo_small);
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.pmg_notification_logo_large);

        notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_2_ID)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.music_note)
                .setContentTitle("Pioneer Music Gym")
                .addAction(R.drawable.skip_previous, "previous", previousPendingIntent)
                .addAction(R.drawable.pause, "pause", pausePendingIntent)
                .addAction(R.drawable.skip_next, "next", nextPendingIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setContentIntent(contentIntent)
                .setColor(context.getResources().getColor(R.color.pmg_logo_red))
                .setAutoCancel(false);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationBuilder.setColorized(true)
                    .setLargeIcon(largeIcon);
        }else {
            notificationBuilder.setLargeIcon(smallIcon);
        }

        notification = notificationBuilder.build();

        startForeground(1, notification);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayerControls.stopPlayer();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    public void setPausePlayer(){
        notification.actions[1] = new Notification.Action(R.drawable.play_button, "pause", pausePendingIntent);
        startForeground(1, notification);
        mediaPlayerControls.pausePlayer();
    }

    public void setStopPlayer(){
        mediaPlayerControls.stopPlayer();
    }

    public void setSeekForwardPlayer(){
        mediaPlayerControls.seekForward();
    }

    public void setSeekBackwardPlayer(){
        mediaPlayerControls.seekBackward();
    }

    public void setReplayPlayer(){
        mediaPlayerControls.replay();
    }

    public void setPitchPlayer(int progress){
        mediaPlayerControls.setPitch(calculatePitch(progress));
    }

    public void setTempoPlayer(int progress){
        mediaPlayerControls.setTempo(calculateTempo(progress));
    }

    private float calculatePitch(int progress){
        if(progress == 120){
            return 1.0f;
        }else if(progress < 120){
            progress = progress - 120;
            double percentageDouble = ((double) progress / 120) * 100;
            int percentage = (int) percentageDouble;
            double pitchDouble = ((double) percentage / 100);
            pitchDouble = (1 + pitchDouble) * 100;
            pitchDouble = (int) pitchDouble;
            pitchDouble = pitchDouble / 100;
            float pitch = (float) pitchDouble;
            if(pitch <= 0.2f) pitch = 0.2f;
            return pitch;
        }else {
            progress = progress - 120;
            double percentageDouble = ((double) progress / 120) * 100;
            int percentage = (int) percentageDouble;
            double pitchDouble = ((double) percentage / 100);
            pitchDouble = pitchDouble + 1;
            float pitch = (float) pitchDouble;
            return pitch;
        }
    }

    private float calculateTempo(int progress){
        if(progress == 100){
            return 1.0f;
        }else if(progress < 100){
            float tempo = progress % 100;
            tempo = tempo / 100;
            return tempo;
        }else {
            float tempo = progress % 100;
            tempo = tempo / 100;
            tempo = 1 + tempo;
            return tempo;
        }
    }

    public void setUrl(String url){
        this.url = url;
    }

    public void setSongTitle(String songTitle){
        notificationBuilder.setContentText(songTitle);
        notification = notificationBuilder.build();
        notification.actions[1] = new Notification.Action(R.drawable.play_button, "pause", pausePendingIntent);
        startForeground(1, notification);
        this.songTitle = songTitle;
    }

    public void setPlayPlayer(){
        notification.actions[1] = new Notification.Action(R.drawable.pause, "pause", pausePendingIntent);
        startForeground(1, notification);
        mediaPlayerControls.playSong(url);
    }

    public void setSeekToPlayer(int progress){
        mediaPlayerControls.seekTo(progress);
    }

    public String getSongTimeElapsed(int progress) {
        return mediaPlayerControls.generateTime(progress);
    }

    public int getMaxProgress(){
        return mediaPlayerControls.getMaxProgress();
    }

    public int getProgress(){
        return mediaPlayerControls.getProgress();
    }

    public String getDurationTime(){
        return mediaPlayerControls.generateTime(mediaPlayerControls.getMaxProgress());
    }

}
