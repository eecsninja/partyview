package com.sms.partyview.fragments;

import android.app.Fragment;

/**
 * Created by myho on 7/3/14.
 */
public class PendingEventsFragment extends EventListFragment {

    public static PendingEventsFragment newInstance() {
        PendingEventsFragment fragment = new PendingEventsFragment();
        return fragment;
    }

    @Override
    protected void populateEventList() {
        // do nothing for now
    }
}
