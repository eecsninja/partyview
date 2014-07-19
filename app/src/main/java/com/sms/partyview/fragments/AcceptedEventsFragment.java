package com.sms.partyview.fragments;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sms.partyview.models.AttendanceStatus;
import com.sms.partyview.models.Event;
import com.sms.partyview.models.EventUser;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by myho on 7/3/14.
 */
public class AcceptedEventsFragment extends EventListFragment {

    private static final String TAG = AcceptedEventsFragment.class.getSimpleName() + "_DEBUG";

    public static AcceptedEventsFragment newInstance() {
        AcceptedEventsFragment fragment = new AcceptedEventsFragment();
        return fragment;
    }

    @Override
    protected void populateEventList() {
        // Query for new results from the network.
        ParseQuery<EventUser> query = EventUser.getQueryForAcceptedEvents();

        query.findInBackground(
            new FindCallback<EventUser>() {
                @Override
                public void done(List<EventUser> eventUsers, ParseException e) {
                List<Event> events = new ArrayList<Event>();
                for (EventUser eventUser : eventUsers) {
                    events.add(eventUser.getEvent());
                    statusMap.put(eventUser.getEvent().getObjectId(), eventUser.getStatus().toString());
                }
                eventAdapter.addAll(events);
                }
            }
        );
    }

    public void addNewEventToList(String eventId, final String attendanceStatus) {
        // Define the class we would like to query
        ParseQuery<Event> query = Event.getQueryForEventWithId(eventId);

        query.getFirstInBackground(new GetCallback<Event>() {
            @Override
            public void done(Event event, ParseException e) {
                if (e == null) {
                    statusMap.put(event.getObjectId(), attendanceStatus);
                    eventAdapter.add(event);
                    Log.d("DEBUG", "back to main");
                    Log.d("DEBUG", event.getTitle().toString());
                } else {
                    System.err.println(
                            "AcceptedEventsFragment.addNewEventToList: " + e.getMessage());
                }
            }
        });
    }
}
