package com.sms.partyview.fragments;


import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sms.partyview.R;
import com.sms.partyview.helpers.Utils;
import com.sms.partyview.models.AttendanceStatus;
import com.sms.partyview.models.Attendee;
import com.sms.partyview.models.EventUser;
import com.sms.partyview.models.LocalEvent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import static com.sms.partyview.models.AttendanceStatus.ACCEPTED;

public class AcceptedEventDetailFragment
        extends EventDetailFragment {

    // For passing in intent data.
    public static final String EVENT_INTENT_KEY = "event";
    public static final String UDPATED_EVENT_INTENT_KEY = "updatedEvent";
    public static final String EVENT_LIST_INDEX_KEY = "eventListIndex";
    private AttendanceStatus status;
    private boolean mEventWasUpdated = false;
    private int mEventListIndex;

    private Button btnJoinLeave;
    private List<EventUser> eventUsers;



    public static AcceptedEventDetailFragment newInstance(String status, int eventListIndexKey, LocalEvent tempEvent) {
        AcceptedEventDetailFragment fragment = new AcceptedEventDetailFragment();
        Bundle args = new Bundle();
        args.putString("eventStatus", status);
        args.putInt(EVENT_LIST_INDEX_KEY, eventListIndexKey);
        args.putSerializable(EVENT_INTENT_KEY, tempEvent);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        status = ACCEPTED;

        eventUsers = new ArrayList<EventUser>();
        attendees = new ArrayList<Attendee>();


        status = AttendanceStatus.valueOf(getArguments().getString("eventStatus"));

        mEventListIndex = getArguments().getInt(EVENT_LIST_INDEX_KEY);
        tempEvent = (LocalEvent) getArguments().getSerializable(EVENT_INTENT_KEY);

        latitude = tempEvent.getLatitude();
        longitude = tempEvent.getLongitude();
    }

    // Displays an event in the activity's UI.
    public void saveAndDisplayEvent(LocalEvent event) {
        tempEvent = event;
        if (tempEvent.getTitle().isEmpty()) {
            return;
        }
        if (tempEvent != null) {
            populateEventInfo();
            getActivity().getActionBar().setTitle(tempEvent.getTitle());
        }
    }


    @Override
    public void setupViews(View view) {
        super.setupViews(view);

        view.findViewById(R.id.llAttendees).setVisibility(View.GONE);

        saveAndDisplayEvent((LocalEvent) getArguments().getSerializable(EVENT_INTENT_KEY));
        retrieveEvent();
    }

    @Override
    protected void retrieveAttendeeList() {
        ParseQuery<EventUser> query = EventUser.getQueryForAttendeeList(tempEvent.getObjectId());
        query.findInBackground(new FindCallback<EventUser>() {
            @Override
            public void done(List<EventUser> users, ParseException e) {
                List<String> eventUserStrings = new ArrayList<String>();
                for (EventUser eventUser : users) {
                    if (eventUser != null) {
                        eventUsers.add(eventUser);
                        attendees.add(new Attendee(eventUser));
                        if (eventUser.getUser().getObjectId()
                                .equals(ParseUser.getCurrentUser().getObjectId())) {
                            currentEventUser = eventUser;
                            status = eventUser.getStatus();
                        }
                        eventUserStrings.add(eventUser.getUser().getUsername());
                    }
                }
                mTvAttendeeList.setText(Utils.joinStrings(eventUserStrings, ", "));
            }
        });
    }

}
