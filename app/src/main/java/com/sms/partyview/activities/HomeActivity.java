package com.sms.partyview.activities;

import com.astuetz.PagerSlidingTabStrip;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.sms.partyview.R;
import com.sms.partyview.adapters.MyPagerAdapter;
import com.sms.partyview.fragments.AcceptedEventsFragment;
import com.sms.partyview.fragments.EventListFragment;
import com.sms.partyview.fragments.PendingEventsFragment;
import com.sms.partyview.helpers.Utils;
import com.sms.partyview.models.AttendanceStatus;
import com.sms.partyview.models.LocalEvent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity
        extends FragmentActivity {

    // Key used to store the user name in the installation info.
    public static final String INSTALLATION_USER_NAME_KEY = "username";
    private FragmentPagerAdapter mAdapterViewPager;
    private PagerSlidingTabStrip mTabs;
    private ViewPager vpPager;

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
            fragment.addNewEventToList(eventId, AttendanceStatus.ACCEPTED.toString());

            // go back to accepted events page
            mAdapterViewPager.getItem(0);
        } else if ((requestCode == Utils.RESPOND_TO_INVITE_EVENT_REQUEST_CODE) && (resultCode == RESULT_OK)) {
            String eventId = data.getStringExtra("eventId");
            String response = data.getStringExtra("response");

            if (response.equals("accepted")) {
                // remove from pending events
                PendingEventsFragment pendingFragment = (PendingEventsFragment) mAdapterViewPager
                        .getItem(1);
                pendingFragment.removeEventFromList(eventId);
                mAdapterViewPager.getItem(1);
                mAdapterViewPager.notifyDataSetChanged();

                vpPager.setCurrentItem(0);

                // go back to accepted events page
                AcceptedEventsFragment fragment = (AcceptedEventsFragment) mAdapterViewPager
                        .getItem(0);
                fragment.addNewEventToList(eventId, response);

                mAdapterViewPager.getItem(0);
                mAdapterViewPager.notifyDataSetChanged();

            } else {
                PendingEventsFragment fragment = (PendingEventsFragment) mAdapterViewPager.getItem(1);
                fragment.removeEventFromList(eventId);

                // go back to invited events page
                mAdapterViewPager.getItem(1);
                mAdapterViewPager.notifyDataSetChanged();
                vpPager.setCurrentItem(1);
            }
        } else if (requestCode == EventListFragment.EVENT_DETAIL_REQUEST &&
                   resultCode == RESULT_OK) {
            LocalEvent event =
                    (LocalEvent) data.getSerializableExtra(
                            EventDetailActivity.UDPATED_EVENT_INTENT_KEY);
            Log.d("DEBUG", "returned local event: " + event);
            if (event == null) {
                return;
            }
            // Replace the existing event if it was updated.
            int index = data.getIntExtra(EventDetailActivity.EVENT_LIST_INDEX_KEY, 0);
            EventListFragment fragment =
                    (EventListFragment) mAdapterViewPager.getItem(vpPager.getCurrentItem());
            fragment.updateEvent(index, event);
        }
    }

    private void setupTabs() {
        // Initialize the ViewPager and set an adapter
        vpPager = (ViewPager) findViewById(R.id.vpPager);
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

    // Registers the user with this installation's info.
    private void storeInstallationInfo() {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        String currentUserName = ParseUser.getCurrentUser().getUsername();
        if (currentUserName == installation.getString(INSTALLATION_USER_NAME_KEY)) {
            return;
        }
        installation.put(INSTALLATION_USER_NAME_KEY, currentUserName);
        installation.saveInBackground();
    }
}
