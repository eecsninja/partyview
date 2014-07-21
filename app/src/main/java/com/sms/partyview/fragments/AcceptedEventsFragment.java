package com.sms.partyview.fragments;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.sms.partyview.models.Event;
import com.sms.partyview.models.EventUser;

import android.util.Log;

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
    protected ParseQuery<EventUser> getQueryForEvents() {
        return EventUser.getQueryForAcceptedEvents();
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
