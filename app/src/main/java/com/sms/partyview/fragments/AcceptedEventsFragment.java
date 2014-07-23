package com.sms.partyview.fragments;

import com.parse.ParseQuery;
import com.sms.partyview.models.EventUser;
import com.sms.partyview.R;

/**
 * Created by myho on 7/3/14.
 */
public class AcceptedEventsFragment extends EventListFragment {

    private static final String TAG = AcceptedEventsFragment.class.getSimpleName() + "_DEBUG";

    public static AcceptedEventsFragment newInstance() {
        AcceptedEventsFragment fragment = new AcceptedEventsFragment();
        return fragment;
    }

    @Override
    protected ParseQuery<EventUser> getQueryForEvents() {
        return EventUser.getQueryForAcceptedEvents();
    }

    @Override
    protected void displayMessage() {
        mTvMessage.setText(getString(R.string.msg_no_events));
    }

    @Override
    protected void notifyDataChanged() {
        mEventListDataChangeListener.onEventListUpdate(0, events.size());
    }
}
