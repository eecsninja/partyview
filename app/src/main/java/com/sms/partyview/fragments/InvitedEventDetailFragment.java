package com.sms.partyview.fragments;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sms.partyview.R;
import com.sms.partyview.models.AttendanceStatus;
import com.sms.partyview.models.Event;
import com.sms.partyview.models.EventUser;
import com.sms.partyview.models.LocalEvent;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

import static com.sms.partyview.models.AttendanceStatus.ACCEPTED;
import static com.sms.partyview.models.AttendanceStatus.DECLINED;


public class InvitedEventDetailFragment extends EventDetailFragment {


    private InviteFragmentListener mListener;

    // For passing in intent data.
    // TODO: These are also in class EventDetailActivity. Find some way to
    // put them in a common place.
    public static final String EVENT_INTENT_KEY = "event";

    public interface InviteFragmentListener {
        public void onSaveResponse(String response, LocalEvent event);
    }

    public static InvitedEventDetailFragment newInstance(LocalEvent event) {
        InvitedEventDetailFragment fragment = new InvitedEventDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(EVENT_INTENT_KEY, event);
        fragment.setArguments(args);
        return fragment;
    }

    // Save the response locally if it's not ready to be saved to database yet.
    private String mSelectedResponse = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // TODO: This code is quite similar to the stuff in EventDetailActivity.
        // They should be related classes.
        tempEvent = (LocalEvent) getArguments().getSerializable(EVENT_INTENT_KEY);

        latitude = tempEvent.getLatitude();
        longitude = tempEvent.getLongitude();


        retrieveEvent();

        retrieveCurrentEventUser();
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);

        // Add accept/reject buttons to activity.
        LinearLayout llEventDetailButtons =
                (LinearLayout) view.findViewById(R.id.llEventDetailButtons);
        LayoutInflater inflater =
                (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout llInviteButtons =
                (LinearLayout) inflater.inflate(
                        R.layout.layout_invite_detail_buttons, llEventDetailButtons, false);
        llEventDetailButtons.addView(llInviteButtons);

        view.findViewById(R.id.btnViewAttendees).setVisibility(View.GONE);
        if (tempEvent != null) {
            populateEventInfo();
            if (!tempEvent.getTitle().isEmpty()) {
//              //  getActivity().getActionBar().setTitle(
//                        getString(R.string.title_activity_invite_detail) + " " +
//                                tempEvent.getTitle());
            }
        }

    }

    @Override
    protected void retrieveAttendeeList() {
        ParseQuery eventQuery = ParseQuery.getQuery(Event.class);
        eventQuery.whereEqualTo("objectId", tempEvent.getObjectId());

        // Define the class we would like to query
        ParseQuery<EventUser> query = ParseQuery.getQuery(EventUser.class);
        query.whereMatchesQuery("event", eventQuery);
        query.include("user");

        query.findInBackground(new FindCallback<EventUser>() {
            @Override
            public void done(List<EventUser> users, ParseException e) {
            String eventUsers = "";
            for (EventUser eventUser : users) {
                if (eventUser != null) {
                    if (eventUsers.equals("")) {
                        eventUsers += eventUser.getUser().getUsername();
                    } else {
                        eventUsers += ", " + eventUser.getUser().getUsername();
                    }
                }
            }
            mTvAttendeeList.setText(eventUsers);

            }
        });
    }

    private void retrieveCurrentEventUser() {
        ParseQuery eventQuery = ParseQuery.getQuery(Event.class);
        eventQuery.whereEqualTo("objectId", tempEvent.getObjectId());

        ParseQuery userQuery = ParseQuery.getQuery(ParseUser.class);
        userQuery.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());

        // Define the class we would like to query
        ParseQuery<EventUser> query = ParseQuery.getQuery(EventUser.class);
        query.whereMatchesQuery("event", eventQuery);
        query.whereMatchesQuery("user", userQuery);

        query.getFirstInBackground(new GetCallback<EventUser>() {
            @Override
            public void done(EventUser eventUser, ParseException e) {
                currentEventUser = eventUser;
                // If user already selected a response, save that response.
                if (mSelectedResponse != null) {
                   // saveAndFinish(mSelectedResponse);
                }
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (InviteFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    public void onAcceptInvite(View v) {
        respondToInvite(ACCEPTED);
    }

    public void onRejectInvite(View v) {
        respondToInvite(DECLINED);
    }

    public void respondToInvite(final AttendanceStatus status) {
        ParseQuery<EventUser> query = ParseQuery.getQuery("EventUser");

        // Save the response locally, so it could be sent later if no current event user is loaded.
        mSelectedResponse = status.toString();

        // Do not attempt to save to database if there is no current event user object loaded.
        if (currentEventUser != null) {
            saveAndFinish(status.toString());
        }
    }

    private void saveAndFinish(String response) {
        currentEventUser.put("status", response);
        currentEventUser.saveInBackground();

        mListener.onSaveResponse(response, tempEvent);
    }
}
