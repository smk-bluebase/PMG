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
                            "number_of_songs INTEGER NOT NULL, number_of_albums INTEGER NOT NULL," +
                            "number_of_movies INTEGER NOT NULL)";
        db.execSQL(singersTable);

        // Composers
        String composersTable = "CREATE TABLE composers (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                            "composer_id INTEGER NOT NULL, composer_name VARCHAR NOT NULL," +
                            "number_of_songs INTEGER NOT NULL, number_of_albums INTEGER NOT NULL," +
                            "number_of_movies INTEGER NOT NULL)";
        db.execSQL(composersTable);

        // Movies
        String moviesTable = "CREATE TABLE movies (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                            "movie_id INTEGER NOT NULL, movie_name VARCHAR NOT NULL," +
                            "year VARCHAR NOT NULL, number_of_songs INTEGER NOT NULL)";
        db.execSQL(moviesTable);

        // Song Master
        String songMasterTable = "CREATE TABLE song_master (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                                "song_id INTEGER NOT NULL, title VARCHAR NOT NULL, album_name VARCHAR, movie_name VARCHAR, " +
                                "album_singer VARCHAR, album_composer VARCHAR, movie_singer VARCHAR, movie_composer VARCHAR," +
                                "language_code VARCHAR NOT NULL, year VARCHAR NOT NULL, duration VARCHAR NOT NULL," +
                                "file_location VARCHAR NOT NULL, lyrics_location VARCHAR NOT NULL)";
        db.execSQL(songMasterTable);

        // Playlists
        String playlistsTable = "CREATE TABLE playlists (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                                "playlist_id INTEGER, playlist_title VARCHAR NOT NULL, number_of_songs INTEGER NOT NULL," +
                                "created_on VARCHAR NOT NULL, is_local INTEGER NOT NULL)";
        db.execSQL(playlistsTable);

        // Playlist - Song Table
        String playlistSongsTable = "CREATE TABLE playlists_songs (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                                    "playlist_id INTEGER NOT NULL, song_id INTEGER NOT NULL)";
        db.execSQL(playlistSongsTable);

        // Favourites Table
        String favouritesTable = "CREATE TABLE favourites (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                                "song_id INTEGER NOT NULL)";
        db.execSQL(favouritesTable);

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

    public void insertSingers(int singerId, String singerName, int numberOfSongs, int numberOfAlbums, int numberOfMovies){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("singer_id", singerId);
        cv.put("singer_name", singerName);
        cv.put("number_of_songs", numberOfSongs);
        cv.put("number_of_albums", numberOfAlbums);
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
                    jsonObject.put("numberOfAlbums", resultSet.getString(resultSet.getColumnIndex("number_of_albums")));
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

    public int singersMaxId(){
        int maxId = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet = db.rawQuery("SELECT MAX(singer_id) AS max_singer_id FROM singers", null);
        resultSet.moveToFirst();

        if(resultSet.getCount() > 0) {
            maxId = resultSet.getInt(resultSet.getColumnIndex("max_singer_id"));
        }

        return maxId;
    }


    // COMPOSERS
    public void deleteComposers(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("composers", null, null);
        db.close();
    }

    public void insertComposers(int composerId, String composerName, int numberOfSongs, int numberOfAlbums, int numberOfMovies){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("composer_id", composerId);
        cv.put("composer_name", composerName);
        cv.put("number_of_songs", numberOfSongs);
        cv.put("number_of_albums", numberOfAlbums);
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
                    jsonObject.put("numberOfAlbums", resultSet.getString(resultSet.getColumnIndex("number_of_albums")));
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

    public int composersMaxId(){
        int maxId = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet = db.rawQuery("SELECT MAX(composer_id) AS max_composer_id FROM composers", null);
        resultSet.moveToFirst();

        if(resultSet.getCount() > 0) {
            maxId = resultSet.getInt(resultSet.getColumnIndex("max_composer_id"));
        }

        return maxId;
    }


    // ALBUMS
    public void deleteAlbums(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("albums", null, null);
        db.close();
    }

    public void insertAlbums(int albumId, String albumName, String year, int numberOfSongs){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("album_id", albumId);
        cv.put("album_name", albumName);
        cv.put("year", year);
        cv.put("number_of_songs", numberOfSongs);

        db.insert("albums", null, cv);
        db.close();
    }

    public JSONArray selectAlbums(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet = db.rawQuery("SELECT * FROM albums", null);
        resultSet.moveToFirst();

        JSONArray jsonArray = new JSONArray();

        if(resultSet.getCount() > 0){
            int i = 0;
            while(i < resultSet.getCount()){
                try {
                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put("albumId", resultSet.getString(resultSet.getColumnIndex("album_id")));
                    jsonObject.put("albumName", resultSet.getString(resultSet.getColumnIndex("album_name")));
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

    public int albumsMaxId(){
        int maxId = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet = db.rawQuery("SELECT MAX(album_id) AS max_album_id FROM albums", null);
        resultSet.moveToFirst();

        if(resultSet.getCount() > 0) {
            maxId = resultSet.getInt(resultSet.getColumnIndex("max_album_id"));
        }

        return maxId;
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

    public int moviesMaxId(){
        int maxId = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet = db.rawQuery("SELECT MAX(movie_id) AS max_movie_id FROM movie", null);
        resultSet.moveToFirst();

        if(resultSet.getCount() > 0) {
            maxId = resultSet.getInt(resultSet.getColumnIndex("max_movie_id"));
        }

        return maxId;
    }


//     SONG MASTER
//    public void deleteSongMaster(){
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete("song_master", null, null);
//        db.close();
//    }
//
//    public void insertSongMaster(int songId, String title, String artistName, String composerName, String albumName, String movieName, String duration, String songLink){
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put("song_id", songId);
//        cv.put("title", title);
//        cv.put("artist_name", artistName);
//        cv.put("composer_name", composerName);
//        cv.put("album_name", albumName);
//        cv.put("movie_name", movieName);
//        cv.put("duration", duration);
//        cv.put("song_link", songLink);
//
//        db.insert("song_master", null, cv);
//        db.close();
//    }
//
//    public JSONArray selectSongMaster(){
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor resultSet = db.rawQuery("SELECT * FROM song_master", null);
//        resultSet.moveToFirst();
//
//        JSONArray jsonArray = new JSONArray();
//
//        if(resultSet.getCount() > 0){
//            try {
//                JSONObject jsonObject = new JSONObject();
//
//                jsonObject.put("songId", resultSet.getString(resultSet.getColumnIndex("song_id")));
//                jsonObject.put("title", resultSet.getString(resultSet.getColumnIndex("title")));
//                jsonObject.put("artistName", resultSet.getString(resultSet.getColumnIndex("artist_name")));
//                jsonObject.put("composerName", resultSet.getString(resultSet.getColumnIndex("composer_name")));
//                jsonObject.put("albumName", resultSet.getString(resultSet.getColumnIndex("album_name")));
//                jsonObject.put("movieName", resultSet.getString(resultSet.getColumnIndex("movie_name")));
//                jsonObject.put("duration", resultSet.getString(resultSet.getColumnIndex("duration")));
//                jsonObject.put("songLink", resultSet.getString(resultSet.getColumnIndex("song_link")));
//
//                jsonArray.put(jsonObject);
//            }catch(JSONException e){
//                e.printStackTrace();
//            }
//        }
//
//        resultSet.close();
//
//        return  jsonArray;
//    }


    // PLAYLISTS
    public void insertPlaylists(int playlistId, String playlistTitle, int numberOfSongs, String createdOn, int isLocal){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("playlist_id", playlistId);
        cv.put("playlist_title", playlistTitle);
        cv.put("number_of_songs", numberOfSongs);
        cv.put("created_on", createdOn);
        cv.put("is_local", isLocal);

        db.insert("playlists", null, cv);
        db.close();
    }

    public JSONArray selectPlaylists(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet = db.rawQuery("SELECT playlist_id, playlist_title, number_of_songs, created_on " +
                                        "FROM playlists" +
                                        " ORDER BY playlist_id, playlist_title", null);
        resultSet.moveToFirst();

        JSONArray jsonArray = new JSONArray();

        if(resultSet.getCount() > 0){
            int i = 0;
            while(i < resultSet.getCount()){
                try {
                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put("playlistId", resultSet.getInt(resultSet.getColumnIndex("playlist_id")));
                    jsonObject.put("playlistTitle", resultSet.getString(resultSet.getColumnIndex("playlist_title")));
                    jsonObject.put("numberOfSongs", resultSet.getInt(resultSet.getColumnIndex("number_of_songs")));
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

    public int playlistLocalMaxId(){
        int maxId = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet = db.rawQuery("SELECT MAX(playlist_id) AS max_local_playlist_id FROM playlists WHERE is_local = 1", null);
        resultSet.moveToFirst();

        if(resultSet.getCount() > 0) {
            maxId = resultSet.getInt(resultSet.getColumnIndex("max_local_playlist_id"));
        }

        return maxId;
    }

    public void deleteFromPlaylists(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("playlists", "is_local = 0", null);
        db.close();
    }


    // PLAYLISTS SONGS
    public void deletePlaylistSongs(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("playlists_songs", null, null);
        db.close();
    }

    public void insertPlaylistSongs(int playlist_id, int song_id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("playlist_id", playlist_id);
        cv.put("song_id", song_id);

        db.insert("playlists_songs", null, cv);
        db.close();
    }

    public JSONArray selectPlaylistSongs(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet = db.rawQuery("SELECT * FROM playlists_songs", null);
        resultSet.moveToFirst();

        JSONArray jsonArray = new JSONArray();

        if(resultSet.getCount() > 0){
            try {
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("playlistId", resultSet.getString(resultSet.getColumnIndex("playlist_id")));
                jsonObject.put("songId", resultSet.getString(resultSet.getColumnIndex("song_id")));

                jsonArray.put(jsonObject);
            }catch(JSONException e){
                e.printStackTrace();
            }
        }

        resultSet.close();

        return  jsonArray;
    }


    // FAVOURITES
    public void insertFavourites(int songId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("song_id", songId);

        db.insert("favourites", null, cv);
        db.close();
    }

    public JSONArray selectFavourites(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet = db.rawQuery("SELECT sm.song_id, sm.title, sm.album_name, sm.movie_name," +
                                    "sm.album_singer, sm.movie_singer, sm.year, sm.duration FROM song_master sm" +
                                    "INNER JOIN favourites f ON f.song_id = sm.song_id " +
                                    "ORDER BY f.id, sm.title ASC", null);
        resultSet.moveToFirst();

        JSONArray jsonArray = new JSONArray();

        if(resultSet.getCount() > 0){
            try {
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("songId", resultSet.getString(resultSet.getColumnIndex("song_id")));
                jsonObject.put("title", resultSet.getString(resultSet.getColumnIndex("title")));
                jsonObject.put("albumName", resultSet.getString(resultSet.getColumnIndex("album_name")));
                jsonObject.put("movieName", resultSet.getString(resultSet.getColumnIndex("movie_name")));
                jsonObject.put("albumSinger", resultSet.getString(resultSet.getColumnIndex("album_singer")));
                jsonObject.put("movieSinger", resultSet.getString(resultSet.getColumnIndex("movie_singer")));
                jsonObject.put("year", resultSet.getString(resultSet.getColumnIndex("year")));
                jsonObject.put("duration", resultSet.getString(resultSet.getColumnIndex("duration")));

                jsonArray.put(jsonObject);
            }catch(JSONException e){
                e.printStackTrace();
            }
        }

        resultSet.close();

        return  jsonArray;
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
        Cursor resultSet = db.rawQuery("SELECT COUNT(*)" +
                                            "FROM favourites f " +
                                            "WHERE f.song_id = ?", args);
        resultSet.moveToFirst();

        if(resultSet.getCount() > 0){
            resultSet.close();
            return true;
        }

        resultSet.close();

        return false;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // As of now no upgrading!
    }
}
