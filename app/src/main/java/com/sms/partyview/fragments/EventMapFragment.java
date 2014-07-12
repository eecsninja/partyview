package com.sms.partyview.fragments;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;
import com.sms.partyview.R;
import com.sms.partyview.models.Attendee;
import com.sms.partyview.models.Event;
import com.sms.partyview.models.EventUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class EventMapFragment extends Fragment implements LocationListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private ArrayList<Marker> markers;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private ArrayList<Attendee> attendees;
    private EventMapFragmentListener mListener;

    /*
     * Constants for location update parameters
     */
    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;

    // The update interval
    private static final int UPDATE_INTERVAL_IN_SECONDS = 5;

    // A fast interval ceiling
    private static final int FAST_CEILING_IN_SECONDS = 1;

    // Update interval in milliseconds
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
            * UPDATE_INTERVAL_IN_SECONDS;

    // A fast ceiling of update intervals, used when the app is visible
    private static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
            * FAST_CEILING_IN_SECONDS;

    private Location lastLocation = null;
    private Location currentLocation = null;

    // A request to connect to Location Services
    private LocationRequest locationRequest;

    // Stores the current instantiation of the location client in this object
    private LocationClient locationClient;


    private String currentEventUserObjId;

    private String eventId;

    private Pubnub pubnub;

    public static final String PUBLISH_KEY = "pub-c-adf5251f-8c96-477d-95fd-ab1907f93905";
    public static final String SUBSCRIBE_KEY = "sub-c-2f5285ae-08b6-11e4-9ae5-02ee2ddab7fe";

    public interface EventMapFragmentListener {
        public void onViewCreated();
    }

    public static EventMapFragment newInstance(ArrayList<Attendee> attendees, String currentEventUserObjId, String eventId) {
        EventMapFragment fragment = new EventMapFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("attendees", attendees);
        args.putString("currentEventUserObjId", currentEventUserObjId);
        args.putString("eventId", eventId);
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
        currentEventUserObjId = getArguments().getString("currentEventUserObjId");
        eventId = getArguments().getString("eventId");

        markers = new ArrayList<Marker>();


        // Create a new global location parameters object
        locationRequest = LocationRequest.create();

        // Set the update interval
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Use high accuracy
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the interval ceiling to one minute
        locationRequest.setFastestInterval(FAST_INTERVAL_CEILING_IN_MILLISECONDS);

        // Create a new location client, using the enclosing class to handle callbacks.
        locationClient = new LocationClient(getActivity(), this, this);

        subscribeToChannel();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout.
        View view = inflater.inflate(
                R.layout.fragment_event_map, container, false);

        setUpMapIfNeeded();

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

    @Override
    public void onStop() {
        if (locationClient.isConnected()) {
            stopPeriodicUpdates();
        }
        locationClient.disconnect();

        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();

        locationClient.connect();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mapFragment == null) {
            mapFragment = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.mapView));
            // Check if we were successful in obtaining the map.
            if (mapFragment != null) {
                // The Map is verified. It is now safe to manipulate the map.
                map = mapFragment.getMap();
                addUsersToMap(attendees);
            }
        }
    }

    private void addUsersToMap(List<Attendee> attendees) {
        if (map != null) {
            for (Attendee attendee : attendees) {
                //if (attendee.getLatitude() != 0 || attendee.getLongitude() != 0) {
                    Marker marker = map.addMarker(new MarkerOptions()
                            .position(new LatLng(attendee.getLatitude(),
                                    attendee.getLongitude()))
                            .title(attendee.getUsername()));

                    markers.add(marker);
             //   }
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
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50, 50, 0);
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

    private ParseGeoPoint geoPointFromLocation(Location loc) {
        return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        if (lastLocation != null
                && geoPointFromLocation(location)
                .distanceInKilometersTo(geoPointFromLocation(lastLocation)) < 0.001) {
            return;
        }
        lastLocation = location;

        updateUserLocation(geoPointFromLocation(location));
        updateUserMarker(location);
        publishUserLocationChange();
    }

    public void updateUserMarker(Location location) {
        for (Marker marker : markers) {
            if (marker.getTitle().equals(ParseUser.getCurrentUser().getUsername())) {
                marker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                break;
            }
        }
    }

    public void updateUserLocation(final ParseGeoPoint location) {
        if (currentEventUserObjId != null) {
            ParseQuery query = ParseQuery.getQuery(EventUser.class);

            // Retrieve the object by id
            query.getInBackground(currentEventUserObjId, new GetCallback<EventUser>() {
                public void done(EventUser eventUser, ParseException e) {
                    if (e == null) {
                        eventUser.put("location", location);
                        eventUser.saveInBackground();
                    }
                }
            });
        }
    }

    public void publishUserLocationChange() {
        Callback callback = new Callback() {
            public void successCallback(String channel, Object response) {
                Log.d("PUBNUB",response.toString());
            }
            public void errorCallback(String channel, PubnubError error) {
                Log.d("PUBNUB",error.toString());
            }
        };

        try {
            JSONObject dataToPublish = new JSONObject();
            dataToPublish.put("username", ParseUser.getCurrentUser().getUsername());
            dataToPublish.put("lat", currentLocation.getLatitude());
            dataToPublish.put("long", currentLocation.getLongitude());
            pubnub.publish(eventId, dataToPublish, callback);
        } catch (JSONException jsonException) {

        }
    }

//    public void updateOtherUserMarker(String username, double latitude, double longitude) {
//        for (Marker marker : markers) {
//            if (marker.getTitle().equals(username)) {
//                marker.setPosition(new LatLng(latitude, longitude));
//                break;
//            }
//        }
//        updateCameraView();
//    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnected(Bundle bundle) {
        currentLocation = getLocation();
        startPeriodicUpdates();
    }

    private void startPeriodicUpdates() {
      locationClient.requestLocationUpdates(locationRequest, this);
    }

    private void stopPeriodicUpdates() {
      locationClient.removeLocationUpdates(this);
    }

    private Location getLocation() {
        if (servicesConnected()) {
            return locationClient.getLastLocation();
        } else {
            return null;
        }
    }

    private boolean servicesConnected() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());

        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        } else {
            return false;
        }
    }

    public void subscribeToChannel () {
        pubnub = new Pubnub(PUBLISH_KEY, SUBSCRIBE_KEY);
        try {
            pubnub.subscribe(eventId, new Callback() {
                @Override
                public void connectCallback(String channel, Object message) {
                    Log.d("PUBNUB", "SUBSCRIBE : CONNECT on channel:" + channel
                            + " : " + message.getClass() + " : "
                            + message.toString());
                }

                @Override
                public void disconnectCallback(String channel, Object message) {
                    Log.d("PUBNUB","SUBSCRIBE : DISCONNECT on channel:" + channel
                            + " : " + message.getClass() + " : "
                            + message.toString());
                }

                public void reconnectCallback(String channel, Object message) {
                    Log.d("PUBNUB","SUBSCRIBE : RECONNECT on channel:" + channel
                            + " : " + message.getClass() + " : "
                            + message.toString());
                }

                @Override
                public void successCallback(String channel, Object message) {
                    Log.d("PUBNUB","SUBSCRIBE : " + channel + " : "
                            + message.getClass() + " : " + message.toString());
                    try {
                        if (message instanceof JSONObject) {
                            JSONObject data = (JSONObject) message;
                            final String username = data.getString("username");
                            final double latitude = data.getLong("lat");
                            final double longitude = data.getLong("long");
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    for (Marker marker : markers) {
                                        if (marker.getTitle().equals(username)) {
                                            marker.setPosition(new LatLng(latitude, longitude));
                                            break;
                                        }
                                    }
                                    updateCameraView();
                                }

                            });


                        }
                    } catch (JSONException e) {

                    }
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    Log.d("PUBNUB","SUBSCRIBE : ERROR on channel " + channel
                            + " : " + error.toString());
                }
            }
            );
        } catch (PubnubException e) {
            Log.d("PUBNUB",e.toString());
        }
    }
}