package gmrr.kidzarea.helper;

/**
 * Created by Rifqi Zuliansyah on 31/03/2016.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteLocationHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteLocationHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "login_kidzarea";

    // Login table name
    private static final String TABLE_LOCATION = "location";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_UID = "uid";    
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_WAKTU = "waktu";

    public SQLiteLocationHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOCATION_TABLE = "CREATE TABLE " + TABLE_LOCATION + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"+ KEY_UID + " TEXT," 
                + KEY_LONGITUDE + " DOUBLE UNIQUE," 
		+ KEY_LATITUDE + " DOUBLE UNIQUE,"
                + KEY_WAKTU + " TEXT" + ")";
        db.execSQL(CREATE_LOCATION_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing location details in database
     * */
    public void addLocation(String uid, double longitude,double latitude, String waktu) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
	values.put(KEY_UID, uid); // unique
        values.put(KEY_LONGITUDE, longitude); // lokasi longitude
        values.put(KEY_LATITUDE, latitude); // lokasi latitude
        values.put(KEY_WAKTU, waktu); // waktu ketika akses tempat

        // Inserting Row
        long id = db.insert(TABLE_LOCATION, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New location inserted into sqlite: " + id);
    }

    /**
     * Getting location data from database
     * */
    public HashMap<String, String> getLocationDetails() {
        HashMap<String, String> location = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOCATION;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
	    location.put("uid", cursor.getString(1));
            location.put("longitude", cursor.getString(2));
	    location.put("latitude", cursor.getString(3));
            location.put("waktu", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return location
        Log.d(TAG, "Fetching location from Sqlite: " + location.toString());

        return location;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteLocations() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_LOCATION, null, null);
        db.close();

        Log.d(TAG, "Deleted all location info from sqlite");
    }

}