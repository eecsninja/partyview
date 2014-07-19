package com.sms.partyview.activities;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.sms.partyview.R;
import com.sms.partyview.adapters.EventAdapter;
import com.sms.partyview.models.Event;
import com.sms.partyview.models.EventUser;
import com.sms.partyview.models.LocalEvent;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sque on 7/16/14.
 */
public abstract class EventDetailFragment extends FragmentActivity {

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

    public static EventDetailFragment newInstance() {
        AcceptedEventsFragment fragment = new AcceptedEventsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize event list and adapter.
        events = new ArrayList<Event>();
        eventAdapter = new EventAdapter(getActivity(), events);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout.
        View view = inflater.inflate(
                R.layout.fragment_event_list, container, false);

        // Set up to display tweets.
        eventsView = (ListView) view.findViewById(R.id.lvHomeEventList);
        eventsView.setAdapter(eventAdapter);

        populateEventList();

        // Add item click listener.

        setUpDisplayDetailedView();

        // Return it.
        return view;
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

    protected void retrieveEvent() {
        ParseQuery<Event> query = Event.getQueryForEventWithId(tempEvent.getObjectId());

        query.getFirstInBackground(new GetCallback<Event>() {
            @Override
            public void done(Event event, ParseException e) {
                mEvent = event;
                Log.d("DEBUG", "in detailed view");
                Log.d("DEBUG", mEvent.getTitle().toString());

                retrieveAttendeeList();
            }
        });
    }

    protected abstract void retrieveAttendeeList();
}
