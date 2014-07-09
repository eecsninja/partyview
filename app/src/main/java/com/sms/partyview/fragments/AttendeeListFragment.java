package com.sms.partyview.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.sms.partyview.AttendanceStatus;
import com.sms.partyview.R;
import com.sms.partyview.adapters.AttendeeArrayAdapter;
import com.sms.partyview.models.Event;
import com.sms.partyview.models.EventUser;

import java.util.ArrayList;
import java.util.List;

public class AttendeeListFragment extends Fragment {
    protected List<EventUser> attendees;

    protected AttendeeArrayAdapter attendeeArrayAdapter;

    protected ListView attendeesView;

    private OnFragmentInteractionListener mListener;

    private String eventId;


    public static AttendeeListFragment newInstance(String eventId) {
        AttendeeListFragment fragment = new AttendeeListFragment();
        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        fragment.setArguments(args);
        return fragment;
    }
    public AttendeeListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize event list and adapter.
        attendees = new ArrayList<EventUser>();
        attendeeArrayAdapter = new AttendeeArrayAdapter(getActivity(), attendees);

        eventId = getArguments().getString("eventId");
        retrieveEventUsers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout.
        View view = inflater.inflate(
                R.layout.fragment_attendee_list, container, false);

        // Set up to display tweets.
        attendeesView = (ListView) view.findViewById(R.id.lvAttendees);
        attendeesView.setAdapter(attendeeArrayAdapter);

        // Return it.
        return view;
    }

    public void updateAttendeeStatus(EventUser user, AttendanceStatus status) {
        int pos = -1;
        for (int i = 0; i < attendees.size(); i++) {
            EventUser attendee = attendees.get(i);
            if (attendee.getObjectId().equals(user.getObjectId())) {
                pos = i;
                break;
            }
        }
        if (pos != -1) {
            attendeeArrayAdapter.getItem(pos).setStatus(status);
            attendeeArrayAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        public void onUsersLoaded(List<EventUser> attendees);
    }

    private void retrieveEventUsers() {
        ParseQuery eventQuery = ParseQuery.getQuery(Event.class);
        eventQuery.whereEqualTo("objectId", eventId);

        // Define the class we would like to query
        ParseQuery<EventUser> query = ParseQuery.getQuery(EventUser.class);
        query.whereMatchesQuery("event", eventQuery);
        query.include("user");

        query.findInBackground(new FindCallback<EventUser>() {
            @Override
            public void done(List<EventUser> attendees, ParseException e) {
                for (EventUser attendee : attendees) {
                    attendeeArrayAdapter.add(attendee);
                }
                mListener.onUsersLoaded(attendees);
            }
        });
    }

}
