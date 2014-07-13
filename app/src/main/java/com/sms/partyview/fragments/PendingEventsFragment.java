package com.sms.partyview.fragments;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sms.partyview.models.AttendanceStatus;
import com.sms.partyview.activities.InviteDetailActivity;
import com.sms.partyview.helpers.Utils;
import com.sms.partyview.models.Event;
import com.sms.partyview.models.EventUser;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by myho on 7/3/14.
 */
public class PendingEventsFragment extends EventListFragment {

    public static PendingEventsFragment newInstance() {
        PendingEventsFragment fragment = new PendingEventsFragment();
        return fragment;
    }

    @Override
    protected void populateEventList() {
        // Define the class we would like to query
        ParseQuery<EventUser> query = ParseQuery.getQuery(EventUser.class);

        // Define our query conditions

        // get list of events where user
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.whereEqualTo("status", AttendanceStatus.INVITED.toString());
        query.addAscendingOrder("date");
        query.include("event.host");

        query.findInBackground(
                new FindCallback<EventUser>() {
                    @Override
                    public void done(List<EventUser> eventUsers, ParseException e) {
                        if (e == null) {
                          //  Log.d("DEBUG", "invited eventUsers");
                          //  Log.d("DEBUG", eventUsers.size() + eventUsers.toString());
                            List<Event> events = new ArrayList<Event>();
                            for (EventUser eventUser : eventUsers) {
                                events.add(eventUser.getEvent());
                            }
                            eventAdapter.addAll(events);
                        }
                    }
                }
        );
    }

    @Override
    protected void setUpDisplayDetailedView() {
        eventsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Event event = events.get(position);
                Intent intent = new Intent(getActivity(), InviteDetailActivity.class);
                intent.putExtra("eventId", event.getObjectId());
                intent.putExtra("eventTitle", event.getTitle());
                getActivity().startActivityForResult(intent, Utils.RESPOND_TO_INVITE_EVENT_REQUEST_CODE);
            }
        });
    }

    public void removeEventFromList(String eventId) {
        // Define the class we would like to query
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);

        // Define our query conditions
        query.whereEqualTo("objectId", eventId);
        query.include("host");

        query.getFirstInBackground(new GetCallback<Event>() {
            @Override
            public void done(Event event, ParseException e) {
                eventAdapter.remove(event);
                Log.d("DEBUG", "back to main");
                Log.d("DEBUG", event.getTitle().toString());
            }
        });
    }
}
