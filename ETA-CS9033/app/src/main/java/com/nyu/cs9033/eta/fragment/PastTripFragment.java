package com.nyu.cs9033.eta.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.controllers.ViewTripActivity;
import com.nyu.cs9033.eta.db.TripDatabaseHelper;

import java.util.List;

/**
 * Created by wangxinli on 11/4/15.
 */
public class PastTripFragment extends Fragment {

    private TripDatabaseHelper helper;
    private View view;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        helper = new TripDatabaseHelper(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_pasttrip, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView listViewPast = (ListView) view.findViewById(R.id.pastTrips);
        //get the list of trips
        final List<String> adapterData = helper.getPastTripsName();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_list_item_1,adapterData);
        //set into ListView
        listViewPast.setAdapter(adapter);

        //set listener to manage the more detail
        listViewPast.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,View v, int i, long l) {
                String trip = adapterData.get(i);
                Intent intent = new Intent(getActivity(), ViewTripActivity.class);
                intent.putExtra("specificTrip",trip);
                startActivity(intent);
            }
        });


    }
}
