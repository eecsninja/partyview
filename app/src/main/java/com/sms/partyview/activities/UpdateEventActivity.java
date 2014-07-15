package com.sms.partyview.activities;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.sms.partyview.fragments.EditEventFragment;
import com.sms.partyview.fragments.UpdateEventFragment;
import com.sms.partyview.models.Event;
import com.sms.partyview.models.LocalEvent;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

/**
 * Created by sque on 7/14/14.
 */
public class UpdateEventActivity extends EditEventActivity {
    // For passing in an Event ID.
    public static final String EVENT_ID_INTENT_KEY = "eventId";
    public static final String EVENT_UPDATED_KEY = "eventUpdated";
    private static final String TAG = UpdateEventActivity.class.getSimpleName() + "_DEBUG";

    // The event to edit.
    protected Event mEvent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "create activity");

        // An event ID must be provided.
        if (!getIntent().hasExtra(EVENT_ID_INTENT_KEY)) {
            showToastAndFinish("No event was provided.");
        }

        String eventId = getIntent().getStringExtra(EVENT_ID_INTENT_KEY);
        getEventById(eventId);
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

    // Create fragment for editing an existing event.
    @Override
    protected EditEventFragment createFragment() {
        return new UpdateEventFragment();
    }

    protected void getEventById(String eventId) {
        // Query for the event with the given ID.
        ParseQuery<Event> query = Event.getQueryForEventWithId(eventId);

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

    @Override
    protected void finishWithEvent(Event event) {
        Intent data = new Intent();
        // Assume the event was updated, even if there were no changes.
        data.putExtra(EVENT_UPDATED_KEY, new LocalEvent(event));
        setResult(RESULT_OK, data);
        finish();
    }

    protected void showToastAndFinish(String toastText) {
        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }

    protected void loadEventForEditing(Event event) {
        mEvent = event;
        if (mEditEventFragment instanceof UpdateEventFragment) {
            ((UpdateEventFragment) mEditEventFragment).loadEvent(event);
        } else {
            throw new ClassCastException("Fragment must be of class UpdateEventFragment.");
        }
    }

    @Override
    protected JSONObject getNotificationData(Event event) {
        JSONObject json = super.getNotificationData(event);
        try {
            json.put("isNewEvent", false);
        } catch (JSONException e) {
            System.err.println(e.getMessage());
        }
        return json;
    }
}
