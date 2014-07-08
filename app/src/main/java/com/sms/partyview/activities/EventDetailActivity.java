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
import com.parse.ParseObject;
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

    Event mEvent;

    TextView tvEventName;

    TextView tvEventOrganizer;

    TextView tvEventDescription;

    TextView tvEventTime;

    String eventId;

    List<EventUser> attendees;

    MapFragment mapFragment;

    GoogleMap map;

    List<Marker> markers;

    Button btnJoinLeave;

    boolean joinedEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        markers = new ArrayList<Marker>();
        joinedEvent = false;

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

        query.getFirstInBackground(new GetCallback<Event>() {
            @Override
            public void done(Event event, ParseException e) {
                mEvent = event;

                event.getHost().fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                    public void done(ParseObject object, ParseException e) {
                        tvEventOrganizer
                                .setText(tvEventOrganizer.getText() + ": " + mEvent.getHost().getUsername());
                    }
                });
                Log.d("DEBUG", "in detailed view");
                Log.d("DEBUG", mEvent.getTitle().toString());
                populateEventInfo();
            }
        });
    }

    private void retrieveEventUser() {
        // Define the class we would like to query
        ParseQuery<EventUser> query = ParseQuery.getQuery(EventUser.class);

        // Define our query conditions
        query.whereEqualTo("user", ParseUser.getCurrentUser());

        query.getFirstInBackground(new GetCallback<EventUser>() {
            @Override
            public void done(EventUser eventUser, ParseException e) {
                joinedEvent = eventUser.getStatus().equals(AttendanceStatus.PRESENT);
                toggleJoinLeave(joinedEvent);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        AttendeeListFragment attendeeListFragment = AttendeeListFragment.newInstance(eventId);
        fts.replace(R.id.flAttendeesContainer, attendeeListFragment);
        fts.commit();
    }

    public void populateEventInfo() {
        tvEventName.setText(tvEventName.getText() + ": " + mEvent.getTitle());
        tvEventDescription.setText(tvEventDescription.getText() + ": " + mEvent.getDescription());
        tvEventTime.setText(tvEventTime.getText() + ": " + mEvent.getDate());
    }

    public void onJoinLeave(View v) {
        String currentState = btnJoinLeave.getText().toString();
        if (currentState.equals(getString(R.string.leave_event))) {
            toggleJoinLeave(false);
        } else {
            toggleJoinLeave(true);
        }

    }

    public void toggleJoinLeave(boolean joining) {
        if (joining) {
            btnJoinLeave.setText(getString(R.string.leave_event));
            for (Marker marker : markers) {
                marker.setVisible(true);
            }
            joinedEvent = true;
        } else {
            btnJoinLeave.setText(getString(R.string.join_event));
            for (Marker marker : markers) {
                marker.setVisible(false);
            }
            joinedEvent = false;
        }
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
                                .position(new LatLng(attendee.getLocation().getLatitude(), attendee.getLocation().getLongitude()))
                                .title(parseObject.getUsername())
                                .visible(joinedEvent));

                        markers.add(marker);
                    }

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
            });

        }

    }


}
