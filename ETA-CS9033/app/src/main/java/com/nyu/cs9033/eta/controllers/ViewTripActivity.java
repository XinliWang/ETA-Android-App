package com.nyu.cs9033.eta.controllers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.db.TripDatabaseHelper;
import com.nyu.cs9033.eta.models.Trip;

import java.text.SimpleDateFormat;

public class ViewTripActivity extends Activity {

	private static final String TAG = "ViewTripActivity";
    private static final int CUR_TRIP = 2;
	private Trip trip;
    private TripDatabaseHelper helper;
    private Button startButton;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO - fill in here
        setContentView(R.layout.activity_viewtrip);
		trip = getTrip(getIntent());
		viewTrip(trip);

       initStartButton();



	}
	
	/**
	 * Create a Trip object via the recent trip that
	 * was passed to TripViewer via an Intent.
	 * 
	 * @param i The Intent that contains
	 * the most recent trip data.
	 * 
	 * @return The Trip that was most recently
	 * passed to TripViewer, or null if there
	 * is none.
	 */
	public Trip getTrip(Intent i) {

		// TODO - fill in here
        helper = new TripDatabaseHelper(this);
		String tripName = i.getStringExtra("specificTrip");
        if(tripName!=null && tripName.length()>0){
            trip = helper.getTrip(tripName);
            return trip;
        }else{
            return null;
        }
	}

	/**
	 * Populate the View using a Trip model.
	 * 
	 * @param trip The Trip model used to
	 * populate the View.
	 */
	public void viewTrip(Trip trip) {

		// TODO - fill in here
        if(trip!=null){

            TextView text_name = (TextView)findViewById(R.id.name);
            TextView text_description = (TextView)findViewById(R.id.destination);
            TextView text_time = (TextView)findViewById(R.id.time);
            TextView text_friends = (TextView)findViewById(R.id.friends);
            text_name.setText(trip.getName());
            text_description.setText(trip.getDestination());

            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            text_time.setText(ft.format(trip.getTime().getTime()));
            text_friends.setText(trip.convertListToString(trip.getFriends()));
        }else{
            new AlertDialog.Builder(this)
                    .setTitle("Alerting Message")
                    .setMessage("You have no trip!")
                    .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        }
	}

    private void initStartButton(){
        startButton = (Button)findViewById(R.id.button_startTrip);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewTripActivity.this,MainActivity.class);
                intent.putExtra("currentTrip",trip);
                startActivityForResult(intent, CUR_TRIP);
            }
        });
    }
}
