package com.sms.partyview.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sms.partyview.R;
import com.sms.partyview.models.Attendee;
import com.sms.partyview.models.ChatMessage;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.List;

/**
 * Created by sandra on 7/7/14.
 */
public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {
    public ChatMessageAdapter(Context context, List<ChatMessage> messages) {
        super(context, 0, messages);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Find or inflate the template.
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext())
                    .inflate(R.layout.chat_message_item, parent, false);
        } else {
            view = convertView;
        }
        // Find views within template.
        TextView tvUsername =
                (TextView) view.findViewById(R.id.tvUsernameChat);
        TextView tvMessage =
                (TextView) view.findViewById(R.id.tvMessage);
        TextView tvDate =
                (TextView) view.findViewById(R.id.tvTimestamp);

        // Set view content.
        ChatMessage message = getItem(position);
        tvUsername.setText(message.getUsername());
        tvMessage.setText(message.getMessage());
        tvDate.setText(message.getDateSent());
        return view;
    }
}
