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
        super(context, "gps_test_application.sqlite", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating Tables

        // User Master
        String employeeMasterTable = "CREATE TABLE user_master (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                                    "user_id INTEGER, user_name VARCHAR NOT NULL, password VARCHAR NOT NULL)";
        db.execSQL(employeeMasterTable);

        // Travel Master
        String travelMasterTable = "CREATE TABLE travel_master (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "user_id INTEGER NOT NULL, start_location VARCHAR NOT NULL, end_location VARCHAR NOT NULL, " +
                "start_latitude VARCHAR NOT NULL, start_longitude VARCHAR NOT NULL," +
                "end_latitude VARCHAR NOT NULL, end_longitude VARCHAR NOT NULL, distance INTEGER NOT NULL," +
                "start_time VARCHAR NOT NULL, end_time VARCHAR NOT NULL, duration VARCHAR NOT NULL," +
                "force_stopped INTEGER NOT NULL, force_stopped_location VARCHAR NOT NULL," +
                "force_stopped_latitude VARCHAR NOT NULL, force_stopped_longitude VARCHAR NOT NULL, file_name VARCHAR NOT NULL)";
        db.execSQL(travelMasterTable);

    }

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

    public void insertUserIdIntoUserMaster(int userId){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE user_master SET `user_id` = " + userId + " WHERE id = (SELECT MAX(id) FROM user_master)";
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

    public void deleteTravelMaster(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("travel_master", null, null);
        db.close();
    }

    public void insertTravelMaster(int userId, String startLocation, String endLocation,
           String startLatitude, String startLongitude, String endLatitude, String endLongitude,
           int distance, String startTime, String endTime, String duration, int forceStopped,
           String forceStoppedLocation, String forceStoppedLatitude, String forceStoppedLongitude,
                                    String kmlFileName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("user_id", userId);
        cv.put("start_location", startLocation);
        cv.put("end_location", endLocation);
        cv.put("start_latitude", startLatitude);
        cv.put("start_longitude", startLongitude);
        cv.put("end_latitude", endLatitude);
        cv.put("end_longitude", endLongitude);
        cv.put("distance", distance);
        cv.put("start_time", startTime);
        cv.put("end_time", endTime);
        cv.put("duration", duration);
        cv.put("force_stopped", forceStopped);
        cv.put("force_stopped_location", forceStoppedLocation);
        cv.put("force_stopped_latitude", forceStoppedLatitude);
        cv.put("force_stopped_longitude", forceStoppedLongitude);
        cv.put("file_name", kmlFileName);

        db.insert("travel_master", null, cv);
        db.close();
    }

    public JSONArray selectHistory(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resultSet = db.rawQuery("SELECT start_location, end_location, force_stopped, force_stopped_location, distance, duration FROM travel_master ORDER BY id DESC", null);
        resultSet.moveToFirst();

        JSONArray jsonArray = new JSONArray();

        if(resultSet.getCount() > 0){
            int i = 0;
            while(i < resultSet.getCount()) {
//                System.out.println("startLocation : " + resultSet.getString(resultSet.getColumnIndex("start_location")));
//                System.out.println("endLocation : " + resultSet.getString(resultSet.getColumnIndex("end_location")));
//                System.out.println("forceStopped : " + resultSet.getString(resultSet.getColumnIndex("force_stopped")));
//                System.out.println("forceStoppedLocation : " + resultSet.getString(resultSet.getColumnIndex("force_stopped_location")));
//                System.out.println("distance : " + resultSet.getString(resultSet.getColumnIndex("distance")));
//                System.out.println("duration : " + resultSet.getString(resultSet.getColumnIndex("duration")));

                try {
                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put("startLocation", resultSet.getString(resultSet.getColumnIndex("start_location")));
                    jsonObject.put("endLocation", resultSet.getString(resultSet.getColumnIndex("end_location")));
                    jsonObject.put("forceStopped", resultSet.getString(resultSet.getColumnIndex("force_stopped")));
                    jsonObject.put("forceStoppedLocation", resultSet.getString(resultSet.getColumnIndex("force_stopped_location")));
                    jsonObject.put("distance", resultSet.getString(resultSet.getColumnIndex("distance")));
                    jsonObject.put("duration", resultSet.getString(resultSet.getColumnIndex("duration")));

                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                resultSet.moveToNext();
                i++;
            }
        }

        resultSet.close();

        return  jsonArray;
    }


    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // As of now no upgrading!
    }
}
