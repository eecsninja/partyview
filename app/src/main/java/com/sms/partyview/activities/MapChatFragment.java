package com.sms.partyview.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sms.partyview.R;
import com.sms.partyview.fragments.ChatFragment;
import com.sms.partyview.fragments.EventMapFragment;
import com.sms.partyview.models.Attendee;

import java.util.ArrayList;

public class MapChatFragment extends Fragment implements EventMapFragment.EventMapFragmentListener,
        ChatFragment.OnFragmentInteractionListener{

    private EventMapFragment eventMapFragment;
    private ArrayList<Attendee> attendees;
    private String currentEventUserObjId;
    private String eventId;
    private Double latitude;
    private Double longitude;
    private ChatFragment chatFragment;

    public static MapChatFragment newInstance(ArrayList<Attendee> attendees, String currentEventUserObjId,
                                              String eventId, Double latitude, Double longitude) {
        MapChatFragment fragment = new MapChatFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("attendees", attendees);
        args.putString("currentEventUserObjId", currentEventUserObjId);
        args.putString("eventId", eventId);
        args.putDouble("latitude", latitude);
        args.putDouble("longitude", longitude);
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
        setupMapFragment();
        setupChatFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout.
        View view = inflater.inflate(
                R.layout.fragment_full_map, container, false);
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

    }
}
