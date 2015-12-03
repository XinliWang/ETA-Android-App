package com.nyu.cs9033.eta.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.nyu.cs9033.eta.models.Person;
import com.nyu.cs9033.eta.models.Trip;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;



public class TripDatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "trips";

    private static final String TABLE_TRIP = "trip";
    private static final String COLUMN_TRIP_ID = "trip_id"; // convention
    private static final String COLUMN_TRIP_NAME = "name";
    private static final String COLUMN_TRIP_TIME = "time";
    private static final String COLUMN_TRIP_IS_ACTIVE = "isActive";
    private static final String COLUMN_TRIP_DESTINATION = "destination";

    private static final String TABLE_LOCATION = "location";
    private static final String COLUMN_LOC_TRIPID = "trip_id";
    private static final String COLUMN_LOC_TIMESTAMP = "timestamp";
    private static final String COLUMN_LOC_LAT = "latitude";
    private static final String COLUMN_LOC_LONG = "longitude";
    private static final String COLUMN_LOC_NAME = "name";
    private static final String COLUMN_LOC_ADDRESS = "address";

    private static final String TABLE_PERSON = "person";
    private static final String COLUMN_PERSON_TRIPID = "trip_id";
    private static final String COLUMN_PERSON_NAME = "name";
    private static final String COLUMN_PERSON_PHONE = "phone";
    private static final String COLUMN_PERSON_LAT = "latitude";
    private static final String COLUMN_PERSON_LONG = "longitude";



    public TripDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //create trip table
        String tripTable = "create table if not exists " + TABLE_TRIP + "("
            + COLUMN_TRIP_ID + " integer, "
            + COLUMN_TRIP_NAME + " varchar(100), "
            + COLUMN_TRIP_TIME + " real, "
            + COLUMN_TRIP_DESTINATION + " varchar(100), "
            + COLUMN_TRIP_IS_ACTIVE + " integer)";

        db.execSQL(tripTable);

        //create location table
        String locationTable = "create table if not exists " + TABLE_LOCATION + "("
            + COLUMN_LOC_TRIPID + " integer references trip(_id), "
            + COLUMN_LOC_NAME + " varchar(100), "
            + COLUMN_LOC_ADDRESS + " varchar(100),"
                + COLUMN_LOC_LAT + " varchar(100), "
                + COLUMN_LOC_LONG + " varchar(100))";

        db.execSQL(locationTable);

        //create person table
        String personTable = "create table if not exists " + TABLE_PERSON + "("
                + COLUMN_PERSON_TRIPID + " integer references trip(_id), "
                + COLUMN_PERSON_NAME + " varchar(100), "
                + COLUMN_PERSON_PHONE + " varchar(100),"
                + COLUMN_PERSON_LAT + " varchar(100),"
                + COLUMN_PERSON_LONG + " varchar(100))";

        db.execSQL(personTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERSON);
        // create tables again
        onCreate(db);

    }

    //insert new trip into database
    public long insertTrip(Trip trip) {
        Log.i("Insert ID:", String.valueOf(trip.getId()));
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TRIP_ID,trip.getId());
        cv.put(COLUMN_TRIP_NAME,trip.getName());
        cv.put(COLUMN_TRIP_TIME, trip.getTime().getTimeInMillis());
        cv.put(COLUMN_TRIP_DESTINATION, trip.getDestination());
        cv.put(COLUMN_TRIP_IS_ACTIVE,trip.isActive());

        //cv.put(COLUMN_TRIP_FRIENDS, trip.convertListToString(trip.getFriends()));
        // return id of new trip
        return getWritableDatabase().insert(TABLE_TRIP, null, cv);
    }

    //insert new location into database
    public long insertLocation(long tripId, ArrayList<String> location) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LOC_TRIPID, tripId);
        cv.put(COLUMN_LOC_NAME, location.get(0));
        cv.put(COLUMN_LOC_ADDRESS, location.get(1));
        cv.put(COLUMN_LOC_LAT, location.get(2));
        cv.put(COLUMN_LOC_LONG, location.get(3));


        // return id of new location
        return getWritableDatabase().insert(TABLE_LOCATION, null, cv);
    }

    //insert new contract infor into database
    public void insertPerson(long tripId, ArrayList<Person> contracts) {
        for(Person contract: contracts){
            ContentValues cv = new ContentValues();

            cv.put(COLUMN_PERSON_TRIPID, tripId);
            cv.put(COLUMN_PERSON_NAME, contract.getName());
            cv.put(COLUMN_PERSON_PHONE, contract.getPhone());
            cv.put(COLUMN_PERSON_LAT, contract.getLatitude());
            cv.put(COLUMN_PERSON_LONG, contract.getLongitude());

            // return id of new location
            getWritableDatabase().insert(TABLE_PERSON, null, cv);
        }

    }

    //get all trips list
    public List<Trip> getAllTrips() {
        List<Trip> tripList = new ArrayList<Trip>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_TRIP, null);

        // loop through all query results
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Trip trip = new Trip();
            trip.setId(cursor.getInt(0));
            trip.setName(cursor.getString(1));
            trip.setTime(convertToCalendar(cursor.getLong(2)));
            trip.setDestination(cursor.getString(3));
           // trip.setFriends(trip.convertStringToList(cursor.getString(4)));
            tripList.add(trip);
        }
        cursor.close();
        return tripList;
    }

    //get all trips name list
    public List<String> getAllTripsName() {
        List<String> tripNameList = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select name from " + TABLE_TRIP, null);

        // loop through all query results
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            tripNameList.add(cursor.getString(0));
        }
        cursor.close();
        return tripNameList;
    }

    //get all past trips name list
    public List<String> getPastTripsName() {
        List<String> tripNameList = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Long p = Calendar.getInstance().getTimeInMillis();
        Cursor cursor = db.rawQuery("select name from " + TABLE_TRIP + " where time < ?;", new String[]{String.valueOf(p - p%(24*60*60*1000))});

        // loop through all query results
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            tripNameList.add(cursor.getString(0));
        }
        cursor.close();
        return tripNameList;
    }

    //get all upcoming trips name list
    public List<String> getUpcomingTripsName() {
        List<String> tripNameList = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();

        Long u = Calendar.getInstance().getTimeInMillis();

        Cursor cursor = db.rawQuery("select name from " + TABLE_TRIP + " where time > ?;", new String[]{String.valueOf(u - u%(24*60*60*1000) + 24*60*60*1000)});

        // loop through all query results
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            tripNameList.add(cursor.getString(0));
        }
        cursor.close();
        return tripNameList;
    }

    //get all current trips name list
    public List<String> getCurTripsName() {
        List<String> tripNameList = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();

        Long c = Calendar.getInstance().getTimeInMillis();

        Cursor cursor = db.rawQuery("select name from " + TABLE_TRIP + " where time between ? and ?;", new String[]{String.valueOf(c- c%(24*60*60*1000)),String.valueOf(c- c%(24*60*60*1000) + 24*60*60*1000)});

        // loop through all query results
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            tripNameList.add(cursor.getString(0));
        }
        cursor.close();
        return tripNameList;
    }


    //get the specific trip's detail
    public Trip getTrip(String tripName) {
        Trip trip = new Trip();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_TRIP + " where name = ?; ", new String[]{tripName});

        // loop through all query results
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            trip.setId(cursor.getLong(0));
            trip.setName(cursor.getString(1));
            trip.setTime(convertToCalendar(cursor.getLong(2)));
            trip.setDestination(cursor.getString(3));
            trip.setIsActive(cursor.getInt(4));
          //  trip.setFriends(trip.convertStringToList(cursor.getString(4)));
            Log.i("Data ID:",String.valueOf(cursor.getLong(0)));
        }
        return trip;
    }


    //get the list of person(name and phone) for specific trip
    public List<String> getPersons(long tripId){
        List<String> personsList = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("select name,phone from " + TABLE_PERSON + " where trip_id = ? ;",new String[]{String.valueOf(tripId)});

        // loop through all query results
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            personsList.add(cursor.getString(0)+":"+cursor.getString(1));
        }
        cursor.close();
        return personsList;
    }

    public void updateTripStatus(long tripId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TRIP_IS_ACTIVE,0);
        db.update(TABLE_TRIP,cv,"trip_id = "+ tripId,null);
    }



    /**
     * We use Integer to save calendar into database,
     * when we want to get from the database, we need convert
     * the integer type to calendar type.
     */
    private Calendar convertToCalendar(Long l){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(l);
        return cal;
    }


}
