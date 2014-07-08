package com.sms.partyview.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.sms.partyview.R;
import com.sms.partyview.models.EventUser;

import java.util.List;

/**
 * Created by sandra on 7/7/14.
 */
public class AttendeeArrayAdapter extends ArrayAdapter<EventUser> {
    public AttendeeArrayAdapter(Context context, List<EventUser> userList) {
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
        final EventUser eventUser = getItem(position);
        eventUser.getUser().fetchInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseObject, ParseException e) {
                tvAttendeeName.setText(eventUser.getUser().getUsername());
            }
        });
        tvAttendeeStatus.setText("<" + eventUser.getStatus().toString() + ">");

       // tvAttendeeName.setText(eventUser.getUser().getUsername());
        return view;
    }
}
