package com.sms.partyview.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.sms.partyview.R;
import com.sms.partyview.adapters.MyPagerAdapter;
import com.sms.partyview.fragments.AcceptedEventsFragment;
import com.sms.partyview.fragments.EventListFragment;
import com.sms.partyview.fragments.PendingEventsFragment;
import com.sms.partyview.models.Event;
import com.sms.partyview.models.User;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity
        extends FragmentActivity
        implements EventListFragment.DummyEventProvider {

    private FragmentPagerAdapter mAdapterViewPager;
    private PagerSlidingTabStrip mTabs;

    User currentUser;

    // Keys for passing in data in an intent.
    public static final String INTENT_USER_NAME = "user_name";
    public static final String INTENT_EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupViews();

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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public ArrayList<Event> getEvents() {
        Event e1 = new Event();
        e1.setTitle("Independence Day BBQ");
        e1.setHost(currentUser);
        e1.setTime("4 July 2014, 4pm");
        Event e2 = new Event();
        e2.setTitle("End of CodePath Android Bootcamp");
        e2.setHost(currentUser);
        e2.setTime("24 July 2014, 7pm");

        // Add the events to the event list.
        ArrayList<Event> events = new ArrayList<Event>();
        events.add(e1);
        events.add(e2);

        return events;
    }

    private void setupViews() {
        retrieveUserInfo();
    }

    private void retrieveUserInfo() {
        // Store user information.
        currentUser = new User();
        // Default user info fields.
        currentUser.setEmail("foo@example.com");
        currentUser.setUserName("AnonymousUser");
        // Display the username and email.
        String userName = getIntent().getStringExtra(INTENT_USER_NAME);
        if (userName != null) {
            currentUser.setUserName(userName);
        }
        String email = getIntent().getStringExtra(INTENT_EMAIL);
        if (email != null) {
            currentUser.setEmail(email);
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
}
