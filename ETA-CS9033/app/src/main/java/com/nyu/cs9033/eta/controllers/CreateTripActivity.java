package com.nyu.cs9033.eta.controllers;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.db.TripDatabaseHelper;
import com.nyu.cs9033.eta.models.Person;
import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.util.JsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CreateTripActivity extends Activity{
	
	private static final String TAG = "CreateTripActivity";
    private static final int CREATE_CODE = 1;
    private static final int SEARCH_LOCATION = 2;
    private static final int PICK_CONTACT = 3;
    private final Uri URI_HW3API = Uri.parse("location://com.example.nyu.hw3api");
    private static final String URL_SERVER = "http://cs9033-homework.appspot.com/";
    private Button searchButton;
    private Button addContactButton;
	private Button createTripButton;
	private Button cancelTripButton;
    private EditText trip_name;
    private EditText trip_destination;
    private EditText trip_date;
    private EditText trip_time;
    private TextView trip_friends;
    private TextView location;
    private Calendar calendar = Calendar.getInstance();
    private Calendar cal;
    private Trip newTrip = new Trip();
    private JSONObject object = new JSONObject();
    private ArrayList<String> contactsName;
    private ArrayList<String> contactsPhone;
    private ArrayList<Person> friends;
    private ArrayList<String> list;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO - fill in here
        setContentView(R.layout.activity_createtrip);

        //initialize some view
        trip_friends = (TextView) findViewById(R.id.friends);
        trip_destination = (EditText) findViewById(R.id.destination);
        location = (TextView) findViewById(R.id.location);
        trip_name =(EditText)findViewById(R.id.name);
        contactsName = new ArrayList<String>();
        contactsPhone = new ArrayList<String>();
        friends = new ArrayList<Person>();

        //fill in date
        initTripDate();

        //fill in time
        initTripTime();

        //add friend from contact book
        initContactBook();

        //search location
        initSearchLocation();

        //create trip
        initCreateTrip();

        //cancel trip
        initCancelTrip();

	}

    private void initTripDate(){
        trip_date = (EditText)findViewById(R.id.date);
        trip_date.setFocusable(false);
        trip_date.setClickable(true);
        trip_date.setInputType(InputType.TYPE_NULL);
        trip_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(CreateTripActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker dp, int year,
                                                  int month, int dayOfMonth) {
                                trip_date.setText(year + "-" + (month + 1) + "-"
                                        + dayOfMonth);
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar
                        .get(Calendar.DAY_OF_MONTH)).show();

            }
        });
    }

    private void initTripTime(){
        trip_time = (EditText) findViewById(R.id.time);
        trip_time.setFocusable(false);
        trip_time.setClickable(true);
        trip_time.setInputType(InputType.TYPE_NULL);
        trip_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(CreateTripActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view,
                                                  int hourOfDay, int minute) {
                                trip_time.setText(hourOfDay + ":" + minute);
                            }
                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                        true).show();
            }
        });
    }

    private void initContactBook(){
        addContactButton = (Button) findViewById(R.id.buttonContact);
        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);

            }
        });
    }

    private void initSearchLocation(){
        searchButton = (Button) findViewById(R.id.buttonSearch);
        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                searchLocation(view);
                // startActivityForResult(intent,SEARCH_LOCATION);
            }
        });
    }

    private void initCreateTrip(){
        createTripButton = (Button)findViewById(R.id.button_createTrip);
        createTripButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String temp_name = trip_name.getText().toString().trim();
                String temp_destination = location.getText().toString().trim();
                String temp_friends = trip_friends.getText().toString().trim();

                String temp_time = trip_time.getText().toString().trim();
                String temp_date = trip_date.getText().toString().trim();

                if(TextUtils.isEmpty(temp_name)||TextUtils.isEmpty(temp_destination)||TextUtils.isEmpty(temp_date)||TextUtils.isEmpty(temp_time)||TextUtils.isEmpty(temp_friends)){
                    Toast.makeText(CreateTripActivity.this, "All information are required!", Toast.LENGTH_LONG).show();
                }else {
                    temp_time = temp_date+" "+temp_time;
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    cal = Calendar.getInstance();
                    try {
                        Date startDate = df.parse(temp_time);
                        cal.setTime(startDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    try {
                        //set the json object
                        object.put("command", "CREATE_TRIP");
                        object.put("location", list);
                        object.put("datetime", cal.getTimeInMillis());
                        object.put("people", newTrip.converToListString(friends));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(isOnline()){
                        //when everything is done, we call this method to send and get data from server
                        new HttpAsyncTask().execute(URL_SERVER);
                    }else{
                        Toast.makeText(getBaseContext(),
                                "Your network is not connected!", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            }
        });
    }

    private void initCancelTrip(){
        cancelTripButton = (Button)findViewById(R.id.button_cancelTrip);
        cancelTripButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                cancelTrip();
            }
        });
    }

	/**
	 * This method should be used to
	 * instantiate a Trip model object.
	 * 
	 * @return The Trip as represented
	 * by the View.
	 */
	public Trip createTrip() throws JSONException {
	
		// TODO - fill in here

        String temp_name = trip_name.getText().toString().trim();
        String temp_destination = location.getText().toString().trim();
        String temp_friends = trip_friends.getText().toString().trim();

        String temp_time = trip_time.getText().toString().trim();
        String temp_date = trip_date.getText().toString().trim();

        if(TextUtils.isEmpty(temp_name)||TextUtils.isEmpty(temp_destination)||TextUtils.isEmpty(temp_date)||TextUtils.isEmpty(temp_time)||TextUtils.isEmpty(temp_friends)){
            Toast.makeText(this, "All information are required!", Toast.LENGTH_LONG).show();
            return null;
        }else{

            newTrip.setName(temp_name);
            newTrip.setDestination(temp_destination);
            newTrip.setTime(cal);
            //newTrip.setFriends(friends);
            return newTrip;
        }

	}

	/**
	 * For HW2 you should treat this method as a 
	 * way of sending the Trip data back to the
	 * main Activity.
	 * 
	 * Note: If you call finish() here the Activity 
	 * will end and pass an Intent back to the
	 * previous Activity using setResult().
	 * 
	 * @return whether the Trip was successfully 
	 * saved.
	 */
	public boolean saveTrip(Trip trip) {
	
		// TODO - fill in here
        if(trip!=null){
            TripDatabaseHelper helper = new TripDatabaseHelper(this);
            Log.i("saveID:",String.valueOf(trip.getId()));
            helper.insertTrip(trip);
            helper.insertLocation(trip.getId(), list);
            helper.insertPerson(trip.getId(),friends);
            finish();
            Toast.makeText(this, "Create trip successfully!", Toast.LENGTH_LONG).show();
            return true;
        }else{
            return false;
        }

	}

	/**
	 * This method should be used when a
	 * user wants to cancel the creation of
	 * a Trip.
	 * 
	 * Note: You most likely want to call this
	 * if your activity dies during the process
	 * of a trip creation or if a cancel/back
	 * button event occurs. Should return to
	 * the previous activity without a result
	 * using finish() and setResult().
	 */
	public void cancelTrip() {
	
		// TODO - fill in here
        Intent intent = new Intent(this,MainActivity.class);
        setResult(RESULT_CANCELED);
        finish();
	}

    /**
     * This method should receive the result from other activities
     * and get the information from that.
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {

            if (intent!=null){
                switch (requestCode){
                    case PICK_CONTACT:
                        getContactInfo(intent);
                        break;

                    case SEARCH_LOCATION:
                        getLocationInfo(intent);
                        break;
                }
            }


    }//onActivityResult

    /**
     * This method get the participators form contact book,
     * if there is no participate,we add it.
     * if there are some participators, we need use "," to combine these names
     */
    protected void getContactInfo(Intent intent)
    {

        ContentResolver contact_resolver = getContentResolver();
        Uri contactData = intent.getData();
        Cursor cursor = contact_resolver.query(contactData, null, null, null, null);

        if (cursor.moveToFirst())
        {
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

            int phoneCount = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            String phone ="";
            if (phoneCount>0){
                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID

                        + " = " + contactId, null, null);

                if(phones.moveToFirst()){
                    do{
                        phone= phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    }while(phones.moveToNext());
                }
            }


            Log.e("name :***: ", name);
            Log.e("phone :***: ", phone);
            if(contactsName.contains(name)&&contactsPhone.contains(phone)){
                Toast.makeText(CreateTripActivity.this, "You have already added this person",Toast.LENGTH_SHORT).show();
                return;
            }else{
                contactsName.add(name);
                contactsPhone.add(phone);

                Person person = new Person();
                person.setName(name);
                person.setPhone(phone);

                friends.add(person);
                String contracts = trip_friends.getText().toString();

                trip_friends.setText(contracts.length() == 0? name : contracts + "," + name);
                contactId = null;
                name = null;
                phone = null;
            }

        }
        contact_resolver = null;
        cursor.close();

    }//getContactInfo


    /**
     * This method should sava and show the specific location's name and address
     * which we have chosen
     */
    protected void getLocationInfo(Intent intent){
        /**
         * 1. get the destination informaiton of location list
         * 2. the structure of list is : name , address, latitude , longitude
         */

        list = intent.getExtras().getStringArrayList("retVal");

        if(!list.isEmpty()){
            String locationInfo = list.get(0) + " : " + list.get(1);
            Log.e("location:", locationInfo);
            location.setText(locationInfo);
        }
    }

    /**
     * This method should send the data which form is “<location>::<location_type>”
     * to the Foursquare API to get the location list
     */
    protected void searchLocation(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW, URI_HW3API);

        String location = trip_destination.getText().toString();
        if(location!=null && location.length()>0){
            intent.putExtra("searchVal", location);
            startActivityForResult(intent, SEARCH_LOCATION);
        }

    }

    /**
     * Sending Http request to get Trip ID from server and save into trip
     */
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String...params){
            //send Json data into server
            JsonUtil jsonUtil = new JsonUtil();
            return jsonUtil.connectServer(params[0],object);
        }

        @Override
        protected void onPostExecute(String result){
            JSONObject response = null;
            try {
                //get the result from server and convert from string to json object
                response = new JSONObject(result);
                if(response.getInt("response_code")==0){
                    //get this trip
                    Trip trip = createTrip();
                    //set the trip id into trip
                    trip.setId(response.getLong("trip_id"));
                    //save this trip data into database
                    saveTrip(trip);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     *This method is used to check the status of the network info.
     */
    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }


}
