package com.sms.partyview.fragments;

import com.sms.partyview.R;
import com.sms.partyview.activities.AcceptedEventDetailActivity;
import com.sms.partyview.adapters.EventAdapter;
import com.sms.partyview.models.Event;
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
                Intent intent = new Intent(getActivity(), AcceptedEventDetailActivity.class);
                intent.putExtra(AcceptedEventDetailActivity.EVENT_INTENT_KEY, new LocalEvent(event));
                intent.putExtra(AcceptedEventDetailActivity.EVENT_LIST_INDEX_KEY, position);

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

    protected abstract void populateEventList();
}
