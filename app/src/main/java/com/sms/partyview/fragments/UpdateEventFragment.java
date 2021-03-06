package com.sms.partyview.fragments;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sms.partyview.models.Event;
import com.sms.partyview.models.EventUser;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import static com.sms.partyview.helpers.Utils.DISPLAY_DATE_FORMATTER;
import static com.sms.partyview.helpers.Utils.DISPLAY_TIME_FORMATTER;

/**
 * Created by sque on 7/12/14.
 */
public class UpdateEventFragment extends EditEventFragment {

    protected Event mEvent = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mBtnSubmit.setText("Update Event");
        return view;
    }

    // Loads an event for editing. Fills the fields with the event parameters.
    public void loadEvent(Event event) {
        mEvent = event;

        mEtTitle.getEditText().setText(event.getTitle());
        mEtAddress.setText(event.getAddress());
        mEtDescription.setText(event.getDescription());

        mStartDateTime = new MutableDateTime(event.getStartDate());
        mEndDateTime = new MutableDateTime(event.getEndDate());
        populateDatTimeViews();
        getEventInvitees(event);
    }

    protected void getEventInvitees(final Event event) {
        // Get a query for this event.
        String eventId = event.getObjectId();
        ParseQuery eventQuery = ParseQuery.getQuery(Event.class);
        eventQuery.whereEqualTo("objectId", eventId);

        // Get all EventUser objects for the event.
        ParseQuery<EventUser> eventUserQuery = ParseQuery.getQuery(EventUser.class);
        eventUserQuery.whereMatchesQuery("event", eventQuery);
        eventUserQuery.include("user");

        // Find it!
        eventUserQuery.findInBackground(new FindCallback<EventUser>() {
            @Override
            public void done(List<EventUser> eventUsers, ParseException e) {
                if (e != null) {
                    System.err.println(e.getMessage() + " " + e.getCode());
                    return;
                }
                String inviteeString = "";
                for (EventUser eventUser : eventUsers) {
                    // Skip the current user, who should not be included in the invitee list.
                    if (eventUser == null || eventUser.getUser() == ParseUser.getCurrentUser()) {
                        continue;
                    }
                    // Use a comma to separate the user names.
                    if (!inviteeString.isEmpty()) {
                        inviteeString += ", ";
                    }
                    inviteeString += eventUser.getUser().getUsername();
                }
                mAutoTvInvites.setText(inviteeString);
            }
        });
    }

    @Override
    public Event getEventFromInputData() {
        // Load input field data into the existing event object.
        if (mEvent != null) {
            readEventInfoFromInputFields(mEvent);
        }
        return mEvent;
    }
}
