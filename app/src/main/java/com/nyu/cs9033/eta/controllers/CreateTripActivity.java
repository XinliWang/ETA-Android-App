package com.nyu.cs9033.eta.controllers;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.models.Trip;

import java.util.Calendar;

public class CreateTripActivity extends Activity{
	
	private static final String TAG = "CreateTripActivity";
    private static final int CREATE_CODE = 1;
    private static final int PICK_CONTACT = 3;
	private Button createTripButton;
	private Button cancelTripButton;
    private EditText trip_name;
    private EditText trip_destination;
    private EditText trip_date;
    private EditText trip_time;
    private EditText trip_friends;
    private Calendar calendar;
    private Trip newTrip;

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


        trip_friends = (EditText) findViewById(R.id.friends);
        trip_friends.setFocusable(false);
        trip_friends.setClickable(true);
        trip_friends.setInputType(InputType.TYPE_NULL);
        trip_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
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
        trip_destination = (EditText)findViewById(R.id.destination);
        trip_friends = (EditText)findViewById(R.id.friends);
        String temp_name = trip_name.getText().toString().trim();
        String temp_destination = trip_destination.getText().toString().trim();
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

        if (requestCode == PICK_CONTACT)
        {
            getContactInfo(intent);
            // Your class variables now have the data, so do something with it.
        }
    }//onActivityResult

    protected void getContactInfo(Intent intent)
    {

        ContentResolver contact_resolver = getContentResolver();
        Cursor cursor = contact_resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "=?", new String[]{}, null);

        if (cursor.moveToFirst())
        {
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = "";
            String phone = "";
            Cursor phoneCur = contact_resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{contactId}, null);

            if (phoneCur.moveToFirst()) {
                name = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                phone = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
            Log.e("Phone number & name :***: ", name + " : " + phone);
            trip_friends.append(name + " : " + phone + "\n");

            contactId = null;
            name = null;
            phone = null;
            phoneCur = null;
        }
        contact_resolver = null;
        cursor = null;

    }//getContactInfo




}
