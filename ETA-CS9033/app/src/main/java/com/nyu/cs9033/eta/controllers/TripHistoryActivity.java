package com.nyu.cs9033.eta.controllers;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.adapter.TabsPagerAdapter;
import com.nyu.cs9033.eta.db.TripDatabaseHelper;


public class TripHistoryActivity extends FragmentActivity implements ActionBar.TabListener {

    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;

    //Tab titles
    private String[] tabs = {"Past","Attending","Upcoming"};


    private TripDatabaseHelper helper = new TripDatabaseHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_triphistory);
        //initialization
        initTabs();
    }

    private void initTabs(){
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        //Adding Tabs
        for(String tab:tabs){
            actionBar.addTab(actionBar.newTab().setText(tab).setTabListener(this));
        }

        //on swiping the viewpager make respective tab selected
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //on changing the page, make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        //on tab selected, show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

}
