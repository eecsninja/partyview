package com.sms.partyview.adapters;

import com.sms.partyview.R;
import com.sms.partyview.models.Event;
import com.sms.partyview.models.LocalEvent;

import org.joda.time.DateTime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static com.sms.partyview.helpers.Utils.DISPLAY_MONTH_FORMATTER;
import static com.sms.partyview.helpers.Utils.DISPLAY_DATE_TIME_FORMATTER;
import static com.sms.partyview.helpers.Utils.DISPLAY_DAY_FORMATTER;

/**
 * Created by sque on 7/4/14.
 */
public class EventAdapter extends ArrayAdapter<Event> {

    public EventAdapter(Context context, List<Event> event_list) {
        super(context, 0, event_list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Find or inflate the template.
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext())
                    .inflate(R.layout.event_item, parent, false);
        } else {
            view = convertView;
        }
        // Find views within template.
        TextView titleField =
                (TextView) view.findViewById(R.id.tvEventItemTitle);
//        TextView timeField =
//                (TextView) view.findViewById(R.id.tvEventItemTime);
        TextView locationField =
                (TextView) view.findViewById(R.id.tvEventItemLocation);
        TextView hostNameField =
                (TextView) view.findViewById(R.id.tvEventItemHost);
        // Set view content.
        Event event = getItem(position);
        titleField.setText(event.getTitle());

        TextView monthField = (TextView) view.findViewById(R.id.tvMonth);
        TextView dayField = (TextView) view.findViewById(R.id.tvDay);

        DateTime date = new DateTime(event.getStartDate());
        //timeField.setText(DISPLAY_DATE_TIME_FORMATTER.print(date));
        // TODO: should show a display name.

        monthField.setText(DISPLAY_MONTH_FORMATTER.print(date));
        dayField.setText(DISPLAY_DAY_FORMATTER.print(date));

        hostNameField.setText("Hosted by: " + event.getHost().getUsername());

        // Date/time should be of a lighter color.
      //  timeField.setTextColor(getContext().getResources().getColor(R.color.gray));

        locationField.setText(event.getAddress());
        locationField.setTextColor(getContext().getResources().getColor(R.color.gray));

        LinearLayout llDate = (LinearLayout) view.findViewById(R.id.llDate);
        if (position % 3 == 0) {
            llDate.setBackgroundResource(R.drawable.purple_circle);
        } else if (position % 2 == 0) {
            llDate.setBackgroundResource(R.drawable.blue_circle);
        } else {
            llDate.setBackgroundResource(R.drawable.coral_circle);
        }

        return view;
    }

    @Override
    public void add(Event object) {
        super.add(object);
        sortByDate();
    }

    @Override
    public void addAll(Collection<? extends Event> collection) {
        super.addAll(collection);
        sortByDate();
        notifyDataSetChanged();
    }

    public void update(int index, LocalEvent event) {
        super.getItem(index).update(event);
        sortByDate();
        notifyDataSetChanged();
    }

    protected void sortByDate() {
        // TODO: This might become deprecated if a local database is implemented.
        sort(new Comparator<Event>() {
            @Override
            public int compare(Event e1, Event e2) {
                return e1.getStartDate().compareTo(e2.getStartDate());
            }
        });
    }
}
