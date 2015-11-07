package com.nyu.cs9033.eta.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.nyu.cs9033.eta.fragment.CurTripFragment;
import com.nyu.cs9033.eta.fragment.PastTripFragment;
import com.nyu.cs9033.eta.fragment.UpcomTripFragment;

/**
 * Created by wangxinli on 11/4/15.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0: return new PastTripFragment();
            case 1: return new CurTripFragment();
            case 2: return new UpcomTripFragment();

        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }



}
