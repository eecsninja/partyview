package com.sms.partyview.fragments;

import com.sms.partyview.R;
import com.sms.partyview.adapters.HomeScreenEventAdapter;
import com.sms.partyview.models.Event;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A fragment representing a list of events.
 */
public class EventListFragment extends Fragment {
    protected ArrayList<Event> events;
    protected HomeScreenEventAdapter eventAdapter;
    protected ListView eventsView;

    protected ArrayList<Event> preOnCreateEvents = new ArrayList<Event>();

    protected DummyEventProvider dummyEventProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize event list and adapter.
        events = new ArrayList<Event>();
        eventAdapter = new HomeScreenEventAdapter(getActivity(), events);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            dummyEventProvider = (DummyEventProvider) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement DummyEventProvider");
        }
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

        // Add item click listener.
        eventsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                Event event = events.get(position);
                System.err.println("Event: " + event.getTitle());
                // TODO: pass it to listener.
                dummyEventProvider.onEventClick(event);
            }
        });
        eventsView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                // Show some dummy events.
                // TODO: Remove this test code.
                if (events.isEmpty()) {
                    addEvents(dummyEventProvider.getEvents());
                }
            }
        });

        // Return it.
        return view;
    }

    // For testing with dummy events.
    public interface DummyEventProvider {
        // Returns an array list of dummy events.
        public ArrayList<Event> getEvents();
        public void onEventClick(Event event);
    }

    // Add new events to list.
    public void addEvents(ArrayList<Event> events) {
        eventAdapter.addAll(events);
    }
}
