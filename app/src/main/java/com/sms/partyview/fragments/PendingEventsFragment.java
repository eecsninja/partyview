package com.sms.partyview.fragments;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.sms.partyview.activities.InvitedEventDetailFragment;
import com.sms.partyview.helpers.Utils;
import com.sms.partyview.models.Event;
import com.sms.partyview.models.EventUser;
import com.sms.partyview.models.LocalEvent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by myho on 7/3/14.
 */
public class PendingEventsFragment extends EventListFragment {

    private static final String TAG = PendingEventsFragment.class.getSimpleName() + "_DEBUG";

    public static PendingEventsFragment newInstance() {
        PendingEventsFragment fragment = new PendingEventsFragment();
        return fragment;
    }

    @Override
    protected void populateEventList() {
        // Define the class we would like to query
        ParseQuery<EventUser> query = EventUser.getQueryForPendingEvents();

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
                Log.d(TAG, "starting detail view");
                Event event = events.get(position);
                Intent intent = new Intent(getActivity(), InvitedEventDetailFragment.class);
                intent.putExtra(InvitedEventDetailFragment.EVENT_INTENT_KEY, new LocalEvent(event));
                getActivity().startActivityForResult(intent, Utils.RESPOND_TO_INVITE_EVENT_REQUEST_CODE);
            }
        });
    }

    public void removeEventFromList(String eventId) {
        ParseQuery<Event> query = Event.getQueryForEventWithId(eventId);

        query.getFirstInBackground(new GetCallback<Event>() {
            @Override
            public void done(Event event, ParseException e) {
                if (e == null) {
                    eventAdapter.remove(event);
                    Log.d("DEBUG", "back to main");
                    Log.d("DEBUG", event.getTitle().toString());
                } else {
                    System.err.println("PendingEventsFragment.removeEventFromList: " +
                            e.getMessage());
                }
            }
        });
    }
}
