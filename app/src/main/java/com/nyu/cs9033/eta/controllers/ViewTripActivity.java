package com.nyu.cs9033.eta.controllers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.models.Trip;

public class ViewTripActivity extends Activity {

	private static final String TAG = "ViewTripActivity";
	private Trip trip;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO - fill in here
        setContentView(R.layout.activity_viewtrip);
		Trip trip = getTrip(getIntent());
		viewTrip(trip);
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
		trip = i.getParcelableExtra("create_trip");
        if(trip!=null){
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
            text_time.setText(trip.getTime());
            text_friends.setText(trip.getFriends());
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
}
