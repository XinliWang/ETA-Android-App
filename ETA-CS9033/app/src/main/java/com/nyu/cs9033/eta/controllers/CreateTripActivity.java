package com.nyu.cs9033.eta.controllers;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.models.Trip;

import java.util.ArrayList;
import java.util.Calendar;

public class CreateTripActivity extends Activity{
	
	private static final String TAG = "CreateTripActivity";
    private static final int CREATE_CODE = 1;
    private static final int SEARCH_LOCATION = 2;
    private static final int PICK_CONTACT = 3;
    private final Uri URI_HW3API = Uri.parse("location://com.example.nyu.hw3api");
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
    private Calendar calendar;
    private Trip newTrip;
    private ListView myListView;





	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO - fill in here
        setContentView(R.layout.activity_createtrip);

        //fill in date
        calendar = Calendar.getInstance();
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

        //fill in time
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

        //add friend from contact book
        addContactButton = (Button) findViewById(R.id.buttonContact);
        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);

            }
        });

        //initialize some view
        trip_friends = (TextView) findViewById(R.id.friends);
        trip_destination = (EditText) findViewById(R.id.destination);
        location = (TextView) findViewById(R.id.location);

        //search location
        searchButton = (Button) findViewById(R.id.buttonSearch);
        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                searchLocation(view);
               // startActivityForResult(intent,SEARCH_LOCATION);
            }
        });


        //create trip
        createTripButton = (Button)findViewById(R.id.button_createTrip);
        createTripButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                newTrip = createTrip();
                saveTrip(newTrip);
            }
        });

        //cancel trip
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
	public Trip createTrip() {
	
		// TODO - fill in here
		trip_name =(EditText)findViewById(R.id.name);
        String temp_name = trip_name.getText().toString().trim();
        String temp_destination = location.getText().toString().trim();
        String temp_friends = trip_friends.getText().toString().trim();

        String temp_time = trip_time.getText().toString().trim();
        String temp_date = trip_date.getText().toString().trim();

        if(TextUtils.isEmpty(temp_name)||TextUtils.isEmpty(temp_destination)||TextUtils.isEmpty(temp_date)||TextUtils.isEmpty(temp_time)||TextUtils.isEmpty(temp_friends)){
            Toast.makeText(this, "All information are required!", Toast.LENGTH_LONG).show();
            return null;
        }else{
            newTrip = new Trip();
            temp_time = temp_date+" "+temp_time;
            newTrip.setName(temp_name);
            newTrip.setDestination(temp_destination);
            newTrip.setTime(temp_time);
            newTrip.setFriends(temp_friends);
            Toast.makeText(this, "Create trip successfully!", Toast.LENGTH_LONG).show();
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
            Intent intent = new Intent(CreateTripActivity.this,MainActivity.class);
            intent.putExtra("create_trip",trip);
            setResult(CREATE_CODE, intent);
            finish();
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

    //contact book
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

    protected void getContactInfo(Intent intent)
    {

        ContentResolver contact_resolver = getContentResolver();
        Uri contactData = intent.getData();
        Cursor cursor = contact_resolver.query(contactData, null, null, null, null);

        if (cursor.moveToFirst())
        {
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            Log.e("name :***: ", name);
            String contracts = trip_friends.getText().toString();
            trip_friends.setText(contracts.length() == 0? name : contracts + "," + name);
            contactId = null;
            name = null;
        }
        contact_resolver = null;
        cursor.close();

    }//getContactInfo

    protected void searchLocation(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW, URI_HW3API);

        String location = trip_destination.getText().toString();
        if(location!=null && location.length()>0){
            intent.putExtra("searchVal",location);
            startActivityForResult(intent, SEARCH_LOCATION);
        }

    }

    protected void getLocationInfo(Intent intent){
        //get the result
        ArrayList<String> list = intent.getExtras().getStringArrayList("retVal");
        if(!list.isEmpty()){
            String locationInfo = list.get(0) + " : " + list.get(1);
            Log.e("location:",locationInfo);
            location.setText(locationInfo);
        }
    }



}
