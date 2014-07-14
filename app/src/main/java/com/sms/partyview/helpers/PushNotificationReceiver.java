package com.sms.partyview.helpers;

import com.parse.ParseUser;
import com.sms.partyview.R;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

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
        String eventId = null;
        String eventHostName = null;
        String eventTitle = null;
        boolean isNewEvent = false;
        try {
            json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            eventId = json.getString("eventId");
            eventHostName = json.getString("eventHostName");
            eventTitle = json.getString("eventTitle");
            isNewEvent = json.getBoolean("isNewEvent");
        } catch (JSONException e) {
            System.err.println(e.getMessage());
        }

        // Build a local notification.
        String notificationMsg =
                eventHostName +
                (isNewEvent ? " has invited you to " : " has updated ") +
                "the event " +
                eventTitle;
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Event notification")
                        .setContentText(notificationMsg);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}
