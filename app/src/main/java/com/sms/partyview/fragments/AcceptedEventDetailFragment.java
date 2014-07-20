package com.sms.partyview.fragments;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetCallback;
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
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import static com.sms.partyview.models.AttendanceStatus.ACCEPTED;
import static com.sms.partyview.models.AttendanceStatus.PRESENT;

public class AcceptedEventDetailFragment
        extends EventDetailFragment {

    // For passing in intent data.
    public static final String EVENT_INTENT_KEY = "event";
    public static final String UDPATED_EVENT_INTENT_KEY = "updatedEvent";
    public static final String EVENT_LIST_INDEX_KEY = "eventListIndex";
    private static final int EDIT_EVENT_REQUEST = 1;
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
//        retrieveEvent();
    }

    // Displays an event in the activity's UI.
    public void saveAndDisplayEvent(LocalEvent event) {
        tempEvent = event;
        if (tempEvent.getTitle().isEmpty()) {
            return;
        }
        //getActionBar().setTitle(tempEvent.getTitle());
        if (tempEvent != null) {
            populateEventInfo();
            getActivity().getActionBar().setTitle(tempEvent.getTitle());
        }
    }


    @Override
    public void setupViews(View view) {
        super.setupViews(view);

        view.findViewById(R.id.llAttendees).setVisibility(View.GONE);

        // Dynamically create button.
        btnJoinLeave = new Button(getActivity());
        btnJoinLeave.setLayoutParams(
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
        );
        btnJoinLeave.setPadding(1, 1, 1, 1);
        btnJoinLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onJoinLeave(view);
            }
        });
        btnJoinLeave.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

        // Add it to the layout section.
      //  LinearLayout llEventDetailButtons = (LinearLayout) view.findViewById(R.id.llEventDetailButtons);
       // llEventDetailButtons.addView(btnJoinLeave);


        saveAndDisplayEvent((LocalEvent) getArguments().getSerializable(EVENT_INTENT_KEY));
        retrieveEvent();
    }

    @Override
    public void populateEventInfo() {
        super.populateEventInfo();
        // Show the attendance status on the join/leave button.
        if (status.equals(PRESENT)) {
            btnJoinLeave.setText(getString(R.string.leave_event));
        } else if (status.equals(ACCEPTED)) {
            btnJoinLeave.setText(getString(R.string.join_event));
        }
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
                            toggleJoinLeave(status);
                        }
                        eventUserStrings.add(eventUser.getUser().getUsername());
                    }
                }
                mTvAttendeeList.setText(Utils.joinStrings(eventUserStrings, ", "));
             //   setupMapFragment();
            }
        });
    }

    public void onJoinLeave(View v) {
        String currentState = btnJoinLeave.getText().toString();
        if (currentState.equals(getString(R.string.leave_event))) {
            toggleJoinLeave(ACCEPTED);
        } else {
            toggleJoinLeave(PRESENT);
        }

    }

    public void toggleJoinLeave(AttendanceStatus status) {
        if (status != null) {
            if (status.equals(PRESENT)) {
                btnJoinLeave.setText(getString(R.string.leave_event));
                this.status = PRESENT;
            } else if (status.equals(ACCEPTED)) {
                btnJoinLeave.setText(getString(R.string.join_event));
                this.status = ACCEPTED;
            }

            if (currentEventUser != null) {

                updateAttendeeInList(currentEventUser, status);

                ParseQuery<EventUser> query = ParseQuery.getQuery("EventUser");

                // Retrieve the object by id
                query.getInBackground(currentEventUser.getObjectId(), new GetCallback<EventUser>() {
                    public void done(EventUser eventUser, ParseException e) {
                        if (e == null) {
                            updateUserStatus(eventUser);
                        }
                    }
                });
            }
        }
    }

    public void updateAttendeeInList(EventUser user, AttendanceStatus status) {
        for (Attendee attendee : attendees) {
            if (attendee.getUsername().equals(user.getUser().getUsername())) {
                attendee.setStatus(status);
                break;
            }
        }
    }

    public void updateUserStatus(EventUser eventUser) {
        eventUser.put("status", status.toString());
        eventUser.saveInBackground();
    }

}
