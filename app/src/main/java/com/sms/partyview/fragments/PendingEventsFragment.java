package com.sms.partyview.fragments;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.sms.partyview.R;
import com.sms.partyview.activities.InviteActivity;
import com.sms.partyview.helpers.Utils;
import com.sms.partyview.models.Event;
import com.sms.partyview.models.EventUser;
import com.sms.partyview.models.LocalEvent;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by myho on 7/3/14.
 */
public class PendingEventsFragment extends EventListFragment {

    private static final String TAG = PendingEventsFragment.class.getSimpleName() + "_DEBUG";

    public static PendingEventsFragment newInstance() {
        PendingEventsFragment fragment = new PendingEventsFragment();
        return fragment;
    }

    @Override
    protected ParseQuery<EventUser> getQueryForEvents() {
        return EventUser.getQueryForPendingEvents();
    }

    @Override
    protected void displayMessage() {
        mTvMessage.setText(getString(R.string.msg_no_invitations));
    }

    @Override
    protected void setUpDisplayDetailedView() {
        eventsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                Log.d(TAG, "starting detail view");
                Event event = events.get(position);
                Intent intent = new Intent(getActivity(), InviteActivity.class);
                intent.putExtra(InviteActivity.EVENT_INTENT_KEY, new LocalEvent(event));
                getActivity()
                        .startActivityForResult(intent, Utils.RESPOND_TO_INVITE_EVENT_REQUEST_CODE);
            }
        });
    }

    public void removeEventFromList(LocalEvent event) {
        ParseQuery<Event> query = Event.getQueryForEventWithId(event.getObjectId());

        query.getFirstInBackground(new GetCallback<Event>() {
            @Override
            public void done(Event event, ParseException e) {
                if (e == null) {
                    eventAdapter.remove(event);

                    if (events.isEmpty()) {
                        displayNoItemMessage();
                    }

                    Log.d("DEBUG", "back to main");
                    Log.d("DEBUG", event.getTitle().toString());
                } else {
                    System.err.println("PendingEventsFragment.removeEventFromList: " +
                            e.getMessage());
                }
            }
        });
    }
}
