package com.nyu.cs9033.eta.controllers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.db.TripDatabaseHelper;

import java.util.List;

public class TripHistoryActivity extends Activity {

    private ListView listView;
    private TripDatabaseHelper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_triphistory);

        helper = new TripDatabaseHelper(this);
        listView = (ListView) findViewById(R.id.currentTrips);

        //get the list of trips
        final List<String> adapterData =  helper.getAllTripsName();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,adapterData);

        //set into ListView
        listView.setAdapter(adapter);

        //set listener to manage the more detail
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String trip = adapterData.get(i);
                Intent intent = new Intent(TripHistoryActivity.this, ViewTripActivity.class);
                intent.putExtra("specificTrip", trip);
                startActivity(intent);
            }
        });

    }



}
