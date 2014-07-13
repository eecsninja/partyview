package com.sms.partyview.fragments;

import com.sms.partyview.models.Event;

import org.joda.time.DateTime;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sque on 7/12/14.
 */
public class UpdateEventFragment extends EditEventFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mBtnSubmit.setText("Update Event");
        return view;
    }

    // Loads an event for editing. Fills the fields with the event parameters.
    public void loadEvent(Event event) {
        mEtTitle.setText(event.getTitle());
        mEtAddress.setText(event.getAddress());
        mEtDescription.setText(event.getDescription());

        DateTime startDate = new DateTime(event.getStartDate());
        mTvStartDate.setText(DISPLAY_DATE_FORMATTER.print(startDate));
        mTvStartTime.setText(DISPLAY_TIME_FORMATTER.print(startDate));

        DateTime endDate = new DateTime(event.getEndDate());
        mTvEndDate.setText(DISPLAY_DATE_FORMATTER.print(endDate));
        mTvEndTime.setText(DISPLAY_TIME_FORMATTER.print(endDate));
    }
}
