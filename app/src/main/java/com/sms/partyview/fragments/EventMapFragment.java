package com.sms.partyview.fragments;

import android.app.Activity;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sms.partyview.R;
import com.sms.partyview.models.Attendee;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class EventMapFragment extends Fragment {

    private ArrayList<Marker> markers;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private ArrayList<Attendee> attendees;
    private EventMapFragmentListener mListener;

    public interface EventMapFragmentListener {
        public void onViewCreated();
    }

    public static EventMapFragment newInstance(ArrayList<Attendee> attendees) {
        EventMapFragment fragment = new EventMapFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("attendees", attendees);
        fragment.setArguments(args);
        return fragment;
    }


    public EventMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize event list and adapter.
        attendees = getArguments().getParcelableArrayList("attendees");
        markers = new ArrayList<Marker>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout.
        View view = inflater.inflate(
                R.layout.fragment_event_map, container, false);

        mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.mapView);
        map = mapFragment.getMap();

        addUsersToMap(attendees);

        if (mListener != null) {
            mListener.onViewCreated();
        }

        // Return it.
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (EventMapFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private void addUsersToMap(List<Attendee> attendees) {
        if (map != null) {
            for (Attendee attendee : attendees) {
                Marker marker = map.addMarker(new MarkerOptions()
                        .position(new LatLng(attendee.getLatitude(),
                                attendee.getLongitude()))
                        .title(attendee.getUsername()));

                markers.add(marker);
            }
            if (attendees.size() > 0) {
                updateCameraView();
            }
        }
    }

    private void updateCameraView() {
        //Calculate the markers to get their position
        LatLngBounds.Builder b = new LatLngBounds.Builder();
        for (Marker m : markers) {
            b.include(m.getPosition());
        }
        LatLngBounds bounds = b.build();
        //Change the padding as per needed
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 25, 25, 0);
        map.animateCamera(cu);
    }

    public void setOnMapClick(GoogleMap.OnMapClickListener mapClickListener) {
        if (map != null) {
            map.setOnMapClickListener(mapClickListener);
        }
    }

    public void setMarkerVisibility(boolean visible) {
        for (Marker marker : markers) {
            marker.setVisible(visible);
        }
    }
}
