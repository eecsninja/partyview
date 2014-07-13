package com.sms.partyview.activities;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sms.partyview.AttendanceStatus;
import com.sms.partyview.R;
import com.sms.partyview.models.Event;
import com.sms.partyview.models.EventUser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;


public class InviteDetailActivity extends Activity {

    private TextView mTvTitle;
    private TextView mTvStart;
    private TextView mTvEnd;
    private TextView mTvLocation;
    private TextView mTvDescription;
    private TextView mTvAttendeeList;
    private Button mBtnAccept;
    private Button mBtnReject;

    private String eventId;
    private Event mEvent;
    private EventUser currentUser;
    private String eventTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_detail);

        eventId = getIntent().getStringExtra("eventId");

        eventTitle = getIntent().getStringExtra("eventTitle");
        if (!eventTitle.isEmpty()) {
            getActionBar().setTitle(getString(R.string.title_activity_invite_detail) + " " +  eventTitle);
        }

        setupViews();

        retrieveEvent();

        retrieveEventUser();
    }

    public void setupViews() {
        mTvDescription = (TextView) findViewById(R.id.tvDescription);
        mTvTitle = (TextView) findViewById(R.id.tvInviteTitle);
        mTvStart = (TextView) findViewById(R.id.tvStartTime);
        mTvEnd = (TextView) findViewById(R.id.tvEndTime);
        mTvLocation = (TextView) findViewById(R.id.tvLocation);
        mBtnAccept = (Button) findViewById(R.id.btnAcceptInvite);
        mBtnReject = (Button) findViewById(R.id.btnRejectInvite);
        mTvAttendeeList = (TextView) findViewById(R.id.tvAttendeeList);
    }

    public void retrieveEvent() {
        // Define the class we would like to query
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);

        // Define our query conditions
        query.whereEqualTo("objectId", eventId);
        query.include("host");

        query.getFirstInBackground(new GetCallback<Event>() {
            @Override
            public void done(Event event, ParseException e) {
                mEvent = event;
                retrieveEventUsers();
                populateEventInfo();
            }
        });

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
                String eventUsers = "";
                for (EventUser eventUser : users) {
                    if (eventUser != null) {
                        if (eventUsers.equals("")) {
                            eventUsers += eventUser.getUser().getUsername();
                        } else {
                            eventUsers += ", " + eventUser.getUser().getUsername();
                        }
                    }
                }
                mTvAttendeeList.setText(eventUsers);

            }
        });
    }

    public void populateEventInfo () {
        mTvTitle.setText(mEvent.getTitle());
        mTvLocation.setText(mEvent.getAddress());
        mTvDescription.setText(mEvent.getDescription());
        mTvStart.setText(mEvent.getStartDate().toString());
        mTvEnd.setText(mEvent.getEndDate().toString());
    }

    private void retrieveEventUser() {
        ParseQuery eventQuery = ParseQuery.getQuery(Event.class);
        eventQuery.whereEqualTo("objectId", eventId);

        ParseQuery userQuery = ParseQuery.getQuery(ParseUser.class);
        userQuery.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());

        // Define the class we would like to query
        ParseQuery<EventUser> query = ParseQuery.getQuery(EventUser.class);
        query.whereMatchesQuery("event", eventQuery);
        query.whereMatchesQuery("user", userQuery);

        query.getFirstInBackground(new GetCallback<EventUser>() {
            @Override
            public void done(EventUser user, ParseException e) {
                currentUser = user;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.invite_detail, menu);
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

    public void onAcceptInvite(View v) {
        ParseQuery<EventUser> query = ParseQuery.getQuery("EventUser");

        // Retrieve the object by id
        query.getInBackground(currentUser.getObjectId(), new GetCallback<EventUser>() {
            public void done(EventUser eventUser, ParseException e) {
                if (e == null) {
                    eventUser.put("status", AttendanceStatus.ACCEPTED.toString());
                    eventUser.saveInBackground();

                    // return to list of events
                    Intent data = new Intent();
                    data.putExtra("eventId", eventId);
                    data.putExtra("response", "accepted");
                    setResult(RESULT_OK, data);

                    finish();
                }
            }
        });
    }

    public void onRejectInvite(View v) {
        ParseQuery<EventUser> query = ParseQuery.getQuery("EventUser");

        // Retrieve the object by id
        query.getInBackground(currentUser.getObjectId(), new GetCallback<EventUser>() {
            public void done(EventUser eventUser, ParseException e) {
                if (e == null) {
                    eventUser.put("status", AttendanceStatus.DECLINED.toString());
                    eventUser.saveInBackground();

                    // return to list of events
                    Intent data = new Intent();
                    data.putExtra("eventId", eventId);
                    data.putExtra("response", "rejected");
                    setResult(RESULT_OK, data);

                    finish();
                }
            }
        });
    }
}
