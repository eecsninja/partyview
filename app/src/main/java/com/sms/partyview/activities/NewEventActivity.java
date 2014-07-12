package com.sms.partyview.activities;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sms.partyview.AttendanceStatus;
import com.sms.partyview.R;
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
import android.view.View;
import android.view.Window;

import java.util.List;

public class NewEventActivity extends FragmentActivity implements EventSaverInterface {
    private EditEventFragment mEditEventFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(getClass().getSimpleName() + "_DEBUG", "create activity");

        super.onCreate(savedInstanceState);

        // MUST request the feature before setting content view
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_new_event);

        // Display the edit event fragment.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        mEditEventFragment = new EditEventFragment();
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

    public void createEvent(View view) {
        mEditEventFragment.createEvent(view);
    }

    @Override
    public void saveNewEvent(final Event event, final String invitesString) {
        showProgressBar();
        new GetGeoPointTask(this) {
            @Override
            protected void onPostExecute(ParseGeoPoint parseGeoPoint) {
                // TODO: What is the proper behavior when this is null?
                // I'm creating an empty ParseGeoPoint object for now.
                if (parseGeoPoint == null) {
                    parseGeoPoint = new ParseGeoPoint();
                }
                event.setLocation(parseGeoPoint);

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
                            notifyInvitees(attendeeList);

                            Intent data = new Intent();
                            data.putExtra("eventId", event.getObjectId());
                            setResult(RESULT_OK, data);

                            Log.d("DEBUG", "saved event");
                            Log.d("DEBUG", event.getObjectId().toString());

                            finish();
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

            new EventUser(
                    AttendanceStatus.INVITED,
                    new ParseGeoPoint(),
                    user,
                    event
            ).saveInBackground();
        }

        // host's invitation status should default to accepted
        new EventUser(
                AttendanceStatus.ACCEPTED,
                new ParseGeoPoint(),
                ParseUser.getCurrentUser(),
                event
        ).saveInBackground();
    }

    // Should be called manually when an async task has started
    private void showProgressBar() {
        this.setProgressBarIndeterminateVisibility(true);
    }

    // Should be called when an async task has finished
    private void hideProgressBar() {
        this.setProgressBarIndeterminateVisibility(false);
    }

    private void notifyInvitees(List<ParseUser> invitees) {
        // TODO: Is there a way to send as a single query?
        for (ParseUser invitee : invitees) {
            ParseQuery query = ParseInstallation.getQuery();
            query.whereEqualTo(HomeActivity.INSTALLATION_USER_NAME_KEY,
                               invitee.getUsername());

            ParsePush push = new ParsePush();
            push.setQuery(query);
            push.setMessage("You have an event invite!");
            push.sendInBackground();
        }
    }

}
