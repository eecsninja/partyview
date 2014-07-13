package com.sms.partyview.activities;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sms.partyview.AttendanceStatus;
import com.sms.partyview.R;
import com.sms.partyview.fragments.AttendeeListDialogFragment;
import com.sms.partyview.fragments.EventMapFragment;
import com.sms.partyview.models.Attendee;
import com.sms.partyview.models.Event;
import com.sms.partyview.models.EventUser;

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
    private String eventId;
    private AttendanceStatus status;
    private String eventTitle;
    private EventUser currentEventUser;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        markers = new ArrayList<Marker>();
        status = AttendanceStatus.ACCEPTED;

        eventTitle = getIntent().getStringExtra("eventTitle");
        if (!eventTitle.isEmpty()) {
            getActionBar().setTitle(eventTitle);
        }

        eventUsers = new ArrayList<EventUser>();
        attendees = new ArrayList<Attendee>();

        setupViews();

        retrieveEvent();
    }

    private void retrieveEvent() {
        eventId = getIntent().getStringExtra("eventId");

        // Define the class we would like to query
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);

        // Define our query conditions
        query.whereEqualTo("objectId", eventId);
        query.include("host");

        query.getFirstInBackground(new GetCallback<Event>() {
            @Override
            public void done(Event event, ParseException e) {
                mEvent = event;
                Log.d("DEBUG", "in detailed view");
                Log.d("DEBUG", mEvent.getTitle().toString());
                populateEventInfo();
                retrieveEventUsers();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.event_detail, menu);
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
        eventMapFragment = EventMapFragment.newInstance(attendees, currentEventUser.getObjectId(),
                eventId, mEvent.getLocation().getLatitude(),mEvent.getLocation().getLongitude());
        fts.replace(R.id.flMapContainer, eventMapFragment);
        fts.commit();
    }

    public void populateEventInfo() {
        tvEventName.setText(tvEventName.getText() + ": " + mEvent.getTitle());
        tvEventDescription.setText(tvEventDescription.getText() + ": " + mEvent.getDescription());
        tvEventTime.setText(tvEventTime.getText() + ": " + mEvent.getStartDate());
        tvEventOrganizer
                .setText(tvEventOrganizer.getText() + ": " + mEvent.getHost()
                        .getUsername());
    }

    private void retrieveEventUsers() {
        ParseQuery eventQuery = ParseQuery.getQuery(Event.class);
        eventQuery.whereEqualTo("objectId", eventId);

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

    public void onViewAttendees(View v) {
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
                        mapIntent.putExtra("eventId", eventId);
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
}
