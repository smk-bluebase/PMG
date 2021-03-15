package bluebase.in.pioneermusicgym;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {
    Context context;

    public DataBaseHelper(@Nullable Context context) {
        super(context, "pmg.sqlite", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating Tables

        // User Master
        String employeeMasterTable = "CREATE TABLE user_master (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                                    "user_id INTEGER, user_name VARCHAR NOT NULL, password VARCHAR NOT NULL)";
        db.execSQL(employeeMasterTable);

        // Singers
        String singersTable = "CREATE TABLE singers (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                            "singer_id INTEGER NOT NULL, singer_name VARCHAR NOT NULL," +
                            "number_of_songs INTEGER NOT NULL, number_of_movies INTEGER NOT NULL)";
        db.execSQL(singersTable);

        // Composers
        String composersTable = "CREATE TABLE composers (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                            "composer_id INTEGER NOT NULL, composer_name VARCHAR NOT NULL," +
                            "number_of_songs INTEGER NOT NULL, number_of_movies INTEGER NOT NULL)";
        db.execSQL(composersTable);

        // Movies
        String moviesTable = "CREATE TABLE movies (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                            "movie_id INTEGER NOT NULL, movie_name VARCHAR NOT NULL," +
                            "year VARCHAR NOT NULL, number_of_songs INTEGER NOT NULL)";
        db.execSQL(moviesTable);

        // Song Master
        String songMasterTable = "CREATE TABLE song_master (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                                "song_id INTEGER NOT NULL, title VARCHAR NOT NULL, movie_name VARCHAR, " +
                                "movie_singer VARCHAR, year VARCHAR NOT NULL, duration VARCHAR NOT NULL)";
        db.execSQL(songMasterTable);

        // Playlists
        String playlistsTable = "CREATE TABLE playlists (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                                "playlist_title VARCHAR NOT NULL, created_on VARCHAR NOT NULL)";
        db.execSQL(playlistsTable);

        // Playlist - Song Table
        String playlistSongsTable = "CREATE TABLE playlists_songs (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                                    "playlist_id INTEGER NOT NULL, song_id INTEGER NOT NULL)";
        db.execSQL(playlistSongsTable);

        // Favourites Table
        String favouritesTable = "CREATE TABLE favourites (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                                "song_id INTEGER NOT NULL)";
        db.execSQL(favouritesTable);

        // Settings Table
        String settingsTable = "CREATE TABLE settings (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                                "get_notification INTEGER NOT NULL, language_code INTEGER NOT NULL)";
        db.execSQL(settingsTable);

        // Notifications Table
        String notificationTable = "CREATE TABLE notifications (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                                "notification_id INTEGER NOT NULL)";
        db.execSQL(notificationTable);

    }

    // USER MASTER
    public void deleteUserMaster(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("user_master", null, null);
        db.close();
    }

    public void insertUserMaster(String userName, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("user_name", userName);
        cv.put("password", password);

        db.insert("user_master", null, cv);
        db.close();
    }

    public void insertUserIdIntoUserMaster(int userId, String userName){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE user_master SET `user_id` = " + userId + " WHERE user_name = \"" + userName + "\"";
        db.execSQL(query);
        db.close();
    }

    public JSONArray selectUserMaster(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet = db.rawQuery("SELECT * FROM user_master", null);
        resultSet.moveToFirst();

        JSONArray jsonArray = new JSONArray();

        if(resultSet.getCount() > 0){
            try {
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("userId", resultSet.getString(resultSet.getColumnIndex("user_id")));
                jsonObject.put("userName", resultSet.getString(resultSet.getColumnIndex("user_name")));
                jsonObject.put("password", resultSet.getString(resultSet.getColumnIndex("password")));

                jsonArray.put(jsonObject);
            }catch(JSONException e){
                e.printStackTrace();
            }
        }

        resultSet.close();

        return  jsonArray;
    }


    // SINGERS
    public void deleteSingers(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("singers", null, null);
        db.close();
    }

    public void insertSingers(int singerId, String singerName, int numberOfSongs, int numberOfMovies){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("singer_id", singerId);
        cv.put("singer_name", singerName);
        cv.put("number_of_songs", numberOfSongs);
        cv.put("number_of_movies", numberOfMovies);

        db.insert("singers", null, cv);
        db.close();
    }

    public JSONArray selectSingers(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet = db.rawQuery("SELECT * FROM singers", null);
        resultSet.moveToFirst();

        JSONArray jsonArray = new JSONArray();

        if(resultSet.getCount() > 0){
            int i = 0;
            while(i < resultSet.getCount()){
                try {
                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put("singerId", resultSet.getString(resultSet.getColumnIndex("singer_id")));
                    jsonObject.put("singerName", resultSet.getString(resultSet.getColumnIndex("singer_name")));
                    jsonObject.put("numberOfSongs", resultSet.getString(resultSet.getColumnIndex("number_of_songs")));
                    jsonObject.put("numberOfMovies", resultSet.getString(resultSet.getColumnIndex("number_of_movies")));

                    jsonArray.put(jsonObject);
                }catch(JSONException e){
                    e.printStackTrace();
                }

                resultSet.moveToNext();
                i++;
            }
        }

        resultSet.close();

        return  jsonArray;
    }


    // COMPOSERS
    public void deleteComposers(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("composers", null, null);
        db.close();
    }

    public void insertComposers(int composerId, String composerName, int numberOfSongs, int numberOfMovies){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("composer_id", composerId);
        cv.put("composer_name", composerName);
        cv.put("number_of_songs", numberOfSongs);
        cv.put("number_of_movies", numberOfMovies);

        db.insert("composers", null, cv);
        db.close();
    }

    public JSONArray selectComposers(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet = db.rawQuery("SELECT * FROM composers", null);
        resultSet.moveToFirst();

        JSONArray jsonArray = new JSONArray();

        if(resultSet.getCount() > 0){
            int i = 0;
            while(i < resultSet.getCount()){
                try {
                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put("composerId", resultSet.getString(resultSet.getColumnIndex("composer_id")));
                    jsonObject.put("composerName", resultSet.getString(resultSet.getColumnIndex("composer_name")));
                    jsonObject.put("numberOfSongs", resultSet.getString(resultSet.getColumnIndex("number_of_songs")));
                    jsonObject.put("numberOfMovies", resultSet.getString(resultSet.getColumnIndex("number_of_movies")));

                    jsonArray.put(jsonObject);
                }catch(JSONException e){
                    e.printStackTrace();
                }

                resultSet.moveToNext();
                i++;
            }
        }

        resultSet.close();

        return  jsonArray;
    }


    // MOVIES
    public void deleteMovies(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("movies", null, null);
        db.close();
    }

    public void insertMovies(int movieId, String movieName, String year, int numberOfSongs){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("movie_id", movieId);
        cv.put("movie_name", movieName);
        cv.put("year", year);
        cv.put("number_of_songs", numberOfSongs);

        db.insert("movies", null, cv);
        db.close();
    }

    public JSONArray selectMovies(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet = db.rawQuery("SELECT * FROM movies", null);
        resultSet.moveToFirst();

        JSONArray jsonArray = new JSONArray();

        if(resultSet.getCount() > 0){
            int i = 0;
            while(i < resultSet.getCount()){
                try {
                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put("movieId", resultSet.getString(resultSet.getColumnIndex("movie_id")));
                    jsonObject.put("movieName", resultSet.getString(resultSet.getColumnIndex("movie_name")));
                    jsonObject.put("year", resultSet.getString(resultSet.getColumnIndex("year")));
                    jsonObject.put("numberOfSongs", resultSet.getString(resultSet.getColumnIndex("number_of_songs")));

                    jsonArray.put(jsonObject);
                }catch(JSONException e){
                    e.printStackTrace();
                }

                resultSet.moveToNext();
                i++;
            }
        }

        resultSet.close();

        return  jsonArray;
    }


    //  SONG MASTER
    public void deleteSongMaster(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("song_master", null, null);
        db.close();
    }

    public void insertSongMaster(int songId, String title, String movieName, String movieSinger, String year, String duration){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("song_id", songId);
        cv.put("title", title);
        cv.put("movie_name", movieName);
        cv.put("movie_singer", movieSinger);
        cv.put("year", year);
        cv.put("duration", duration);

        db.insert("song_master", null, cv);
        db.close();
    }

    public JSONArray selectSongMaster(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet = db.rawQuery("SELECT * FROM song_master ORDER BY title ASC", null);
        resultSet.moveToFirst();

        JSONArray jsonArray = new JSONArray();

        if(resultSet.getCount() > 0){
            int i = 0;
            while(i < resultSet.getCount()){
                try {
                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put("songId", resultSet.getString(resultSet.getColumnIndex("song_id")));
                    jsonObject.put("title", resultSet.getString(resultSet.getColumnIndex("title")));
                    jsonObject.put("movieName", resultSet.getString(resultSet.getColumnIndex("movie_name")));
                    jsonObject.put("movieSinger", resultSet.getString(resultSet.getColumnIndex("movie_singer")));
                    jsonObject.put("year", resultSet.getString(resultSet.getColumnIndex("year")));
                    jsonObject.put("duration", resultSet.getString(resultSet.getColumnIndex("duration")));

                    jsonArray.put(jsonObject);
                }catch(JSONException e){
                    e.printStackTrace();
                }

                resultSet.moveToNext();
                i++;
            }
        }

        resultSet.close();

        return  jsonArray;
    }

    public void deleteFromSongMaster(int songId){
        String[] args = {String.valueOf(songId)};
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("song_master", "song_id = ?", args);
        db.close();
    }


    // PLAYLISTS
    public void insertPlaylists(String playlistTitle, String createdOn){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("playlist_title", playlistTitle);
        cv.put("created_on", createdOn);

        db.insert("playlists", null, cv);
        db.close();
    }

    public void insertIntoPlaylists(int playlistId, String playlistTitle, String createdOn){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", playlistId);
        cv.put("playlist_title", playlistTitle);
        cv.put("created_on", createdOn);

        db.insert("playlists", null, cv);
        db.close();
    }

    public JSONArray selectPlaylists(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet = db.rawQuery("SELECT id, playlist_title, created_on " +
                                        "FROM playlists " +
                                        "ORDER BY playlist_title", null);
        resultSet.moveToFirst();

        JSONArray jsonArray = new JSONArray();

        if(resultSet.getCount() > 0){
            int i = 0;
            while(i < resultSet.getCount()){
                try {
                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put("playlistId", resultSet.getInt(resultSet.getColumnIndex("id")));
                    jsonObject.put("playlistTitle", resultSet.getString(resultSet.getColumnIndex("playlist_title")));
                    jsonObject.put("createdOn", resultSet.getString(resultSet.getColumnIndex("created_on")));

                    jsonArray.put(jsonObject);
                }catch(JSONException e){
                    e.printStackTrace();
                }

                resultSet.moveToNext();
                i++;
            }
        }

        resultSet.close();

        return  jsonArray;
    }

    public boolean ifPlaylistNameExists(String playlistTitle){
        String[] args = {playlistTitle};
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet = db.rawQuery("SELECT COUNT(*) AS COUNT FROM playlists WHERE playlist_title = ?", args);
        resultSet.moveToFirst();

        int count = 0;

        if(resultSet.getCount() > 0){
            count = resultSet.getInt(resultSet.getColumnIndex("COUNT"));
        }

        resultSet.close();

        if(count == 0) return true;
        else return false;
    }

    public void deleteFromPlaylists(int id){
        String[] args = {String.valueOf(id)};
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("playlists", "id = ?", args);
        db.close();
    }


    // PLAYLISTS SONGS
    public void insertPlaylistSongs(int playlist_id, int song_id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("playlist_id", playlist_id);
        cv.put("song_id", song_id);

        db.insert("playlists_songs", null, cv);
        db.close();
    }

    public int[] selectPlaylistSongs(int playlistId){
        String[] args = {String.valueOf(playlistId)};
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet = db.rawQuery("SELECT song_id FROM playlists_songs WHERE playlist_id = ?", args);
        resultSet.moveToFirst();

        int songIds[] = {};

        if(resultSet.getCount() > 0){
            int i = 0;
            songIds = new int[resultSet.getCount()];
            while(i < resultSet.getCount()){
                try {
                    songIds[i] = resultSet.getInt(resultSet.getColumnIndex("song_id"));
                }catch(Exception e){
                    e.printStackTrace();
                }

                resultSet.moveToNext();
                i++;
            }
        }

        resultSet.close();

        return songIds;
    }

    public int selectPlaylistSongsCount(int playlistId){
        String[] args = {String.valueOf(playlistId)};
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet = db.rawQuery("SELECT COUNT(*) AS count FROM playlists_songs WHERE playlist_id = ?", args);
        resultSet.moveToFirst();

        int count = 0;

        if(resultSet.getCount() > 0) {
            count = resultSet.getInt(resultSet.getColumnIndex("count"));
        }

        resultSet.close();

        return count;
    }

    public void deleteFromPlaylistSongs(int playlistId, int songId){
        String[] args = {String.valueOf(playlistId), String.valueOf(songId)};
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("playlists_songs", "playlist_id = ? AND song_id = ?", args);
        db.close();
    }

    public void deleteFromPlaylistSongs(int playlistId){
        String[] args = {String.valueOf(playlistId)};
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("playlists_songs", "playlist_id = ?", args);
        db.close();
    }


    // FAVOURITES
    public void insertFavourites(int songId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("song_id", songId);

        db.insert("favourites", null, cv);
        db.close();
    }

    public void insertIntoFavourites(int id, int songId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("song_id", songId);

        db.insert("favourites", null, cv);
        db.close();
    }

    public int[] selectFavourites(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet = db.rawQuery("SELECT song_id FROM favourites", null);
        resultSet.moveToFirst();

        int songIds[] = {};

        if(resultSet.getCount() > 0){
            int i = 0;
            songIds = new int[resultSet.getCount()];
            while(i < resultSet.getCount()){
                try {
                    songIds[i] = resultSet.getInt(resultSet.getColumnIndex("song_id"));
                }catch(Exception e){
                    e.printStackTrace();
                }

                resultSet.moveToNext();
                i++;
            }
        }

        resultSet.close();

        return songIds;
    }

    public void deleteFromFavourites(int songId){
        String[] args = { String.valueOf(songId) };
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("favourites", "song_id = ?", args);
        db.close();
    }

    public boolean checkSongAvailability(int songId){
        String[] args = {String.valueOf(songId)};

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet = db.rawQuery("SELECT COUNT(*) AS COUNT " +
                                            "FROM favourites f " +
                                            "WHERE f.song_id = ?", args);
        resultSet.moveToFirst();

        if(resultSet.getCount() > 0){
            if(resultSet.getInt(resultSet.getColumnIndex("COUNT")) > 0) {
                resultSet.close();
                return true;
            }
        }

        resultSet.close();

        return false;
    }


    // SETTINGS
    public void insertSettings(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("get_notification", 1);
        cv.put("language_code", 0);

        db.insert("settings", null, cv);
        db.close();
    }

    public void setGetNotification(int getNotification){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("get_notification", getNotification);

        db.update("settings", cv, "id = ?", new String[]{String.valueOf(1)});
        db.close();
    }

    public int getNotification(){
        int getNotification = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet = db.rawQuery("SELECT get_notification FROM settings WHERE id = 1", null);
        resultSet.moveToFirst();

        if(resultSet.getCount() > 0){

            getNotification = resultSet.getInt(resultSet.getColumnIndex("get_notification"));
        }

        resultSet.close();

        return getNotification;
    }

    public void setLanguageCode(int languageCode){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("language_code", languageCode);

        db.update("settings", cv, "id = ?", new String[]{String.valueOf(1)});
        db.close();
    }

    public int getLanguageCode(){
        int languageCode = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet = db.rawQuery("SELECT language_code FROM settings WHERE id = 1", null);
        resultSet.moveToFirst();

        if(resultSet.getCount() > 0){
            languageCode = resultSet.getInt(resultSet.getColumnIndex("language_code"));
        }

        resultSet.close();

        return languageCode;
    }

    public int getSettingsCount(){
        int count = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet = db.rawQuery("SELECT COUNT(*) AS COUNT FROM settings", null);
        resultSet.moveToFirst();

        if(resultSet.getCount() > 0){
            count = resultSet.getInt(resultSet.getColumnIndex("COUNT"));
        }

        resultSet.close();

        return count;
    }

    // NOTIFICATIONS
    public void insertNotificationId(int notificationId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("notification_id", notificationId);

        db.insert("notifications", null, cv);
        db.close();
    }

    public boolean checkNotificationShown(int notificationId){
        String[] args = {String.valueOf(notificationId)};

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet = db.rawQuery("SELECT COUNT(*) AS COUNT " +
                "FROM notifications " +
                "WHERE notification_id = ?", args);
        resultSet.moveToFirst();

        if(resultSet.getCount() > 0){
            if(resultSet.getInt(resultSet.getColumnIndex("COUNT")) == 1) {
                resultSet.close();
                return true;
            }
        }

        resultSet.close();

        return false;
    }

    public void deleteNotifications(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("notifications", null, null);
        db.close();
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // As of now no upgrading!
    }
}
