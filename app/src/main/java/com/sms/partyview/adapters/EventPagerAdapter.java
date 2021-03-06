package com.sms.partyview.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sms.partyview.R;

import java.util.List;

public class EventPagerAdapter extends FragmentPagerAdapter {

    private static final String[] CONTENT = { "Info", "Chat"};
    private static final int[] ICONS = {R.drawable.ic_info, R.drawable.ic_action_chat};

    private List<Fragment> mFragments;

    public EventPagerAdapter(FragmentManager fragmentManager, List<Fragment> fragments) {
        super(fragmentManager);
        this.mFragments = fragments;
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return this.mFragments.size();
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return CONTENT[position % CONTENT.length];
    }
}

