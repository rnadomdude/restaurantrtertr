package com.sp.vending;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class assignmentsql extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "restaurantlist.db";
    private static final int SCHEMA_VERSION = 1;

    public assignmentsql(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE restaurants_table (_id INTEGER PRIMARY KEY AUTOINCREMENT, restaurantName TEXT, " +
                "restaurantAddress TEXT, restaurantTel TEXT, " +
                "lat REAL, lon REAL, image LONGBLOB);");
    }
     @Override
     public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

     }

     public Cursor getAll() {
        return (getReadableDatabase().rawQuery(
                "SELECT _id, restaurantName, restaurantAddress, restaurantTel," +
                        "lat, lon, image FROM restaurants_table ORDER BY restaurantName" , null));
     }

     public Cursor getById(String id) {
        String[] args = {id};

        return (getReadableDatabase().rawQuery(
                "SELECT _id, restaurantName, restaurantAddress, restaurantTel, " +
                        "lat, lon, image FROM restaurants_table WHERE _ID = ?", args));
     }

     public void insert(String restaurantName, String restaurantAddress, String restaurantTel, double lat, double lon, byte[] image) {
         ContentValues cv=new ContentValues();

         cv.put("restaurantName", restaurantName);
         cv.put("restaurantAddress", restaurantAddress);
         cv.put("restaurantTel", restaurantTel);
         cv.put("lat", lat);
         cv.put("lon", lon);
         cv.put("image", image);

         getWritableDatabase().insert("restaurants_table", "restaurantName", cv);
     }

     public void update(String id, String restaurantName, String restaurantAddress, String  restaurantTel, double lat, double lon, byte[] image) {
        ContentValues cv = new ContentValues();
        String[]  args  = {id};
        cv.put("restaurantName", restaurantName);
        cv.put("restaurantAddress", restaurantAddress);
        cv.put("restaurantTel", restaurantTel);
        cv.put("lat", lat);
        cv.put("lon", lon);
        cv.put("image", image);

        getWritableDatabase().update("restaurants_table", cv, " _ID = ?", args);
     }
     public String getID(Cursor c) {return (c.getString(0));}

     public String getRestaurantName(Cursor c) {
        return(c.getString(1));
     }

    public String getRestaurantAddress(Cursor c) {
        return(c.getString(2));
    }

    public String getRestaurantTel(Cursor c) { return(c.getString(3));}


    public double getLatitude(Cursor c) { return (c.getDouble(4));}

    public double getLongitude(Cursor c) { return (c.getDouble(5));}

    public byte[] getImage(Cursor c) {return (c.getBlob(6));}

}
