package bluebase.in.pioneermusicgym;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {
    public static String IP = "http://115.243.95.117:8081/Pioneer_Music_Gym";

    private static final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public static boolean emailValidator(String email) {
        if (email == null) {
            return false;
        }

        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    public static int userId;

    public static String userName;

    public static String email;

    public static DataBaseHelper dataBaseHelper;

    public static void startDatabaseHelper(Context context){
        dataBaseHelper = new DataBaseHelper(context);
    }

    public static void closeDataBaseHelper(){
        dataBaseHelper.close();
    }

    public static int openTab;

    public static int homeQueryLimit = 10;

    public static int queryLimit = 50;

    public static int playlistId = 0;

    public static int songIds[];

    public static int songId = 0;

    public static List<SongQueueItems> songQueue = new ArrayList();

    public static boolean isPresent(SongQueueItems songQueueItem){
        for(SongQueueItems item : songQueue){
            if(item.getSongId() == songQueueItem.getSongId()){
                return true;
            }
        }

        return false;
    }

    public static boolean isSearching = false;

    public static boolean isHomeSearching = false;

    public static String searchQuery = "";

    public static int getPXFromDP(Context context, int dp){
        Resources r = context.getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );

        return px;
    }

    public static MusicPlayerService musicPlayerService;

    public static boolean isBound = false;

    public static boolean fromNotification = false;

    public static boolean isPlaying = false;

    public static boolean isMusicPlayerOpen = false;

    public static boolean isServiceStarted = false;

}