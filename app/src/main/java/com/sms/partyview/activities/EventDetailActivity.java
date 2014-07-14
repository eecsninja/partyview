package com.sms.partyview.activities;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sms.partyview.models.AttendanceStatus;
import com.sms.partyview.R;
import com.sms.partyview.fragments.AttendeeListDialogFragment;
import com.sms.partyview.fragments.EventMapFragment;
import com.sms.partyview.models.Attendee;
import com.sms.partyview.models.Event;
import com.sms.partyview.models.EventUser;
import com.sms.partyview.models.LocalEvent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EventDetailActivity extends FragmentActivity implements EventMapFragment.EventMapFragmentListener {

    private Event mEvent;
    private AttendanceStatus status;
    private EventUser currentEventUser;
    private LocalEvent tempEvent;
    private boolean mEventWasUpdated = false;
    private int mEventListIndex;

    private TextView tvEventName;
    private TextView tvEventOrganizer;
    private TextView tvEventDescription;
    private TextView tvEventTime;
    private Button btnJoinLeave;

    private EventMapFragment eventMapFragment;
    private List<EventUser> eventUsers;
    private ArrayList<Attendee> attendees;

    private List<Marker> markers;

    private final int EDIT_EVENT_REQUEST = 1;

    // For passing in intent data.
    public static final String EVENT_INTENT_KEY = "event";
    public static final String UDPATED_EVENT_INTENT_KEY = "updatedEvent";
    public static final String EVENT_LIST_INDEX_KEY = "eventListIndex";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        markers = new ArrayList<Marker>();
        status = AttendanceStatus.ACCEPTED;

        eventUsers = new ArrayList<EventUser>();
        attendees = new ArrayList<Attendee>();

        status = AttendanceStatus.valueOf(getIntent().getStringExtra("eventStatus"));

        mEventListIndex = getIntent().getIntExtra(EVENT_LIST_INDEX_KEY, 0);

        setupViews();
        saveAndDisplayEvent((LocalEvent) getIntent().getSerializableExtra(EVENT_INTENT_KEY));

        retrieveEvent();
    }

    // Displays an event in the activity's UI.
    private void saveAndDisplayEvent(LocalEvent event) {
        tempEvent = event;
        if (tempEvent.getTitle().isEmpty()) {
            return;
        }
        getActionBar().setTitle(tempEvent.getTitle());
        if (tempEvent != null) {
            populateEventInfo();
        }
    }

    private void retrieveEvent() {
        // Define the class we would like to query
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);

        // Define our query conditions
        query.whereEqualTo("objectId", tempEvent.getObjectId());
        query.include("host");

        query.getFirstInBackground(new GetCallback<Event>() {
            @Override
            public void done(Event event, ParseException e) {
                mEvent = event;
                Log.d("DEBUG", "in detailed view");
                Log.d("DEBUG", mEvent.getTitle().toString());

                retrieveEventUsers();
            }
        });
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
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setupViews() {
        tvEventName = (TextView) findViewById(R.id.tvEventNameTitle);
        tvEventOrganizer = (TextView) findViewById(R.id.tvEventOrganizerTitle);
        tvEventDescription = (TextView) findViewById(R.id.tvEventDescTitle);
        tvEventTime = (TextView) findViewById(R.id.tvEventTimeTitle);
        btnJoinLeave = (Button) findViewById(R.id.btnJoinLeave);
    }

    public void setupMapFragment() {
        // Create the transaction
        FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
        // Replace the content of the container
        eventMapFragment = EventMapFragment.newInstance(
                attendees,
                currentEventUser.getObjectId(),
                tempEvent.getObjectId(),
                mEvent.getLocation().getLatitude(),mEvent.getLocation().getLongitude());
        fts.replace(R.id.flMapContainer, eventMapFragment);
        fts.commit();
    }

    public void populateEventInfo() {
        tvEventName.setText(
                getString(R.string.event_name_title) + ": " + tempEvent.getTitle());
        tvEventDescription.setText(
                getString(R.string.event_desc_title) + ": " + tempEvent.getDescription());
        tvEventTime.setText(
                getString(R.string.event_time_title) + ": " + tempEvent.getStartDate());
        tvEventOrganizer.setText(
                getString(R.string.event_organizer_title) + ": " + tempEvent.getHost());
        if (status.equals(AttendanceStatus.PRESENT)) {
            btnJoinLeave.setText(getString(R.string.leave_event));
        } else if (status.equals(AttendanceStatus.ACCEPTED)) {
            btnJoinLeave.setText(getString(R.string.join_event));
        }
    }

    private void retrieveEventUsers() {
        ParseQuery eventQuery = ParseQuery.getQuery(Event.class);
        eventQuery.whereEqualTo("objectId", tempEvent.getObjectId());

        // Define the class we would like to query
        ParseQuery<EventUser> query = ParseQuery.getQuery(EventUser.class);
        query.whereMatchesQuery("event", eventQuery);
        query.include("user");

        query.findInBackground(new FindCallback<EventUser>() {
            @Override
            public void done(List<EventUser> users, ParseException e) {
                for (EventUser eventUser : users) {
                    if (eventUser != null) {
                        eventUsers.add(eventUser);
                        attendees.add(new Attendee(eventUser));
                        if (eventUser.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                            currentEventUser = eventUser;
                            status = eventUser.getStatus();
                            toggleJoinLeave(status);
                        }
                    }
                }
                setupMapFragment();
            }
        });
    }

    public void onViewAttendees(MenuItem mi) {
        AttendeeListDialogFragment.show(this, getString(R.string.attendees_title), attendees);
    }

    public void onJoinLeave(View v) {
        String currentState = btnJoinLeave.getText().toString();
        if (currentState.equals(getString(R.string.leave_event))) {
            toggleJoinLeave(AttendanceStatus.ACCEPTED);
        } else {
            toggleJoinLeave(AttendanceStatus.PRESENT);
        }

    }

    public void toggleJoinLeave(AttendanceStatus status) {
        if (status != null) {
            if (status.equals(AttendanceStatus.PRESENT)) {
                btnJoinLeave.setText(getString(R.string.leave_event));
                this.status = AttendanceStatus.PRESENT;
            } else if (status.equals(AttendanceStatus.ACCEPTED)) {
                btnJoinLeave.setText(getString(R.string.join_event));
                this.status = AttendanceStatus.ACCEPTED;
            }

            if (eventMapFragment != null) {
                eventMapFragment.setMarkerVisibility(status.equals(AttendanceStatus.PRESENT));
            }

            if (currentEventUser != null) {

                updateAttendeeInList(currentEventUser, status);

                ParseQuery<EventUser> query = ParseQuery.getQuery("EventUser");

                // Retrieve the object by id
                query.getInBackground(currentEventUser.getObjectId(), new GetCallback<EventUser>() {
                    public void done(EventUser eventUser, ParseException e) {
                        if (e == null) {
                            updateUserStatus(eventUser);
                        }
                    }
                });
            }
        }
    }

    public void updateAttendeeInList(EventUser user, AttendanceStatus status) {
        for (Attendee attendee : attendees) {
            if (attendee.getUsername().equals(user.getUser().getUsername())) {
                attendee.setStatus(status);
                break;
            }
        }
    }

    public void updateUserStatus(EventUser eventUser) {
        eventUser.put("status", status.toString());
        eventUser.saveInBackground();
    }

    public void onViewCreated() {
        if (eventMapFragment != null) {
            eventMapFragment.setOnMapClick(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if (status.equals(AttendanceStatus.PRESENT)) {
                        Intent mapIntent = new Intent(EventDetailActivity.this, FullMapActivity.class);
                        mapIntent.putParcelableArrayListExtra("attendees", attendees);
                        mapIntent.putExtra("currentEventUserObjId", currentEventUser.getObjectId());
                        mapIntent.putExtra("eventId", mEvent.getObjectId());
                        mapIntent.putExtra("latitude", mEvent.getLocation().getLatitude());
                        mapIntent.putExtra("longitude", mEvent.getLocation().getLongitude());
                        startActivity(mapIntent);
                    }
                }
            });
            eventMapFragment.setMarkerVisibility(status.equals(AttendanceStatus.PRESENT));
        }
    }

    public void editEvent(MenuItem menuItem) {
        Intent intent = new Intent(this, EditEventActivity.class);
        intent.putExtra(EditEventActivity.EVENT_ID_INTENT_KEY, mEvent.getObjectId());
        startActivityForResult(intent, EDIT_EVENT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_EVENT_REQUEST && resultCode == RESULT_OK) {
            // If the event was (most likely) updated, load the event again.
            // Also, flag it as updated.
            LocalEvent updatedEvent =
                    (LocalEvent) data.getSerializableExtra(EditEventActivity.EVENT_UPDATED_KEY);
            if (updatedEvent != null) {
                saveAndDisplayEvent(updatedEvent);
                mEventWasUpdated = true;
            }
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

    public void onJoinChat(MenuItem mi) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(EditEventActivity.EVENT_ID_INTENT_KEY, tempEvent.getObjectId());
        startActivity(intent);
    }
}
