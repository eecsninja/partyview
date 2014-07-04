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
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class EventListFragment extends Fragment {
    protected ArrayList<Event> events;
    protected HomeScreenEventAdapter eventAdapter;
    protected ListView eventsView;

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
        /* Auto-generated code. Uncomment to add Activity as a listener.
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }
        */
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
            }
        });

        // Return it.
        return view;
    }

    /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated
    * to the activity and potentially other fragments contained in that
    * activity.
    * <p>
    * See the Android Training lesson <a href=
    * "http://developer.android.com/training/basics/fragments/communicating.html"
    * >Communicating with Other Fragments</a> for more information.
    */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

    // Add new events to list.
    public void addEvents(ArrayList<Event> events) {
        eventAdapter.addAll(events);
    }
}
