package com.sms.partyview.fragments;

import com.sms.partyview.adapters.AttendeeArrayAdapter;
import com.sms.partyview.models.Attendee;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;

import eu.inmite.android.lib.dialogs.BaseDialogFragment;

public class AttendeeListDialogFragment extends BaseDialogFragment {

    public static String TAG = "list";
    private static String ARG_TITLE = "title";
    private static String ARG_ATTENDEES = "attendees";
    protected ArrayList<Attendee> attendees;

    protected AttendeeArrayAdapter attendeeArrayAdapter;

    public static void show(FragmentActivity activity, String title, ArrayList<Attendee> attendees) {
        AttendeeListDialogFragment dialog = new AttendeeListDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putParcelableArrayList(ARG_ATTENDEES, attendees);
        dialog.setArguments(args);
        dialog.show(activity.getSupportFragmentManager(), TAG);
    }


    @Override
    public Builder build(Builder builder) {
        builder.setTitle(getTitle());

        attendees = getArguments().getParcelableArrayList(ARG_ATTENDEES);
        attendeeArrayAdapter = new AttendeeArrayAdapter(getActivity(), attendees);
        builder.setItems(attendeeArrayAdapter, 0, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        builder.setPositiveButton("Close", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return builder;
    }

    private String getTitle() {
        return getArguments().getString(ARG_TITLE);
    }
}