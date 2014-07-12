package com.sms.partyview.activities;

import com.astuetz.PagerSlidingTabStrip;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sms.partyview.R;
import com.sms.partyview.adapters.MyPagerAdapter;
import com.sms.partyview.fragments.AcceptedEventsFragment;
import com.sms.partyview.fragments.PendingEventsFragment;
import com.sms.partyview.helpers.Utils;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
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

        // Create notification with current user info.
        // TODO: This is just a template for other notifications. Remove it
        // when no longer needed.
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("Successfully logged in");
        ParseUser user = ParseUser.getCurrentUser();
        try {
            notificationBuilder.
                    setContentText("Signed in as " + user.getUsername());
        } catch (NullPointerException e) {
            System.err.println(e.getMessage());
        }
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());

        storeInstallationInfo();
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
        } else if ((requestCode == Utils.RESPOND_TO_INVITE_EVENT_REQUEST_CODE) && (resultCode == RESULT_OK)) {
            String eventId = data.getStringExtra("eventId");
            String response = data.getStringExtra("response");

            if (response.equals("accepted")) {
                // remove from pending events
                PendingEventsFragment pendingFragment = (PendingEventsFragment) mAdapterViewPager.getItem(1);
                pendingFragment.removeEventFromList(eventId);
                mAdapterViewPager.getItem(1);
                mAdapterViewPager.notifyDataSetChanged();

                vpPager.setCurrentItem(0);

                // go back to accepted events page
                AcceptedEventsFragment fragment = (AcceptedEventsFragment) mAdapterViewPager.getItem(0);
                fragment.addNewEventToList(eventId);

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
        cacheAppUsers();

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

    private void cacheAppUsers() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();

        // Query for new results from the network.
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(final List<ParseUser> users, ParseException e) {

                Log.d(HomeActivity.class.getSimpleName() + "_DEBUG", "got user info");
                Log.d(HomeActivity.class.getSimpleName() + "_DEBUG", users.toString());

                // Remove the previously cached results.
                ParseObject.unpinAllInBackground("users", new DeleteCallback() {
                    public void done(ParseException e) {
                        // Cache the new results.
                        ParseObject.pinAllInBackground("users", users);
                        Log.d(HomeActivity.class.getSimpleName() + "_DEBUG", "pin all user info");

                    }
                });
            }
        });
    }
}
