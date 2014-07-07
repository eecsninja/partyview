package com.sms.partyview.activities;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sms.partyview.R;
import com.sms.partyview.models.Event;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class EventDetailActivity extends Activity {

    Event mEvent;

    TextView tvEventName;

    TextView tvEventOrganizer;

    TextView tvEventDescription;

    TextView tvEventTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        setupViews();

        retrieveEvent();
    }

    private void retrieveEvent() {
        String eventId = getIntent().getStringExtra("eventId");

        // Define the class we would like to query
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);

        // Define our query conditions
        query.whereEqualTo("objectId", eventId);

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
    }

    public void populateEventInfo() {
        tvEventName.setText(tvEventName.getText() + ": " + mEvent.getTitle());

        // TODO:
        // learn how to query relational data from Parse
//        tvEventOrganizer
//                .setText(tvEventOrganizer.getText() + ": " + mEvent.getHost().getUsername());

        tvEventDescription.setText(tvEventDescription.getText() + ": " + mEvent.getDescription());
        tvEventTime.setText(tvEventTime.getText() + ": " + mEvent.getDate());
    }
}
