package com.sms.partyview.activities;

import com.google.android.gms.maps.model.Marker;

import com.astuetz.PagerSlidingTabStrip;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sms.partyview.R;
import com.sms.partyview.adapters.EventPagerAdapter;
import com.sms.partyview.fragments.AcceptedEventDetailFragment;
import com.sms.partyview.fragments.AttendeeListDialogFragment;
import com.sms.partyview.fragments.ChatFragment;
import com.sms.partyview.fragments.EventMapFragment;
import com.sms.partyview.fragments.MapChatFragment;
import com.sms.partyview.models.AttendanceStatus;
import com.sms.partyview.models.Attendee;
import com.sms.partyview.models.Event;
import com.sms.partyview.models.EventUser;
import com.sms.partyview.models.LocalEvent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class EventActivity extends FragmentActivity implements
        ChatFragment.OnFragmentInteractionListener,
        EventMapFragment.EventMapFragmentListener{

    // For passing in intent data.
    public static final String EVENT_INTENT_KEY = "event";
    public static final String UDPATED_EVENT_INTENT_KEY = "updatedEvent";
    public static final String EVENT_LIST_INDEX_KEY = "eventListIndex";
    private static final int EDIT_EVENT_REQUEST = 1;
    private AttendanceStatus status;
    private boolean mEventWasUpdated = false;
    private int mEventListIndex;

    private Button btnJoinLeave;
    private EventMapFragment eventMapFragment;
    private List<EventUser> eventUsers;
    private ArrayList<Attendee> attendees;
    private List<Marker> markers;

    private FragmentPagerAdapter mAdapterViewPager;
    private PagerSlidingTabStrip mTabs;
    private ViewPager vpPager;

    // Data objects.
    protected Event mEvent;
    protected EventUser currentEventUser;
    protected LocalEvent tempEvent;

    // Fragments
    AcceptedEventDetailFragment detailFragment;
    MapChatFragment mapChatFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        eventUsers = new ArrayList<EventUser>();
        attendees = new ArrayList<Attendee>();

        status = AttendanceStatus.valueOf(getIntent().getStringExtra("eventStatus"));

        mEventListIndex = getIntent().getIntExtra(EVENT_LIST_INDEX_KEY, 0);

        tempEvent = (LocalEvent) getIntent().getSerializableExtra(EVENT_INTENT_KEY);
       // saveAndDisplayEvent((LocalEvent) getIntent().getSerializableExtra(EVENT_INTENT_KEY));

        retrieveAttendeeList();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.event, menu);
        // Show the edit menu item if the current user is the host.
        menu.findItem(R.id.action_edit_event)
                .setVisible(tempEvent.getHost().equals(ParseUser.getCurrentUser().getUsername()));
        return true;
    }

    private void setupTabs() {
        // Initialize the ViewPager and set an adapter
        vpPager = (ViewPager) findViewById(R.id.vpEventPager);
        mAdapterViewPager = new EventPagerAdapter(getSupportFragmentManager(), getFragments());
        vpPager.setAdapter(mAdapterViewPager);

        mTabs = (PagerSlidingTabStrip) findViewById(R.id.eventTabs);
        mTabs.setViewPager(vpPager);

        mapChatFragment = (MapChatFragment) mAdapterViewPager.getItem(1);

    }

    private List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<Fragment>();

        fragments.add(AcceptedEventDetailFragment.newInstance(status.toString(), mEventListIndex, tempEvent));
        fragments.add(MapChatFragment.newInstance(attendees, currentEventUser.getObjectId(), tempEvent.getObjectId(),
                tempEvent.getLatitude(), tempEvent.getLongitude(), currentEventUser.getStatus().toString()));

        return fragments;
    }

    public void onViewAttendees(MenuItem mi) {
        AttendeeListDialogFragment.show(this, getString(R.string.attendees_title), attendees);
    }

    public void editEvent(MenuItem menuItem) {
        Intent intent = new Intent(this, UpdateEventActivity.class);
        intent.putExtra(UpdateEventActivity.EVENT_ID_INTENT_KEY, tempEvent.getObjectId());
        startActivityForResult(intent, EDIT_EVENT_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_EVENT_REQUEST && resultCode == RESULT_OK) {
            // If the event was (most likely) updated, load the event again.
            // Also, flag it as updated.
            LocalEvent updatedEvent =
                    (LocalEvent) data.getSerializableExtra(UpdateEventActivity.SAVED_EVENT_KEY);
            if (updatedEvent != null) {

                AcceptedEventDetailFragment fragment = (AcceptedEventDetailFragment) mAdapterViewPager.getItem(0);
                fragment.saveAndDisplayEvent(updatedEvent);
                vpPager.setCurrentItem(0);
                mEventWasUpdated = true;
            }
        }
    }
        @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        // Finish the activity, passing back data about whether the
        // event was updated.
        Intent data = new Intent();
        Log.d("DEBUG", "Event was updated? " + mEventWasUpdated);
        if (mEventWasUpdated) {
            data.putExtra(UDPATED_EVENT_INTENT_KEY, tempEvent);
        }
        data.putExtra(EVENT_LIST_INDEX_KEY, mEventListIndex);
        Log.d("DEBUG", "Updated list index: " + mEventListIndex);
        setResult(RESULT_OK, data);
        finish();
    }

    protected void retrieveAttendeeList() {
        ParseQuery<EventUser> query = EventUser.getQueryForAttendeeList(tempEvent.getObjectId());
        query.findInBackground(new FindCallback<EventUser>() {
            @Override
            public void done(List<EventUser> users, ParseException e) {
            List<String> eventUserStrings = new ArrayList<String>();
            for (EventUser eventUser : users) {
                if (eventUser != null) {
                    eventUsers.add(eventUser);
                    attendees.add(new Attendee(eventUser));
                    if (eventUser.getUser().getObjectId()
                            .equals(ParseUser.getCurrentUser().getObjectId())) {
                        currentEventUser = eventUser;
                        status = eventUser.getStatus();
                    }
                    eventUserStrings.add(eventUser.getUser().getUsername());
                }
            }
            setupTabs();
            }
        });
    }


    public void onViewAttendees(View view) {
        AttendeeListDialogFragment.show(this, getString(R.string.attendees_title), attendees);
    }

    public void onViewCreated() {
        if (mapChatFragment == null) {
            mapChatFragment = (MapChatFragment) mAdapterViewPager.getItem(1);
        }
        if (mapChatFragment != null) {
            mapChatFragment.onViewCreated();
        }
    }

    public void onJoinLeave(View v) {
        mapChatFragment.onJoinLeave(v);
    }
}
