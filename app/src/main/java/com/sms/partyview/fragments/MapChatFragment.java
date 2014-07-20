package com.sms.partyview.fragments;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.sms.partyview.R;
import com.sms.partyview.models.AttendanceStatus;
import com.sms.partyview.models.Attendee;
import com.sms.partyview.models.EventUser;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import java.util.ArrayList;
import static com.sms.partyview.models.AttendanceStatus.ACCEPTED;
import static com.sms.partyview.models.AttendanceStatus.PRESENT;

public class MapChatFragment extends Fragment implements EventMapFragment.EventMapFragmentListener,
        ChatFragment.OnFragmentInteractionListener{

    private EventMapFragment eventMapFragment;
    private ArrayList<Attendee> attendees;
    private String currentEventUserObjId;
    private String eventId;
    private Double latitude;
    private Double longitude;
    private ChatFragment chatFragment;
    private AttendanceStatus status;
    private Switch switchShareLocation;

    public static MapChatFragment newInstance(ArrayList<Attendee> attendees, String currentEventUserObjId,
                                              String eventId, Double latitude, Double longitude, String status) {
        MapChatFragment fragment = new MapChatFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("attendees", attendees);
        args.putString("currentEventUserObjId", currentEventUserObjId);
        args.putString("eventId", eventId);
        args.putDouble("latitude", latitude);
        args.putDouble("longitude", longitude);
        args.putString("status", status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        attendees = getArguments().getParcelableArrayList("attendees");
        currentEventUserObjId = getArguments().getString("currentEventUserObjId");
        eventId = getArguments().getString("eventId");
        latitude = getArguments().getDouble("latitude");
        longitude = getArguments().getDouble("longitude");
        status = AttendanceStatus.valueOf(getArguments().getString("status"));
        setupMapFragment();
        setupChatFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout.
        View view = inflater.inflate(
                R.layout.fragment_full_map, container, false);
        switchShareLocation = (Switch) view.findViewById(R.id.switchLocation);
        toggleJoinLeave(status);
        return view;
    }

    public void setupMapFragment() {
        // Create the transaction
        FragmentTransaction fts = getActivity().getSupportFragmentManager().beginTransaction();
        // Replace the content of the container
        eventMapFragment = EventMapFragment.newInstance(attendees, currentEventUserObjId, eventId, latitude, longitude);
        fts.replace(R.id.flFullMapContainer, eventMapFragment);
        fts.commit();
    }

    public void setupChatFragment() {
        // Create the transaction
        FragmentTransaction fts = getActivity().getSupportFragmentManager().beginTransaction();
        // Replace the content of the container
        chatFragment = ChatFragment.newInstance(eventId);
        fts.replace(R.id.flChatContainer2, chatFragment);
        fts.commit();
    }

    public void onViewCreated() {
        if (eventMapFragment != null) {
            eventMapFragment.setMarkerVisibility(status.equals(PRESENT));
        }
    }

    public void onJoinLeave(View v) {
        if (switchShareLocation.isChecked()) {
            toggleJoinLeave(PRESENT);
        } else {
            toggleJoinLeave(ACCEPTED);
        }

    }

    public void toggleJoinLeave(AttendanceStatus status) {
        if (status != null) {
            if (status.equals(PRESENT)) {
                this.status = PRESENT;
                switchShareLocation.setChecked(true);
            } else if (status.equals(ACCEPTED)) {
                this.status = ACCEPTED;
                switchShareLocation.setChecked(false);
            }

            if (eventMapFragment != null) {
                eventMapFragment.setMarkerVisibility(status.equals(PRESENT));
            }

            if (currentEventUserObjId != null) {

                updateAttendeeInList(status);

                ParseQuery<EventUser> query = ParseQuery.getQuery("EventUser");

                // Retrieve the object by id
                query.getInBackground(currentEventUserObjId, new GetCallback<EventUser>() {
                    public void done(EventUser eventUser, ParseException e) {
                        if (e == null) {
                            updateUserStatus(eventUser);
                        }
                    }
                });
            }
        }
    }

    public void updateAttendeeInList(AttendanceStatus status) {
        for (Attendee attendee : attendees) {
            if (attendee.getObjectId().equals(currentEventUserObjId)) {
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
