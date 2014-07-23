package com.sms.partyview.adapters;

import com.sms.partyview.fragments.AcceptedEventsFragment;
import com.sms.partyview.fragments.PendingEventsFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

public class MyPagerAdapter extends SmartFragmentStatePagerAdapter {

    private static final String[] CONTENT = { "Events", "Invites"};
    private static int NUM_ITEMS = 2;

    public MyPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                return AcceptedEventsFragment.newInstance();
            case 1: // Fragment # 0 - This will show FirstFragment different title
                return PendingEventsFragment.newInstance();
            default:
                return null;
        }    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return CONTENT[position % CONTENT.length];
    }
}

