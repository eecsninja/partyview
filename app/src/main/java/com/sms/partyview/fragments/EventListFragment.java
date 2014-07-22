package com.sms.partyview.fragments;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sms.partyview.R;
import com.sms.partyview.activities.EventActivity;
import com.sms.partyview.adapters.EventAdapter;
import com.sms.partyview.models.Event;
import com.sms.partyview.models.EventUser;
import com.sms.partyview.models.LocalEvent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * A fragment representing a list of events.
 */
public abstract class EventListFragment extends Fragment {

    private static final String TAG = EventListFragment.class.getSimpleName() + "_DEBUG";
    protected List<Event> events;

    protected EventAdapter eventAdapter;

    protected ListView eventsView;

    protected ArrayList<Event> preOnCreateEvents = new ArrayList<Event>();

    protected Map<String, String> statusMap = new HashMap<String, String>();

    public static final int EVENT_DETAIL_REQUEST = 123;

    private PullToRefreshLayout mPullToRefreshLayout;

    protected LinearLayout mLlMessage;
    protected TextView mTvMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize event list and adapter.
        events = new ArrayList<Event>();
        eventAdapter = new EventAdapter(getActivity(), events);
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
                R.layout.fragment_event_list, container, false);

        mLlMessage = (LinearLayout) view.findViewById(R.id.llMessage);
        mTvMessage = (TextView) view.findViewById(R.id.tvMessage);

        // Set up to display tweets.
        eventsView = (ListView) view.findViewById(R.id.lvHomeEventList);
        eventsView.setAdapter(eventAdapter);

        // TODO: This causes the pull down refresh UI to appear over the
        // tabs, not over this list fragment. Should fix that somehow.
        mPullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.refreshLayoutEventList);
        // Now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(getActivity())
                // Mark All Children as pullable
                .allChildrenArePullable()
                // Set a OnRefreshListener
                .listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(View view) {
                        // Get the updated event list.
                        // TODO: Refresh events for both lists.
                        populateEventList();
                    }
                })
                // Finally commit the setup to our PullToRefreshLayout
                .setup(mPullToRefreshLayout);

        populateEventList();

        // Add item click listener.

        setUpDisplayDetailedView();

        // Return it.
        return view;
    }

    // Determines whether the given event ID is present in the events list.
    public boolean containsEventWithId(String eventId) {
        for (Event event : events) {
            if (event.getObjectId().equals(eventId)) {
                return true;
            }
        }
        return false;
    }

    protected void setUpDisplayDetailedView() {
        eventsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                Event event = events.get(position);

                Log.d("DEBUG", "calling detailed view");
                Log.d("DEBUG", event.getTitle().toString());
                Log.d("DEBUG", "calling act: " + getActivity().toString());
                Intent intent = new Intent(getActivity(), EventActivity.class);
                intent.putExtra(EventActivity.EVENT_INTENT_KEY, new LocalEvent(event));

                String status = statusMap.get(event.getObjectId());
                if (status != null) {
                    intent.putExtra(EventActivity.EVENT_STATUS_KEY, status);
                } else {
                    throw new IllegalArgumentException(
                            "Could not find attendance status for event " + event.getTitle() +
                            " with ID " + event.getObjectId());
                }
                getActivity().startActivityForResult(intent, EVENT_DETAIL_REQUEST);
            }
        });
    }

    public void updateEvent(LocalEvent event, final String attendanceStatus) {
        // Find the event in the event array, based on object ID.
        int index = -1;
        for (int i = 0; i < events.size(); ++i) {
            if (events.get(i).getObjectId().equals(event.getObjectId())) {
                index = i;
                break;
            }
        }
        // If no event was found, add it as a new event.
        if (index == -1) {
            addNewEventToList(event, attendanceStatus);
            return;
        }
        updateExistingEvent(index, event);
    }

    public void removeEventFromList(LocalEvent event) {
        ParseQuery<Event> query = Event.getQueryForEventWithId(event.getObjectId());

        query.getFirstInBackground(new GetCallback<Event>() {
            @Override
            public void done(Event event, ParseException e) {
                if (e == null) {
                    eventAdapter.remove(event);
                    if (events.isEmpty()) {
                        displayNoItemMessage();
                    }
                    Log.d(TAG, "back to main");
                    Log.d(TAG, event.getTitle().toString());
                } else {
                    System.err.println("EventListFragment.removeEventFromList: " +
                            e.getMessage());
                }
            }
        });
    }

    protected void updateExistingEvent(int index, LocalEvent event) {
        // Make sure index is valid.
        if (index >= events.size()) {
            throw new ArrayIndexOutOfBoundsException("Event list index is too large: " + index);
        }
        // Make sure the new event is an updated version of the current event
        if (!events.get(index).getObjectId().equals(event.getObjectId())) {
            System.err.println("Updated event does not match original event!");
            return;
        }
        eventAdapter.update(index, event);
    }

    protected void addNewEventToList(LocalEvent event, final String attendanceStatus) {
        mLlMessage.setVisibility(GONE);
        statusMap.put(event.getObjectId(), attendanceStatus);

        // Define the class we would like to query
        ParseQuery<Event> query = Event.getQueryForEventWithId(event.getObjectId());

        query.getFirstInBackground(new GetCallback<Event>() {
            @Override
            public void done(Event event, ParseException e) {
                if (e == null) {
                    eventAdapter.add(event);
                    Log.d(TAG, "back to main");
                    Log.d(TAG, event.getTitle().toString());
                } else {
                    System.err.println(
                            "EventsListFragment.addNewEventToList: " + e.getMessage());
                }
            }
        });

    }

    protected void populateEventList() {
        // Query for new results from the network.
        ParseQuery<EventUser> query = getQueryForEvents();

        query.findInBackground(
                new FindCallback<EventUser>() {
                    @Override
                    public void done(List<EventUser> eventUsers, ParseException e) {
                        if (e == null) {

                            if(eventUsers.isEmpty()) {
                                displayNoItemMessage();
                                return;
                            }

                            mLlMessage.setVisibility(GONE);
                            List<Event> events = new ArrayList<Event>();
                            for (EventUser eventUser : eventUsers) {
                                events.add(eventUser.getEvent());
                                statusMap.put(
                                        eventUser.getEvent().getObjectId(),
                                        eventUser.getStatus().toString());
                            }
                            eventAdapter.clear();
                            eventAdapter.addAll(events);
                            // This may be called from a pull down refresh.
                            mPullToRefreshLayout.setRefreshComplete();
                        } else {
                            System.err.println("EventListFragment.populateEventList(): " +
                                               e.getMessage());
                        }
                    }
                }
        );
    }

    protected void displayNoItemMessage() {
        eventAdapter.clear();
        mLlMessage.setVisibility(VISIBLE);
        displayMessage();
        mPullToRefreshLayout.setRefreshComplete();
    }

    // Returns a parse query for the events shown in this list.
    protected abstract ParseQuery<EventUser> getQueryForEvents();

    protected abstract void displayMessage();
}
