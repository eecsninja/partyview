package com.sms.partyview.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import com.sms.partyview.R;
import com.sms.partyview.fragments.EventMapFragment;
import com.sms.partyview.models.Attendee;

import java.util.ArrayList;

public class FullMapActivity extends FragmentActivity implements EventMapFragment.EventMapFragmentListener {

    private EventMapFragment eventMapFragment;
    private ArrayList<Attendee> attendees;
    private String currentEventUserObjId;
    private String eventId;
    private Double latitude;
    private Double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_map);

        attendees = getIntent().getParcelableArrayListExtra("attendees");
        currentEventUserObjId = getIntent().getStringExtra("currentEventUserObjId");
        eventId = getIntent().getStringExtra("eventId");
        latitude = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);
        setupMapFragment();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.full_map, menu);
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

    public void setupMapFragment() {
        // Create the transaction
        FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
        // Replace the content of the container
        eventMapFragment = EventMapFragment.newInstance(attendees, currentEventUserObjId, eventId, latitude, longitude);
        fts.replace(R.id.flFullMapContainer, eventMapFragment);
        fts.commit();
    }

    public void onViewCreated() {

    }
}
