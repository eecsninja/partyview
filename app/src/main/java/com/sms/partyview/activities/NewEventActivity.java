package com.sms.partyview.activities;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sms.partyview.models.AttendanceStatus;
import com.sms.partyview.R;
import com.sms.partyview.fragments.CreateEventFragment;
import com.sms.partyview.fragments.EditEventFragment;
import com.sms.partyview.helpers.EventSaverInterface;
import com.sms.partyview.helpers.GetGeoPointTask;
import com.sms.partyview.models.Event;
import com.sms.partyview.models.EventUser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import java.util.List;

public class NewEventActivity extends FragmentActivity implements EventSaverInterface {

    private static final String TAG = NewEventActivity.class.getSimpleName() + "_DEBUG";
    protected EditEventFragment mEditEventFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(NewEventActivity.class.getSimpleName() + "_DEBUG", "create activity");

        super.onCreate(savedInstanceState);

        // MUST request the feature before setting content view
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_new_event);

        // Display the edit event fragment.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        mEditEventFragment = createFragment();
        ft.replace(R.id.flNewEventContainer, mEditEventFragment);
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void saveNewEvent(final Event event, final String invitesString) {
        showProgressBar();
        new GetGeoPointTask(this) {
            @Override
            protected void onPostExecute(ParseGeoPoint parseGeoPoint) {
                // network error
                if (parseGeoPoint == null) {
                    hideProgressBar();
                    Log.d("DEBUG", "geoPoint is null");
                    showToast(getString(R.string.error_network_error_retrieving_location));

                    // TODO: set focus on address field
                    return;
                } else if (parseGeoPoint.getLatitude() == 0.0 && parseGeoPoint.getLongitude() == 0.0) {
                    hideProgressBar();
                    Log.d(TAG, "No address found");
                    showToast(getString(R.string.error_invalid_location));

                    // TODO: set focus on address field
                    return;
                } else {
                    event.setLocation(parseGeoPoint);
                }

                //TODO: discuss with team on best way to save:
                //      saveInBackground
                //      saveEventually: offline protection

                event.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        hideProgressBar();
                        if (e == null) {
                            List<ParseUser> attendeeList =
                                    mEditEventFragment.getAttendeeList(invitesString);
                            generateEventUsers(attendeeList, event);
                            notifyInvitees(attendeeList, event);

                            finishWithEvent(event);
                        } else {
                            Log.d("DEBUG", "exception creating event");
                            Log.d("DEBUG", e.toString());
                        }
                    }
                });
            }

        }.execute(event.getAddress());
    }

    public static void generateEventUsers(List<ParseUser> attendeeList, Event event) {
        // create an EventUser object for host and everyone in invites
        for (ParseUser user : attendeeList) {
            createUserEventIfNoneExists(AttendanceStatus.INVITED, user, event);
        }

        // host's invitation status should default to accepted
        createUserEventIfNoneExists(AttendanceStatus.ACCEPTED, ParseUser.getCurrentUser(), event);
    }

    // Creates an EventUser object if there's no existing object with the user-event pair.
    protected static void createUserEventIfNoneExists(
            final AttendanceStatus status, final ParseUser user, final Event event) {
        // Search for EventUsers with the given user and event.
        ParseQuery userQuery = ParseQuery.getQuery(ParseUser.class);
        userQuery.whereEqualTo("objectId", user.getObjectId());
        ParseQuery eventQuery = ParseQuery.getQuery(Event.class);
        eventQuery.whereEqualTo("objectId", event.getObjectId());

        ParseQuery<EventUser> query = new ParseQuery(EventUser.class);
        query.whereMatchesQuery("user", userQuery);
        query.whereMatchesQuery("event", eventQuery);
        query.findInBackground(new FindCallback<EventUser>() {
            @Override
            public void done(List<EventUser> eventUsers, ParseException e) {
                if (e != null) {
                    System.err.println(e.getMessage());
                    return;
                }
                if (!eventUsers.isEmpty()) {
                    return;
                }
                // If there was no EventUser found, create one.
                new EventUser(status, new ParseGeoPoint(), user, event).saveInBackground();
            }
        });
    }

    // Create fragment for editing a new event.
    protected EditEventFragment createFragment() {
        return new CreateEventFragment();
    }

    // Finish this activity, returning the relevant event-related data
    // as part of an intent.
    protected void finishWithEvent(Event event) {
        Intent data = new Intent();
        data.putExtra("eventId", event.getObjectId());
        setResult(RESULT_OK, data);

        Log.d("DEBUG", "saved event");
        Log.d("DEBUG", event.getObjectId().toString());
        finish();
    }

    // Should be called manually when an async task has started
    private void showProgressBar() {
        this.setProgressBarIndeterminateVisibility(true);
    }

    // Should be called when an async task has finished
    private void hideProgressBar() {
        this.setProgressBarIndeterminateVisibility(false);
    }

    protected void notifyInvitees(List<ParseUser> invitees, Event event) {
        // TODO: Is there a way to send as a single query?
        for (ParseUser invitee : invitees) {
            ParseQuery query = ParseInstallation.getQuery();
            query.whereEqualTo(HomeActivity.INSTALLATION_USER_NAME_KEY,
                               invitee.getUsername());

            ParsePush push = new ParsePush();
            push.setQuery(query);
            push.setMessage(getNotificationMessage(event));
            push.sendInBackground();
        }
    }

    // Constructs a notification message for the given event.
    protected String getNotificationMessage(Event event) {
        return event.getHost().getUsername() + " has invited you to " + event.getTitle();
    }

    private void showToast(String toastText) {
        Toast.makeText(
                this, toastText, Toast.LENGTH_SHORT).show();
    }

}
