package com.sms.partyview.helpers;

import com.sms.partyview.R;
import com.sms.partyview.activities.InvitedEventDetailFragment;
import com.sms.partyview.models.LocalEvent;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by sque on 7/13/14.
 */
public class PushNotificationReceiver extends BroadcastReceiver {
    public static final String EVENT_UPDATE_NOTIFICATION_ACTION =
            "com.sms.partyview.EVENT_NOTIFICATION";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            System.err.println("No intent found.");
            return;
        }
        if (!intent.getAction().equals(EVENT_UPDATE_NOTIFICATION_ACTION)) {
            System.err.println("Unable to handle action " + intent.getAction());
            return;
        }
        JSONObject json = null;
        LocalEvent event = null;
        boolean isNewEvent = false;
        try {
            json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            event = new LocalEvent(json.getJSONObject("event"));
            isNewEvent = json.getBoolean("isNewEvent");
        } catch (JSONException e) {
            System.err.println(e.getMessage());
        }
        String eventId = event.getObjectId();
        String eventHostName = event.getHost();
        String eventTitle = event.getTitle();

        // Create an intent to launch an activity.
        PendingIntent pendingIntent = createPendingIntent(context, event);

        // Build a local notification and attach intent to it.
        String notificationMsg =
                eventHostName +
                (isNewEvent ? " has invited you to " : " has updated ") +
                "the event " +
                eventTitle;
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(isNewEvent ? "New event invite" : "Event update")
                        .setContentText(notificationMsg)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

    // Builds an intent to launch an activity to view the event.
    protected PendingIntent createPendingIntent(Context context, LocalEvent event) {
        int requestId = (int) System.currentTimeMillis();
        int flags = PendingIntent.FLAG_CANCEL_CURRENT;
        // TODO: InviteDetailActivity doesn't make sense if it is already accepted.
        // Find a way to properly handle that case.
        Intent intent = new Intent(context, InvitedEventDetailFragment.class);
        intent.putExtra(InvitedEventDetailFragment.EVENT_INTENT_KEY, event);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestId, intent, flags);
        return pendingIntent;
    }
}
