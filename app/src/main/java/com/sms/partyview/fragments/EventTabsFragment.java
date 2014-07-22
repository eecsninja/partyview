package com.sms.partyview.fragments;

import com.astuetz.PagerSlidingTabStrip;
import com.sms.partyview.R;
import com.sms.partyview.activities.EditEventActivity;
import com.sms.partyview.activities.InviteActivity;
import com.sms.partyview.adapters.MyPagerAdapter;
import com.sms.partyview.models.LocalEvent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
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

    // TODO: How to make these an enum?
    private static final int ACCEPTED_EVENTS_TAB = 0;
    private static final int PENDING_EVENTS_TAB = 1;

    private FragmentStatePagerAdapter mAdapterViewPager;
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
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_tabs, container, false);
        setupTabs(view);

        return view;
    }

    public void respondToEventCreation(Intent data) {
        LocalEvent event =
                (LocalEvent) data.getSerializableExtra(EditEventActivity.SAVED_EVENT_KEY);

        EventListFragment fragment = getEventList();
        fragment.addNewEventToList(event, ACCEPTED.toString());

        displayEventTab();
    }

    public void respondToInvite(Intent data) {
        LocalEvent event = (LocalEvent) data.getSerializableExtra(InviteActivity.EVENT_INTENT_KEY);
        String response = data.getStringExtra(InviteActivity.INVITE_RESPONSE_KEY);

        // remove from pending events
        getInviteList().removeEventFromList(event);
        mAdapterViewPager.notifyDataSetChanged();

        if (response.equalsIgnoreCase(ACCEPTED.toString())) {
            getEventList().addNewEventToList(event, response);

            mAdapterViewPager.notifyDataSetChanged();

            displayEventTab();
        } else {
            displayInviteTab();
        }
    }

    public void respondToEventEdit(Intent data) {
        LocalEvent event =
                (LocalEvent) data.getSerializableExtra(
                        AcceptedEventDetailFragment.UDPATED_EVENT_INTENT_KEY);
        if (event == null) {
            return;
        }
        // Replace the existing event if it was updated.
        EventListFragment fragment =
                (EventListFragment) mAdapterViewPager.getItem(mViewPager.getCurrentItem());
        fragment.updateEvent(event, null);
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

    private static List<Fragment> getFragments() {
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

    public EventListFragment getEventList() {
        return (EventListFragment) mAdapterViewPager.getItem(ACCEPTED_EVENTS_TAB);
    }

    public EventListFragment getInviteList() {
        return (EventListFragment) mAdapterViewPager.getItem(PENDING_EVENTS_TAB);
    }

    public void displayEventTab() {
        mViewPager.setCurrentItem(ACCEPTED_EVENTS_TAB);
    }

    public void displayInviteTab() {
        mViewPager.setCurrentItem(PENDING_EVENTS_TAB);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
}
