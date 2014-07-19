package com.sms.partyview.activities;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sms.partyview.models.AttendanceStatus;
import com.sms.partyview.R;
import com.sms.partyview.models.Event;
import com.sms.partyview.models.EventUser;
import com.sms.partyview.models.LocalEvent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.List;

import static com.sms.partyview.models.AttendanceStatus.ACCEPTED;
import static com.sms.partyview.models.AttendanceStatus.DECLINED;


public class InvitedEventDetailActivity extends EventDetailActivity {

    // For passing in intent data.
    // TODO: These are also in class EventDetailActivity. Find some way to
    // put them in a common place.
    public static final String EVENT_INTENT_KEY = "event";

    // Save the response locally if it's not ready to be saved to database yet.
    private String mSelectedResponse = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: This code is quite similar to the stuff in EventDetailActivity.
        // They should be related classes.
        tempEvent = (LocalEvent) getIntent().getSerializableExtra(EVENT_INTENT_KEY);
        if (tempEvent != null) {
            populateEventInfo();
            if (!tempEvent.getTitle().isEmpty()) {
                getActionBar().setTitle(
                        getString(R.string.title_activity_invite_detail) + " " +
                                  tempEvent.getTitle());
            }
        }

        retrieveEvent();

        retrieveCurrentEventUser();
    }

    @Override
    public void setupViews() {
        super.setupViews();

        // No need to show the "Mini Map" label when there's no map.
        findViewById(R.id.tvMiniMap).setVisibility(View.GONE);

        // Add accept/reject buttons to activity.
        LinearLayout llEventDetailButtons =
                (LinearLayout) findViewById(R.id.llEventDetailButtons);
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout llInviteButtons =
                (LinearLayout) inflater.inflate(
                        R.layout.layout_invite_detail_buttons, llEventDetailButtons, false);
        llEventDetailButtons.addView(llInviteButtons);
    }

    @Override
    protected void retrieveAttendeeList() {
        ParseQuery eventQuery = ParseQuery.getQuery(Event.class);
        eventQuery.whereEqualTo("objectId", tempEvent.getObjectId());

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

    private void retrieveCurrentEventUser() {
        ParseQuery eventQuery = ParseQuery.getQuery(Event.class);
        eventQuery.whereEqualTo("objectId", tempEvent.getObjectId());

        ParseQuery userQuery = ParseQuery.getQuery(ParseUser.class);
        userQuery.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());

        // Define the class we would like to query
        ParseQuery<EventUser> query = ParseQuery.getQuery(EventUser.class);
        query.whereMatchesQuery("event", eventQuery);
        query.whereMatchesQuery("user", userQuery);

        query.getFirstInBackground(new GetCallback<EventUser>() {
            @Override
            public void done(EventUser eventUser, ParseException e) {
                currentEventUser = eventUser;
                // If user already selected a response, save that response.
                if (mSelectedResponse != null) {
                    saveAndFinish(mSelectedResponse);
                }
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
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onAcceptInvite(View v) {
        respondToInvite(ACCEPTED);
    }

    public void onRejectInvite(View v) {
        respondToInvite(DECLINED);
    }

    private void respondToInvite(final AttendanceStatus status) {
        ParseQuery<EventUser> query = ParseQuery.getQuery("EventUser");

        // Save the response locally, so it could be sent later if no current event user is loaded.
        mSelectedResponse = status.toString();

        // Do not attempt to save to database if there is no current event user object loaded.
        if (currentEventUser != null) {
            saveAndFinish(status.toString());
        }
    }

    private void saveAndFinish(String response) {
        currentEventUser.put("status", response);
        currentEventUser.saveInBackground();

        // return to list of events
        Intent data = new Intent();
        data.putExtra("eventId", tempEvent.getObjectId());
        data.putExtra("response", response);
        setResult(RESULT_OK, data);

        finish();
    }
}
