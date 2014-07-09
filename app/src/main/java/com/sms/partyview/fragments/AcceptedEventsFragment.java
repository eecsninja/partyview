package com.sms.partyview.fragments;

import com.google.common.collect.ImmutableList;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sms.partyview.models.Event;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);

        // Define our query conditions
        // TODO: add condition when attendee include currentUser
        query.whereEqualTo("host", ParseUser.getCurrentUser());
        query.addAscendingOrder("date");
        query.include("host");

        query.findInBackground(new FindCallback<Event>() {
                                   @Override
                                   public void done(List<Event> events, ParseException e) {
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
