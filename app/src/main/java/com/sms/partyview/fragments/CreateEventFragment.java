package com.sms.partyview.fragments;

import com.sms.partyview.models.Event;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sque on 7/12/14.
 */
public class CreateEventFragment extends EditEventFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mBtnSubmit.setText("Create Event");
        return view;
    }

    @Override
    public Event getEventFromInputData() {
        // Create a new event object.
        Event event = new Event();
        readEventInfoFromInputFields(event);
        return event;
    }
}
