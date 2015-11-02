package com.nyu.cs9033.eta.controllers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.models.Trip;

public class MainActivity extends Activity{

	private static final String TAG = "MainActivity";
    private static final int CREATE_CODE = 1;
    private Button createButton;
    private Button viewButton;
    private Trip trip;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// TODO - fill in here
        createButton = (Button)findViewById(R.id.main_createTrip);
        createButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startCreateTripActivity();
            }
        });
        viewButton = (Button)findViewById(R.id.main_viewTripHistory);
        viewButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startViewTripActivity();
            }
        });

	}


	/**
	 * This method should start the
	 * Activity responsible for creating
	 * a Trip.
	 */
	public void startCreateTripActivity() {
		
		// TODO - fill in here
        Intent intent = new Intent(this, CreateTripActivity.class);
        startActivityForResult(intent,CREATE_CODE);

	}
	
	/**
	 * This method should start the
	 * Activity responsible for viewing
	 * a Trip.
	 */
	public void startViewTripActivity() {
		
		// TODO - fill in here
        Intent intent = new Intent(this,TripHistoryActivity.class);
        startActivity(intent);
	}
	
	/**
	 * Receive result from CreateTripActivity here.
	 * Can be used to save instance of Trip object
	 * which can be viewed in the ViewTripActivity.
	 * 
	 * Note: This method will be called when a Trip
	 * object is returned to the main activity. 
	 * Remember that the Trip will not be returned as
	 * a Trip object; it will be in the persisted
	 * Parcelable form. The actual Trip object should
	 * be created and saved in a variable for future
	 * use, i.e. to view the trip.
	 * 
	 */
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		// TODO - fill in here
//        if(resultCode==CREATE_CODE){
//            trip = data.getParcelableExtra("create_trip");
//        }else if(resultCode==RESULT_CANCELED){
//            Toast.makeText(this, "Pause to create trip!", Toast.LENGTH_LONG).show();
//        }
//	}

}
