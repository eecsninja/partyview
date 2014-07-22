package com.sms.partyview.activities;

import com.sms.partyview.R;
import com.sms.partyview.fragments.InvitedEventDetailFragment;
import com.sms.partyview.models.LocalEvent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;

import static com.sms.partyview.models.AttendanceStatus.ACCEPTED;
import static com.sms.partyview.models.AttendanceStatus.DECLINED;

public class InviteActivity extends FragmentActivity
        implements InvitedEventDetailFragment.InviteFragmentListener {

    // Data objects.
    protected LocalEvent tempEvent;

    protected InvitedEventDetailFragment detailFragment;

    public static final String EVENT_INTENT_KEY = "event";
    public static final String INVITE_RESPONSE_KEY = "response";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        tempEvent = (LocalEvent) getIntent().getSerializableExtra(EVENT_INTENT_KEY);
        setupDetailFragment();
    }

    public void onSaveResponse(String response, String eventId) {
        // return to list of events
        Intent data = new Intent();
        data.putExtra(EVENT_INTENT_KEY, eventId);
        data.putExtra(INVITE_RESPONSE_KEY, response);
        setResult(RESULT_OK, data);

        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setupDetailFragment() {
        // Create the transaction
        FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
        // Replace the content of the container
        detailFragment = InvitedEventDetailFragment.newInstance(tempEvent);
        fts.replace(R.id.flInviteDetailContainer, detailFragment);
        fts.commit();
    }

    public void onAcceptInvite(View v) {
        detailFragment.respondToInvite(ACCEPTED);
    }

    public void onRejectInvite(View v) {
        detailFragment.respondToInvite(DECLINED);
    }
}
