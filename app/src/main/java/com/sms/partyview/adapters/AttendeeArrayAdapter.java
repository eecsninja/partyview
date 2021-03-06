package com.sms.partyview.adapters;

import com.sms.partyview.R;
import com.sms.partyview.models.Attendee;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sandra on 7/7/14.
 */
public class AttendeeArrayAdapter extends ArrayAdapter<Attendee> {
    public AttendeeArrayAdapter(Context context, List<Attendee> userList) {
        super(context, 0, userList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Find or inflate the template.
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext())
                    .inflate(R.layout.attendee_item, parent, false);
        } else {
            view = convertView;
        }
        // Find views within template.
        final TextView tvAttendeeName =
                (TextView) view.findViewById(R.id.tvAttendeeName);
        TextView tvAttendeeStatus =
                (TextView) view.findViewById(R.id.tvAttendeeStatus);

        // Set view content.
        Attendee attendee = getItem(position);
        tvAttendeeName.setText(attendee.getUsername());
        tvAttendeeStatus.setText("(" + attendee.getStatus().toString().toLowerCase() + ")");

        LinearLayout statusIcon = (LinearLayout) view.findViewById(R.id.llStatusIcon);
        switch(attendee.getStatus()) {
            case ACCEPTED:
                statusIcon.setBackgroundResource(R.drawable.status_accepted_circle);
                break;
            case PRESENT:
                statusIcon.setBackgroundResource(R.drawable.status_present_circle);
                break;
            case DECLINED:
                statusIcon.setBackgroundResource(R.drawable.status_declined_circle);
                break;
            case INVITED:
                statusIcon.setBackgroundResource(R.drawable.status_invited_circle);
                break;
        }
        return view;
    }
}
