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

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // As of now no upgrading!
    }
}
