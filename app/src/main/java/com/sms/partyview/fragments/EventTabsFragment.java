package com.sms.partyview.fragments;

import com.astuetz.PagerSlidingTabStrip;
import com.sms.partyview.R;
import com.sms.partyview.activities.AcceptedEventDetailActivity;
import com.sms.partyview.activities.EditEventActivity;
import com.sms.partyview.adapters.MyPagerAdapter;
import com.sms.partyview.models.LocalEvent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import static com.sms.partyview.models.AttendanceStatus.ACCEPTED;

public class EventTabsFragment extends Fragment {

    private static final String TAG = EventTabsFragment.class.getSimpleName() + "_DEBUG";

    private FragmentPagerAdapter mAdapterViewPager;
    private PagerSlidingTabStrip mTabs;
    private ViewPager mViewPager;

    public EventTabsFragment() {
        // Required empty public constructor
    }

    public static EventTabsFragment newInstance() {
        EventTabsFragment fragment = new EventTabsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_tabs, container, false);
        setupTabs(view);

        return view;
    }

    public void respondToEventCreation(Intent data) {
        LocalEvent event =
                (LocalEvent) data.getSerializableExtra(EditEventActivity.SAVED_EVENT_KEY);

        AcceptedEventsFragment fragment = (AcceptedEventsFragment) mAdapterViewPager.getItem(0);
        fragment.addNewEventToList(event.getObjectId(), ACCEPTED.toString());

        displayEventTab();
    }

    public void respondToInvite(Intent data) {
        String eventId = data.getStringExtra("eventId");
        String response = data.getStringExtra("response");

        // remove from pending events
        PendingEventsFragment pendingFragment = (PendingEventsFragment) mAdapterViewPager
                .getItem(1);
        pendingFragment.removeEventFromList(eventId);
        mAdapterViewPager.notifyDataSetChanged();

        if (response.equalsIgnoreCase(ACCEPTED.toString())) {
            AcceptedEventsFragment fragment = (AcceptedEventsFragment) mAdapterViewPager
                    .getItem(0);
            fragment.addNewEventToList(eventId, response);

            mAdapterViewPager.notifyDataSetChanged();

            displayEventTab();
        } else {
            displayInviteTab();
        }
    }

    public void respondToEventEdit(Intent data) {
        LocalEvent event =
                (LocalEvent) data.getSerializableExtra(
                        AcceptedEventDetailActivity.UDPATED_EVENT_INTENT_KEY);
        Log.d("DEBUG", "returned local event: " + event);
        if (event == null) {
            return;
        }
        // Replace the existing event if it was updated.
        int index = data.getIntExtra(AcceptedEventDetailActivity.EVENT_LIST_INDEX_KEY, 0);
        EventListFragment fragment =
                (EventListFragment) mAdapterViewPager.getItem(mViewPager.getCurrentItem());
        fragment.updateEvent(index, event);
    }

    private void setupTabs(View view) {
        // Initialize the ViewPager and set an adapter
        mViewPager = (ViewPager) view.findViewById(R.id.vpPager);
        mAdapterViewPager = new MyPagerAdapter(getActivity().getSupportFragmentManager(),
                getFragments());
        mViewPager.setAdapter(mAdapterViewPager);
        mTabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        mTabs.setViewPager(mViewPager);
    }

    private List<Fragment> getFragments() {
        List<Fragment> fragments
                = new ArrayList<Fragment>();

        fragments.add(AcceptedEventsFragment.newInstance());
        fragments.add(PendingEventsFragment.newInstance());

        return fragments;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    public void displayEventTab() {
        mViewPager.setCurrentItem(0);
    }

    public void displayInviteTab() {
        mViewPager.setCurrentItem(1);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
}
