package com.nyu.cs9033.eta.controllers;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.db.TripDatabaseHelper;
import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.util.CurrentLocationUtil;
import com.nyu.cs9033.eta.util.JsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends Activity{

	private static final String TAG = "MainActivity";
    private static final int CREATE_CODE = 1;
    private static final int CUR_TRIP = 2;

    private static final String URL_SERVER = "http://cs9033-homework.appspot.com/";
    private Button createButton;
    private Button viewButton;
    private Button arriveButton;
    private LinearLayout currentInfo;
    private TextView nameView;
    private TextView destView;
    private TextView timeView;
    private TextView friendView;
    private TextView otherDetail;
    private Trip trip = new Trip();
    private TripDatabaseHelper helper;
    CurrentLocationUtil currentLocationUtil;
    Calendar calendar = Calendar.getInstance();
    Messenger service = null;
    StringBuilder friends = new StringBuilder();
    boolean isBound;
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        // Enable Local Datastore.
        //Parse.enableLocalDatastore(this);
        //Parse.initialize(this, "V1scd1EqtMZLCC7y0f0wQxdAgBLGVLL4BHDXPNSi", "rhERO6RWevEG7AdTieTQSBY7mnkd7MhhODtgpire");

		// TODO - fill in here
        createButton = (Button)findViewById(R.id.main_createTrip);
        createButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startCreateTripActivity();
            }
        });

        viewButton = (Button)findViewById(R.id.main_viewTripHistory);
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startViewTripActivity();
            }
        });

        arriveButton = (Button)findViewById(R.id.main_arrive);
        arriveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //stopService(new Intent(MainActivity.this,CurrentLocationUtil.class));
                doUnbindService();
                Toast.makeText(MainActivity.this, "I have arrived!", Toast.LENGTH_LONG).show();
            }
        });

        trip = getCurInfo(getIntent());

        if(trip!=null){
            //get the other friends' information
            new HttpAsyncTask().execute(URL_SERVER);
            //startService(new Intent(this, CurrentLocationUtil.class));
            checkServiceIsRunning();
        }

	}

	/**
	 * This method should start the
	 * Activity responsible for creating
	 * a Trip.
	 */
	public void startCreateTripActivity() {
		
		// TODO - fill in here
        Intent intent = new Intent(this, CreateTripActivity.class);
        startActivityForResult(intent, CREATE_CODE);

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
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO - fill in here



	}

    private Trip getCurInfo(Intent intent){
        trip = intent.getParcelableExtra("currentTrip");
        helper = new TripDatabaseHelper(this);
        if(trip!=null){
            Log.i("TRip ID4:",String.valueOf(trip.getName()));
            currentInfo =(LinearLayout)findViewById(R.id.currentInfo);
            currentInfo.setVisibility(View.VISIBLE);
            nameView = (TextView)findViewById(R.id.name);
            destView = (TextView)findViewById(R.id.destination);
            timeView = (TextView)findViewById(R.id.time);
            friendView = (TextView)findViewById(R.id.friends);
            nameView.append(trip.getName());
            destView.append(trip.getDestination());
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            timeView.append(ft.format(trip.getTime().getTime()));


            List<String> list = new ArrayList<String>();
            list = helper.getPersons(trip.getId());
            for(String person:list){
                friends.append(person+"\n");
            }

            friendView.append(friends.toString());
            Log.i("id:",String.valueOf(trip.getId()));

        }
        return trip;
    }

//    Handler timerHandler = new Handler();
//    Runnable timerRunnable = new Runnable() {
//        @Override
//        public void run() {
//            // other stuff
//            getInforDetails();
//           // getOtherDetails();
//            timerHandler.postDelayed(this, 1000); }
//    };





    private String getInforDetails(String url){
        double mylat = currentLocationUtil.getLatitude();
        double mylong = currentLocationUtil.getLongitude();
        JSONObject curObj = new JSONObject();
        String string = "";
        //myDetail=(TextView)findViewById(R.id.myDetail);
        try {
            curObj.put("command","UPDATE_LOCATION");
            curObj.put("latitude",mylat);
            curObj.put("longitude", mylong);
            curObj.put("datetime", calendar.getTimeInMillis());
            JsonUtil jsonUtil = new JsonUtil();
            jsonUtil.connectServer(url,curObj);
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            string = "Latitude:"+mylat+"\nLongitude:"+mylong+"\nUpdate Time:"+ft.format(calendar.getTime());
            //myDetail.setText("Latitude:"+mylat+"\nLongitude:"+mylong+"\nUpdate Time:"+ft.format(calendar.getTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return string;
    }



    private String convertJsonToString(String string){
        StringBuilder builder = new StringBuilder("");
        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray distance_left = jsonObject.getJSONArray("distance_left");
            JSONArray time_left = jsonObject.getJSONArray("time_left");
            JSONArray people = jsonObject.getJSONArray("people");
            for(int i=0;i<people.length();i++){
                builder.append("Name:"+people.getString(i)+"\nDistance Left:"+
                        distance_left.getDouble(i)+"miles\nTime Left:"+time_left.getDouble(i)/60+"minutes\n");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }




private class HttpAsyncTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String...params){
        JSONObject obj = new JSONObject();
        JsonUtil jsonUtil = new JsonUtil();
        try {
            obj.put("command", "TRIP_STATUS");
            obj.put("trip_id", trip.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("obj:",obj.toString());
        return jsonUtil.connectServer(params[0],obj);
    }

    @Override
    protected void onPostExecute(String result){
        Log.i("result:",result);
        otherDetail = (TextView)findViewById(R.id.otherDetail);
        otherDetail.setText(convertJsonToString(result));
    }

}

    /**
     * The method start or stop service about get current location in per minutes
     */
    private boolean checkServiceIsRunning(){
        if (CurrentLocationUtil.isRunning()){
            doBindService();
            return true;
        }else{
            return false;
        }
    }

    void doBindService() {
        /**
         *Establish a connection with the service. We use an explicit
         *class name because there is no reason to be able to let other
         * applications interact with our component.
         */

        bindService(new Intent(MainActivity.this, CurrentLocationUtil.class),
                connection, // ServiceConnection object
                Context.BIND_AUTO_CREATE); // Create service if not

        isBound = true;

    }

    void doUnbindService() {
        if (isBound) {
            // If we registered with the service, then now is the time to unregister.
            if (service != null) {
                try {
                    Message msg = Message.obtain(null, CurrentLocationUtil.MSG_UNREGISTER_CLIENT);
                    service.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service has crashed.
                }
            }
            // Detach our existing connection.
            unbindService(connection);
            isBound = false;
        }
    }


    /**
     * The method communicates with service
     */
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service = new Messenger(iBinder);
            Message message = Message.obtain(null,CurrentLocationUtil.MSG_REGISTER_CLIENT);

            try {
                service.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            service = null;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            doUnbindService();
            stopService(new Intent(MainActivity.this, CurrentLocationUtil.class));
            helper.close();
        } catch (Throwable t) {
            Log.e("MainActivity", "Failed to unbind from the service", t);
        }
    }

}
