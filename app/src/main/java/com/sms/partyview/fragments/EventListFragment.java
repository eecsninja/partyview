package com.sms.partyview.fragments;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sms.partyview.R;
import com.sms.partyview.activities.EventDetailActivity;
import com.sms.partyview.adapters.HomeScreenEventAdapter;
import com.sms.partyview.models.Event;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of events.
 */
public abstract class EventListFragment extends Fragment {

    protected List<Event> events;

    protected HomeScreenEventAdapter eventAdapter;

    protected ListView eventsView;

    protected ArrayList<Event> preOnCreateEvents = new ArrayList<Event>();

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

//        eventsView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView absListView, int i) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
//                // Show some dummy events.
//                // TODO: Remove this test code.
//                if (events.isEmpty()) {
//                    populateListView();
//                }
//            }
//        });

        // Return it.
        return view;
    }

    private void setUpDisplayDetailedView() {
        eventsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                Event event = events.get(position);

                Log.d("DEBUG", "calling detailed view");
                Log.d("DEBUG", event.getTitle().toString());
                Log.d("DEBUG", "calling act: " + getActivity().toString());
                Intent i = new Intent(getActivity(), EventDetailActivity.class);
                i.putExtra("event", event);
                startActivity(i);
            }
        });
    }

    protected abstract void populateEventList();
}
