package com.sms.partyview.adapters;

import com.sms.partyview.R;
import com.sms.partyview.models.Event;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sque on 7/4/14.
 */
public class HomeScreenEventAdapter extends ArrayAdapter<Event> {

    public HomeScreenEventAdapter(Context context, List<Event> event_list) {
        super(context, 0, event_list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Find or inflate the template.
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext())
                    .inflate(R.layout.home_event_item, parent, false);
        } else {
            view = convertView;
        }
        // Find views within template.
        TextView titleField =
                (TextView) view.findViewById(R.id.tvEventItemTitle);
        TextView timeField =
                (TextView) view.findViewById(R.id.tvEventItemTime);
        TextView hostNameField =
                (TextView) view.findViewById(R.id.tvEventItemHost);
        // Set view content.
        Event event = getItem(position);
        titleField.setText(event.getTitle());
        timeField.setText(event.getTime());
        // TODO: should show a display name.
        hostNameField.setText("Hosted by " + event.getHost().getUserName());

        return view;
    }
}
