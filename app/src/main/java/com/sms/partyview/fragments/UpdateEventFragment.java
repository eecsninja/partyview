package com.sms.partyview.fragments;

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
}
