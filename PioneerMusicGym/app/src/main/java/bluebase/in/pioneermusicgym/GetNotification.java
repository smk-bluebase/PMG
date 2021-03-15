package bluebase.in.pioneermusicgym;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static bluebase.in.pioneermusicgym.App.CHANNEL_1_ID;

public class GetNotification {

    public static String urlGetNotification = CommonUtils.IP + "/pmg_android/notification/getNotifications.php";

    public static String GROUP_KEY = "bluebase.in.pioneermusicgym.new_messages";

    public static NotificationManagerCompat notificationManagerCompat;

    public static void getNewNotification(Context context){
        CommonUtils.startDatabaseHelper(context);
        if(CommonUtils.dataBaseHelper.getNotification() == 1) {
            PostGetNotification postGetNotification = new PostGetNotification(context);
            postGetNotification.checkServerAvailability(2);
        }
        CommonUtils.closeDataBaseHelper();
    }

    public static class PostGetNotification extends PostRequest{
        public PostGetNotification(Context context){
            super(context);
            notificationManagerCompat = NotificationManagerCompat.from(context);
        }

        @Override
        public void serverAvailability(boolean isServerAvailable) {
            if(isServerAvailable){
                super.postRequest(urlGetNotification, new JsonObject());
            }else {
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFinish(JSONArray jsonArray) {

            CommonUtils.startDatabaseHelper(context);

            try{
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                int numberOfNotifications = 0;

                if(jsonObject.getBoolean("status")) {
                    JSONArray notifications = jsonObject.getJSONArray("notifications");

                    numberOfNotifications = notifications.length();

                    Intent activityIntent = new Intent(context, MainActivity.class);
                    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);

                    String notificationMessage = "";

                    for (int i = 0; i < notifications.length(); i++) {
                        JSONArray notification = notifications.getJSONArray(i);

                        int notificationId = 0;
                        String message = "";

                        notificationId = notification.getInt(0);
                        message = notification.getString(1);

                        if(numberOfNotifications != 1) notificationMessage = numberOfNotifications + " new messages";
                        else notificationMessage = "1 new message";

                        notificationMessage += message + "\n ";

                        if(!CommonUtils.dataBaseHelper.checkNotificationShown(notificationId)) {
                            CommonUtils.dataBaseHelper.insertNotificationId(notificationId);

                            Notification newNotification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                                    .setSmallIcon(R.drawable.music_note)
                                    .setContentText("Pioneer Music Gym")
                                    .setContentText(message)
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                    .setContentIntent(contentIntent)
                                    .setAutoCancel(false)
                                    .setColor(context.getResources().getColor(R.color.c1))
                                    .setGroup(GROUP_KEY)
                                    .build();

                            notificationManagerCompat.notify(i + 1, newNotification);
                        }
                    }

//                    Notification summaryNotification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
//                            .setSmallIcon(R.drawable.music_note)
//                            .setContentTitle("Pioneer Music Gym")
////                            .setContentText(message)
//                            .setPriority(NotificationCompat.PRIORITY_HIGH)
//                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                            .setContentIntent(contentIntent)
//                            .setAutoCancel(false)
//                            .setColor(context.getResources().getColor(R.color.c1))
//                            .setGroup(GROUP_KEY)
//                            .setGroupSummary(true)
//                            .build();
//
//                    notificationManagerCompat.notify(0, summaryNotification);

                }

                CommonUtils.closeDataBaseHelper();

            }catch (JSONException e){
                e.printStackTrace();
            }

        }
    }

}
