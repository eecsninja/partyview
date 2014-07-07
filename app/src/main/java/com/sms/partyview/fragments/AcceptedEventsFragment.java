package com.sms.partyview.fragments;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sms.partyview.models.Event;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
        query.whereEqualTo("host", ParseUser.getCurrentUser());
        query.addAscendingOrder("date");

        query.findInBackground(new FindCallback<Event>() {
                                   @Override
                                   public void done(List<Event> events, ParseException e) {
                                       for (Event event : events) {
                                           event.getHost()
                                                   .fetchIfNeededInBackground(
                                                           new GetCallback<ParseObject>() {
                                                               public void done(ParseObject object,
                                                                       ParseException e) {
                                                               }
                                                           }
                                                   );
                                       }

                                       eventAdapter.addAll(events);
                                   }
                               }
        );
    }
}
