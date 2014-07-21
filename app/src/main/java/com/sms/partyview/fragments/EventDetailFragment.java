package com.sms.partyview.fragments;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.sms.partyview.R;
import com.sms.partyview.models.Attendee;
import com.sms.partyview.models.Event;
import com.sms.partyview.models.EventUser;
import com.sms.partyview.models.LocalEvent;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.ArrayList;

import static com.sms.partyview.helpers.Utils.DISPLAY_DATE_TIME_FORMATTER;

/**
 * Created by sque on 7/16/14.
 */
public abstract class EventDetailFragment extends Fragment {

    // Data objects.
    protected Event mEvent;
    protected EventUser currentEventUser;
    protected LocalEvent tempEvent;
    protected ArrayList<Attendee> attendees;

    // Views.
    protected TextView mTvTitle;
    protected TextView mTvOrganizer;
    protected TextView mTvDescription;
    protected TextView mTvStartTime;
    protected TextView mTvEndTime;
    protected TextView mTvLocation;
    protected TextView mTvAttendeeList;

    //Fonts
    protected Typeface openSansTypeface;
    protected Typeface openSansBold;

    // Maps
    protected GoogleMap mMap;
    protected Double latitude;
    protected Double longitude;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout.
        View view = inflater.inflate(
                R.layout.fragment_event_detail, container, false);

        openSansTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Aller_Rg.ttf");
        openSansBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Aller_Bd.ttf");
        setupViews(view);

        // Return it.
        return view;
    }

    protected void setupViews(View view) {
        mTvTitle = (TextView) view.findViewById(R.id.tvEventName);
        mTvOrganizer = (TextView) view.findViewById(R.id.tvEventOrganizer);
        mTvDescription = (TextView) view.findViewById(R.id.tvEventDescription);
        mTvStartTime = (TextView) view.findViewById(R.id.tvEventStartTime);
        mTvEndTime = (TextView) view.findViewById(R.id.tvEventEndTime);
        mTvLocation = (TextView) view.findViewById(R.id.tvEventLocation);
        mTvAttendeeList = (TextView) view.findViewById(R.id.tvEventAttendeeList);
    }

    protected void populateEventInfo () {
        mTvTitle.setText(tempEvent.getTitle());
        mTvOrganizer.setText(tempEvent.getHost());
        mTvLocation.setText(tempEvent.getAddress());
        mTvDescription.setText(tempEvent.getDescription());
        mTvStartTime.setText(DISPLAY_DATE_TIME_FORMATTER.print(new DateTime(tempEvent.getStartDate())));
        mTvEndTime.setText(DISPLAY_DATE_TIME_FORMATTER.print(new DateTime(tempEvent.getEndDate())));
    }

    protected void retrieveEvent() {
        ParseQuery<Event> query = Event.getQueryForEventWithId(tempEvent.getObjectId());

        query.getFirstInBackground(new GetCallback<Event>() {
            @Override
            public void done(Event event, ParseException e) {
                mEvent = event;
                Log.d("DEBUG", "in detailed view");
                Log.d("DEBUG", mEvent.getTitle().toString());

                retrieveAttendeeList();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isPlayServicesAvailable()) {
            setUpMapIfNeeded();
        }
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getActivity()
                    .getSupportFragmentManager().findFragmentById(R.id.location_map);

            if (mapFragment != null) {
                mMap = mapFragment.getMap();
                // Check if we were successful in obtaining the map.
                if (mMap != null)
                    setUpMap();
            }
        }
    }

    private void setUpMap() {
        // For showing a move to my loction button
        mMap.setMyLocationEnabled(true);
        // For dropping a marker at a point on the  Map
        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(getString(R.string.label_event_location)));
        // For zooming automatically to the Dropped PIN Location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,
                longitude), 12.0f));

        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    protected boolean isPlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());

        if (status == ConnectionResult.SUCCESS) {
            return (true);
        } else if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
            // deal with error
        } else {
            // maps is not available
        }

        return (false);
    }

    public void onViewAttendees(View view) {
        AttendeeListDialogFragment.show(getActivity(), getString(R.string.attendees_title), attendees);
    }
    protected abstract void retrieveAttendeeList();
}
