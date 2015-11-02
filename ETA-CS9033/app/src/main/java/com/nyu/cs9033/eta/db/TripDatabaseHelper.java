package com.nyu.cs9033.eta.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import com.nyu.cs9033.eta.models.Trip;

import java.util.ArrayList;
import java.util.List;



public class TripDatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "trips";

    private static final String TABLE_TRIP = "trip";
    private static final String COLUMN_TRIP_ID = "_id"; // convention
    private static final String COLUMN_TRIP_NAME = "name";
    private static final String COLUMN_TRIP_TIME = "time";
    private static final String COLUMN_TRIP_FRIENDS = "friends";
    private static final String COLUMN_TRIP_DESTINATION = "destination";

    private static final String TABLE_LOCATION = "location";
    private static final String COLUMN_LOC_TRIPID = "trip_id";
    private static final String COLUMN_LOC_TIMESTAMP = "timestamp";
    private static final String COLUMN_LOC_LAT = "latitude";
    private static final String COLUMN_LOC_LONG = "longitude";
    private static final String COLUMN_LOC_ALT = "altitude";
    private static final String COLUMN_LOC_PROVIDER = "provider";


    public TripDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //create trip table
        String tripTable = "create table " + TABLE_TRIP + "("
            + COLUMN_TRIP_ID + " integer primary key autoincrement, "
            + COLUMN_TRIP_NAME + " text, "
            + COLUMN_TRIP_TIME + " text, "
            + COLUMN_TRIP_DESTINATION + " text, "
            + COLUMN_TRIP_FRIENDS + " text)";

        db.execSQL(tripTable);

        // create location table
        String locationTable = "create table " + TABLE_LOCATION + "("
            + COLUMN_LOC_TRIPID + " integer references trip(_id), "
            + COLUMN_LOC_TIMESTAMP + " integer, "
            + COLUMN_LOC_LAT + " real, "
            + COLUMN_LOC_LONG + " real, "
            + COLUMN_LOC_ALT + " real, "
            + COLUMN_LOC_PROVIDER + " varchar(100))";

        db.execSQL(locationTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        // create tables again
        onCreate(db);

    }

    public long insertTrip(Trip trip) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TRIP_NAME,trip.getName());
        cv.put(COLUMN_TRIP_TIME, trip.getTime());
        cv.put(COLUMN_TRIP_DESTINATION, trip.getDestination());
        cv.put(COLUMN_TRIP_FRIENDS, trip.getFriends());
        // return id of new trip
        return getWritableDatabase().insert(TABLE_TRIP, null, cv);
    }

    public long insertLocation(long tripId, Location location) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LOC_TRIPID, tripId);
        cv.put(COLUMN_LOC_TIMESTAMP, location.getTime());
        cv.put(COLUMN_LOC_LAT, location.getLatitude());
        cv.put(COLUMN_LOC_LONG, location.getLongitude());
        cv.put(COLUMN_LOC_ALT, location.getAltitude());
        cv.put(COLUMN_LOC_PROVIDER, location.getProvider());
        // return id of new location
        return getWritableDatabase().insert(TABLE_LOCATION, null, cv);
    }

    public List<Trip> getAllTrips() {
        List<Trip> tripList = new ArrayList<Trip>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_TRIP, null);

        // loop through all query results
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Trip trip = new Trip();
            trip.setId(cursor.getInt(0));
            trip.setName(cursor.getString(1));
            trip.setTime(cursor.getString(2));
            trip.setDestination(cursor.getString(3));
            trip.setFriends(cursor.getString(4));
            tripList.add(trip);
        }
        return tripList;
    }

    public List<String> getAllTripsName() {
        List<String> tripNameList = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select name from " + TABLE_TRIP, null);

        // loop through all query results
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            tripNameList.add(cursor.getString(0));
        }
        return tripNameList;
    }

    public Trip getTrip(String tripName) {
        Trip trip = new Trip();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_TRIP + " where name = ?; ", new String[]{tripName});

        // loop through all query results
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            trip.setId(cursor.getInt(0));
            trip.setName(cursor.getString(1));
            trip.setTime(cursor.getString(2));
            trip.setDestination(cursor.getString(3));
            trip.setFriends(cursor.getString(4));
        }
        return trip;
    }



}
