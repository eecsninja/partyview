package com.sms.partyview.activities;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.sms.partyview.R;
import com.sms.partyview.fragments.EditEventFragment;
import com.sms.partyview.fragments.UpdateEventFragment;
import com.sms.partyview.models.Event;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by sque on 7/14/14.
 */
public class UpdateEventActivity extends EditEventActivity {
    // For passing in an Event ID.
    public static final String EVENT_ID_INTENT_KEY = "eventId";
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

        int titleId = getResources().getIdentifier("action_bar_title", "id",
                "android");
        TextView yourTextView = (TextView) findViewById(titleId);
        yourTextView.setTextColor(getResources().getColor(R.color.white));
        yourTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf"));
        yourTextView.setPadding(0,0,0,5);
        yourTextView.setTextSize(22);

        String eventId = getIntent().getStringExtra(EVENT_ID_INTENT_KEY);
        getEventById(eventId);
    }

    // Create fragment for editing an existing event.
    @Override
    protected EditEventFragment createFragment() {
        return new UpdateEventFragment();
    }

    // Does not create a new event.
    @Override
    protected boolean isNewEvent() {
        return false;
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
}
