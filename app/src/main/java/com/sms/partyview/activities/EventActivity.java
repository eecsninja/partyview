package com.sms.partyview.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.parse.ParseUser;
import com.sms.partyview.R;
import com.sms.partyview.adapters.EventPagerAdapter;
import com.sms.partyview.adapters.MyPagerAdapter;
import com.sms.partyview.fragments.AcceptedEventsFragment;
import com.sms.partyview.fragments.AttendeeListDialogFragment;
import com.sms.partyview.fragments.PendingEventsFragment;
import com.sms.partyview.models.Attendee;
import com.sms.partyview.models.Event;
import com.sms.partyview.models.EventUser;
import com.sms.partyview.models.LocalEvent;

import java.util.ArrayList;
import java.util.List;

public class EventActivity extends FragmentActivity {

    private FragmentPagerAdapter mAdapterViewPager;
    private PagerSlidingTabStrip mTabs;
    private ViewPager vpPager;

    // Data objects.
    protected Event mEvent;
    protected EventUser currentEventUser;
    protected LocalEvent tempEvent;
    private ArrayList<Attendee> attendees;

    private static final int EDIT_EVENT_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        setupTabs();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.event_detail, menu);
        // Show the edit menu item if the current user is the host.
        menu.findItem(R.id.action_edit_event)
                .setVisible(tempEvent.getHost().equals(ParseUser.getCurrentUser().getUsername()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupTabs() {
        // Initialize the ViewPager and set an adapter
        vpPager = (ViewPager) findViewById(R.id.vpEventPager);
        mAdapterViewPager = new EventPagerAdapter(getSupportFragmentManager(), getFragments());
        vpPager.setAdapter(mAdapterViewPager);

        mTabs = (PagerSlidingTabStrip) findViewById(R.id.eventTabs);
        mTabs.setViewPager(vpPager);
    }

    private List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<Fragment>();

       // fragments.add(AcceptedEventDetailFragment.newInstance());
        fragments.add(PendingEventsFragment.newInstance());

        return fragments;
    }

    public void onViewAttendees(MenuItem mi) {
        AttendeeListDialogFragment.show(this, getString(R.string.attendees_title), attendees);
    }

    public void editEvent(MenuItem menuItem) {
        Intent intent = new Intent(this, UpdateEventActivity.class);
        intent.putExtra(UpdateEventActivity.EVENT_ID_INTENT_KEY, mEvent.getObjectId());
        startActivityForResult(intent, EDIT_EVENT_REQUEST);
    }
}
