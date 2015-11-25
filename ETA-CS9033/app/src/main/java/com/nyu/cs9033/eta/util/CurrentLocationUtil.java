package com.nyu.cs9033.eta.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.provider.Settings;
import android.util.Log;

import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.controllers.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wangxinli on 11/16/15.
 */
public class CurrentLocationUtil extends Service implements LocationListener{
    private static final String URL_SERVER = "http://cs9033-homework.appspot.com/";
    //The mininum distance to update location in 50 meters
    private static final long MIN_DISTANCE_FOR_UPDATE = 50;
    //The minimum time to update location in 2 minutes
    private static final long MIN_TIME_FOR_UPDATE = 1000*60*1;

    public static final int MSG_REGISTER_CLIENT = 1;

    public static final int MSG_UNREGISTER_CLIENT =2;

    //flag for GPS status
    boolean isGPSEnabled = false;

    //flag for network status
    boolean isNetworkEnabled = false;

    //flag for current location status
    boolean canGetLocation = false;

    //flag for Service running status
    private static boolean alive = true;

    Location location;
    double latitude;
    double longitude;
    /**set timer to build new thread to send json data to sever periodically*/
    Timer timer = new Timer();

    final Messenger messenger = new Messenger(new IncomingHandler());

    /** For showing and hiding our notification. */
    NotificationManager mNM;
    /** Keeps track current registered clients. */
    Messenger client = null;
    protected LocationManager locationManager;

    public CurrentLocationUtil(){

    }

    public Location getLocation(){
        try{
            locationManager = (LocationManager)this.getSystemService(LOCATION_SERVICE);

            //getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            //getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!isGPSEnabled && !isNetworkEnabled){
                showSettingAlert();
            }else{
                this.canGetLocation = true;
                //if network enabled to get lat/long
                if(isNetworkEnabled){
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME_FOR_UPDATE,MIN_DISTANCE_FOR_UPDATE,this);
                    Log.d("Network","Network");
                    if (locationManager!=null){
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location!=null){
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                //if GPS enabled to get lat/long
                if(isGPSEnabled){
                    if (location == null){
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_FOR_UPDATE,MIN_DISTANCE_FOR_UPDATE,this);
                        Log.d("GPS","GPS");
                        if (locationManager!=null){
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location!=null){
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return location;
    }

    /**
     * Stop using GPS listener Calling function will stop using GPS in your app.
     */
    public void stopUsingGPS(){
        if (locationManager!=null){
            locationManager.removeUpdates(CurrentLocationUtil.this);
        }
    }

    /**
     * Function to get latitude
     */
    public double getLatitude(){
        if (location!=null){
            latitude = location.getLatitude();
        }
        return latitude;
    }
    /**
     * Function to get longitude
     */
    public double getLongitude(){
        if (location!=null){
            latitude = location.getLongitude();
        }
        return longitude;
    }


    /**
     * Function to check GPS/network enabled
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show setting alert dialog on pressing setting button will launch settings option
     */
    public void showSettingAlert(){
        AlertDialog.Builder alerDialog = new AlertDialog.Builder(getApplicationContext());
        //setting dialogHelp title
        alerDialog.setTitle("GPS settings");
        //setting dialogHelp message
        alerDialog.setMessage("GPS is not enabled. Do you want to go to settings option?");

        //on pressing settings button
        alerDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        getApplicationContext().startActivity(intent);
                    }
                });
        //on pressing cancel button
        alerDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface,int i){
                        dialogInterface.cancel();
                    }
                });
        //showing alert message
        alerDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        float bestAccuracy = -1f;
        if (location.getAccuracy()!=0.0f && (location.getAccuracy() < bestAccuracy) || bestAccuracy == -1f){
            locationManager.removeUpdates(this);
        }
        bestAccuracy = location.getAccuracy();

    }

    public float getAccuracy(){
        return location.getAccuracy();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        alive = true;
        return START_STICKY;
    }

    /**
     * After we bind service, we call timer to execute tamerTask periodically
     */
    @Override
    public IBinder onBind(Intent intent) {

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (Looper.myLooper() == null) {
                        Looper.prepare();
                    }
                        timerTask();

                }
            }, 0, MIN_TIME_FOR_UPDATE);


        return messenger.getBinder();
    }


    @Override
    public void onCreate(){
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        // Display a notification about us starting.
        showNotification();
        alive = true;
        Log.i("Location:","Start service");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        alive = false;
        if(timer!=null){
            timer.cancel();
        }
        mNM.cancel(R.string.service_started);
        stopUsingGPS();
        Log.i("Location:", "Stop service");
    }

    /**
     * This method is checking the status of network
     */
    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    /**
     * This method check if this service is running
     */
    public static boolean isRunning(){
        return alive;
    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        /**
         * In this sample, we'll use the same text for the ticker and the
         * expanded notification
         */
        CharSequence text = getText(R.string.service_started);
        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.ic_launcher,
                text, System.currentTimeMillis());
        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.service_label),
                text, contentIntent);
        /**
         * Send the notification.
         * We use a layout id because it is a unique number. We use it later to cancel.
         */
        mNM.notify(R.string.service_started, notification);
    }

    /**
     * get json format of my location data
     */
    private JSONObject getLocationJson(){
        JSONObject locaJson = new JSONObject();
        try {
            locaJson.put("command","UPDATE_LOCATION");
            locaJson.put("latitude",latitude);
            locaJson.put("longitude",longitude);
            locaJson.put("datetime",new Date().getTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return locaJson;
    }

    /**
     * This method will call JsonUtil to send Json data to server though Http request
     */
    private void timerTask(){


            if (isOnline()) {
                getLocation();
                Log.i("la:", String.valueOf(latitude));
                Log.i("lo:", String.valueOf(longitude));
                JsonUtil jsonUtil = new JsonUtil();
                String s = jsonUtil.connectServer(URL_SERVER, getLocationJson());
                Log.i("current:", s);

            }

    }

    /**
     * This method will connect activity and service,receive the message from clients
     */
    class IncomingHandler extends Handler { // Handler of incoming messages from clients.
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    client = msg.replyTo;
                    break;
                case MSG_UNREGISTER_CLIENT:
                    client = null;
                    timer.cancel();
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }



}
