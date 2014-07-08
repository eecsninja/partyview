package com.sms.partyview.activities;

import com.astuetz.PagerSlidingTabStrip;
import com.sms.partyview.R;
import com.sms.partyview.adapters.MyPagerAdapter;
import com.sms.partyview.fragments.AcceptedEventsFragment;
import com.sms.partyview.fragments.PendingEventsFragment;
import com.sms.partyview.helpers.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity
        extends FragmentActivity {

    private FragmentPagerAdapter mAdapterViewPager;

    private PagerSlidingTabStrip mTabs;

    // Keys for passing in data in an intent.
    public static final String INTENT_USER_NAME = "user_name";

    public static final String INTENT_EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupTabs();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_new_event:
                displayNewEventActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == Utils.NEW_EVENT_REQUEST_CODE) && (resultCode == RESULT_OK)) {
            String eventId = data.getStringExtra("eventId");

            AcceptedEventsFragment fragment = (AcceptedEventsFragment) mAdapterViewPager.getItem(0);
            fragment.addNewEventToList(eventId);

            // go back to accepted events page
            mAdapterViewPager.getItem(0);
        }
    }

    private void setupTabs() {
        // Initialize the ViewPager and set an adapter
        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        mAdapterViewPager = new MyPagerAdapter(getSupportFragmentManager(), getFragments());
        vpPager.setAdapter(mAdapterViewPager);

        mTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mTabs.setViewPager(vpPager);
    }

    private List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<Fragment>();

        fragments.add(AcceptedEventsFragment.newInstance());
        fragments.add(PendingEventsFragment.newInstance());

        return fragments;
    }

    private void displayNewEventActivity() {
        Intent i = new Intent(this, NewEventActivity.class);
        startActivityForResult(i, Utils.NEW_EVENT_REQUEST_CODE);
    }
}
