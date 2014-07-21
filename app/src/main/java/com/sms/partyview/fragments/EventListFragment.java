package com.sms.partyview.fragments;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
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
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * A fragment representing a list of events.
 */
public abstract class EventListFragment extends Fragment {

    protected List<Event> events;

    protected EventAdapter eventAdapter;

    protected ListView eventsView;

    protected ArrayList<Event> preOnCreateEvents = new ArrayList<Event>();

    protected Map<String, String> statusMap = new HashMap<String, String>();

    public static final int EVENT_DETAIL_REQUEST = 123;

    private PullToRefreshLayout mPullToRefreshLayout;

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
                intent.putExtra(EventActivity.EVENT_LIST_INDEX_KEY, position);

                if (statusMap.get(event.getObjectId()) != null) {
                    intent.putExtra("eventStatus", statusMap.get(event.getObjectId()));
                }
                getActivity().startActivityForResult(intent, EVENT_DETAIL_REQUEST);
            }
        });
    }

    public void updateEvent(int index, LocalEvent event) {
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


    protected void populateEventList() {
        // Query for new results from the network.
        ParseQuery<EventUser> query = getQueryForEvents();

        query.findInBackground(
                new FindCallback<EventUser>() {
                    @Override
                    public void done(List<EventUser> eventUsers, ParseException e) {
                        if (e == null) {
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

    // Returns a parse query for the events shown in this list.
    protected abstract ParseQuery<EventUser> getQueryForEvents();
}
