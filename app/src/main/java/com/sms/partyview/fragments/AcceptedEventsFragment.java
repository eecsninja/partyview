package com.sms.partyview.fragments;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sms.partyview.AttendanceStatus;
import com.sms.partyview.models.Event;
import com.sms.partyview.models.EventUser;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by myho on 7/3/14.
 */
public class AcceptedEventsFragment extends EventListFragment {

    public static AcceptedEventsFragment newInstance() {
        AcceptedEventsFragment fragment = new AcceptedEventsFragment();
        return fragment;
    }

    @Override
    protected void populateEventList() {
        // Define the class we would like to query
        ParseQuery<EventUser> query = ParseQuery.getQuery(EventUser.class);

        // Define our query conditions

        // get list of events where user
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.whereNotEqualTo("status", AttendanceStatus.DECLINED.toString());
        query.whereNotEqualTo("status", AttendanceStatus.INVITED.toString());
        query.addAscendingOrder("date");
        query.include("host");
        query.include("event");
        query.include("host");

        query.findInBackground(
            new FindCallback<EventUser>() {
                @Override
                public void done(List<EventUser> eventUsers, ParseException e) {

                List<Event> events = new ArrayList<Event>();
                for (EventUser eventUser : eventUsers) {
                    Event event = eventUser.getEvent();
                    events.add(event);

                    event.getHost().fetchInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            eventAdapter.notifyDataSetChanged();
                        }
                    });
                }
                eventAdapter.addAll(events);
                }
            }
        );
    }

    public void addNewEventToList(String eventId) {
        // Define the class we would like to query
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);

        // Define our query conditions
        query.whereEqualTo("objectId", eventId);
        query.include("host");

        query.getFirstInBackground(new GetCallback<Event>() {
            @Override
            public void done(Event event, ParseException e) {
                eventAdapter.add(event);
                Log.d("DEBUG", "back to main");
                Log.d("DEBUG", event.getTitle().toString());
            }
        });
    }
}
