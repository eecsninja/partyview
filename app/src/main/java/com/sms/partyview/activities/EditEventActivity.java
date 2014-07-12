package com.sms.partyview.activities;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sms.partyview.AttendanceStatus;
import com.sms.partyview.models.Event;
import com.sms.partyview.models.EventUser;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sque on 7/12/14.
 */
public class EditEventActivity extends NewEventActivity {
    // For passing in an Event ID.
    public static final String EVENT_ID_INTENT_KEY = "eventId";

    // The event to edit.
    protected Event mEvent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(getClass().getSimpleName() + "_DEBUG", "create activity");

        // An event ID must be provided.
        if (!getIntent().hasExtra(EVENT_ID_INTENT_KEY)) {
            showToastAndFinish("No event was provided.");
        }

        String eventId = getIntent().getStringExtra(EVENT_ID_INTENT_KEY);
        getEventById(eventId);
    }

    @Override
    public void saveNewEvent(final Event event, final String invitesString) {
        // TODO: Save the updated event.
    }

    protected void getEventById(String eventId) {
        // Query for the event with the given ID.
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.whereEqualTo("objectId", eventId);

        query.findInBackground(
            new FindCallback<Event>() {
                @Override
                public void done(List<Event> events, ParseException e) {
                    // Handle exceptions or if there was no event found.
                    String toastMsg = null;
                    if (e != null) {
                        toastMsg = e.getMessage();
                    } else if (events.isEmpty()) {
                        toastMsg = "No event with that ID was found.";
                    }
                    if (toastMsg != null) {
                        showToastAndFinish(toastMsg);
                    }

                    // Store the event.
                    loadEventForEditing(events.get(0));
                }
            }
        );
    }

    protected void showToastAndFinish(String toastText) {
        Toast.makeText(
                EditEventActivity.this, toastText, Toast.LENGTH_SHORT).show();
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }

    protected void loadEventForEditing(Event event) {
        mEvent = event;
        mEditEventFragment.loadEvent(event);
    }
}
