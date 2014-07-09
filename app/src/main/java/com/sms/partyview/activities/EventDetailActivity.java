package com.sms.partyview.activities;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sms.partyview.AttendanceStatus;
import com.sms.partyview.R;
import com.sms.partyview.fragments.AttendeeListFragment;
import com.sms.partyview.models.Event;
import com.sms.partyview.models.EventUser;

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

public class EventDetailActivity extends FragmentActivity
        implements AttendeeListFragment.OnFragmentInteractionListener {

    private Event mEvent;
    private String eventId;
    private AttendanceStatus status;
    private String eventUserId;
    private String eventTitle;
    private EventUser eventUser;

    private TextView tvEventName;
    private TextView tvEventOrganizer;
    private TextView tvEventDescription;
    private TextView tvEventTime;
    private Button btnJoinLeave;

    private MapFragment mapFragment;
    private GoogleMap map;
    private AttendeeListFragment attendeeListFragment;

    private List<Marker> markers;

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

        setupViews();

        retrieveEvent();

        retrieveEventUser();

        setupFragment();

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
            }
        });
    }

    private void retrieveEventUser() {
        ParseQuery eventQuery = ParseQuery.getQuery(Event.class);
        eventQuery.whereEqualTo("objectId", eventId);

        // Define the class we would like to query
        ParseQuery<EventUser> query = ParseQuery.getQuery(EventUser.class);
        query.whereMatchesQuery("event", eventQuery);

        // Define our query conditions
        query.whereEqualTo("user", ParseUser.getCurrentUser());

        query.getFirstInBackground(new GetCallback<EventUser>() {
            @Override
            public void done(EventUser user, ParseException e) {
                if (user != null) {
                    eventUserId = user.getObjectId();
                    eventUser = user;
                    status = user.getStatus();
                    toggleJoinLeave(status);
                }
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

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            map = mapFragment.getMap();
        }
        btnJoinLeave = (Button) findViewById(R.id.btnJoinLeave);
    }

    public void setupFragment() {
        // Create the transaction
        FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
        // Replace the content of the container
        attendeeListFragment = AttendeeListFragment.newInstance(eventId);
        fts.replace(R.id.flAttendeesContainer, attendeeListFragment);
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

    public void onJoinLeave(View v) {
        String currentState = btnJoinLeave.getText().toString();
        if (currentState.equals(getString(R.string.leave_event))) {
            toggleJoinLeave(AttendanceStatus.ACCEPTED);
        } else {
            toggleJoinLeave(AttendanceStatus.PRESENT);
        }

    }

    public void toggleJoinLeave(AttendanceStatus status) {
        if (status.equals(AttendanceStatus.PRESENT)) {
            btnJoinLeave.setText(getString(R.string.leave_event));
            for (Marker marker : markers) {
                marker.setVisible(true);
            }
            this.status = AttendanceStatus.PRESENT;
        } else {
            btnJoinLeave.setText(getString(R.string.join_event));
            for (Marker marker : markers) {
                marker.setVisible(false);
            }
            this.status = AttendanceStatus.ACCEPTED;
        }


        attendeeListFragment.updateAttendeeStatus(eventUser, status);

        ParseQuery<EventUser> query = ParseQuery.getQuery("EventUser");

        // Retrieve the object by id
        query.getInBackground(eventUserId, new GetCallback<EventUser>() {
            public void done(EventUser eventUser, ParseException e) {
                if (e == null) {
                    updateUserStatus(eventUser);
                }
            }
        });
    }

    public void updateUserStatus(EventUser eventUser) {
        eventUser.put("status", status.toString());
        eventUser.saveInBackground();
    }


    @Override
    public void onUsersLoaded(List<EventUser> attendees) {
        addUsersToMap(attendees);
    }

    private void addUsersToMap(List<EventUser> attendees) {
        for (final EventUser attendee : attendees) {
            attendee.getUser().fetchInBackground(new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser parseObject, ParseException e) {
                    if (map != null) {
                        Marker marker = map.addMarker(new MarkerOptions()
                                .position(new LatLng(attendee.getLocation().getLatitude(),
                                        attendee.getLocation().getLongitude()))
                                .title(parseObject.getUsername())
                                .visible(status.equals(AttendanceStatus.PRESENT)));

                        markers.add(marker);
                    }
                    updateCameraView();
                }
            });
        }
    }

    private void updateCameraView() {
        //Calculate the markers to get their position
        LatLngBounds.Builder b = new LatLngBounds.Builder();
        for (Marker m : markers) {
            b.include(m.getPosition());
        }
        LatLngBounds bounds = b.build();
        //Change the padding as per needed
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 25, 25, 0);
        map.animateCamera(cu);
    }
}
