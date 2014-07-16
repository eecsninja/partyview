package com.sms.partyview.activities;

import com.sms.partyview.R;
import com.sms.partyview.models.Event;
import com.sms.partyview.models.EventUser;
import com.sms.partyview.models.LocalEvent;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

/**
 * Created by sque on 7/16/14.
 */
public class EventDetailActivity extends FragmentActivity {

    // Data objects.
    protected Event mEvent;
    protected EventUser currentEventUser;
    protected LocalEvent tempEvent;

    // Views.
    protected TextView mTvTitle;
    protected TextView mTvOrganizer;
    protected TextView mTvDescription;
    protected TextView mTvStartTime;
    protected TextView mTvEndTime;
    protected TextView mTvLocation;
    protected TextView mTvAttendeeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        setupViews();
    }

    protected void setupViews() {
        mTvTitle = (TextView) findViewById(R.id.tvEventName);
        mTvOrganizer = (TextView) findViewById(R.id.tvEventOrganizer);
        mTvDescription = (TextView) findViewById(R.id.tvEventDescription);
        mTvStartTime = (TextView) findViewById(R.id.tvEventStartTime);
        mTvEndTime = (TextView) findViewById(R.id.tvEventEndTime);
        mTvLocation = (TextView) findViewById(R.id.tvEventLocation);
        mTvAttendeeList = (TextView) findViewById(R.id.tvEventAttendeeList);
    }

    protected void populateEventInfo () {
        mTvTitle.setText(tempEvent.getTitle());
        mTvOrganizer.setText(tempEvent.getHost());
        mTvLocation.setText(tempEvent.getAddress());
        mTvDescription.setText(tempEvent.getDescription());
        mTvStartTime.setText(tempEvent.getStartDate().toString());
        mTvEndTime.setText(tempEvent.getEndDate().toString());
    }
}
