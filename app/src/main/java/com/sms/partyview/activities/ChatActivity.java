package com.sms.partyview.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import com.pubnub.api.Pubnub;
import com.sms.partyview.R;
import com.sms.partyview.fragments.ChatFragment;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class ChatActivity extends FragmentActivity implements ChatFragment.OnFragmentInteractionListener {
    public static final String PUBLISH_KEY = "pub-c-adf5251f-8c96-477d-95fd-ab1907f93905";
    public static final String SUBSCRIBE_KEY = "sub-c-2f5285ae-08b6-11e4-9ae5-02ee2ddab7fe";

    private Pubnub pubnub;

    private EditText etMessage;

    private String eventId;

    private ChatFragment chatFragment;

    protected static final DateTimeFormatter DISPLAY_TIME_FORMATTER = DateTimeFormat
            .forPattern("h:mm a");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        eventId = getIntent().getStringExtra("eventId");
        setupChatFragment();

        pubnub = new Pubnub(PUBLISH_KEY, SUBSCRIBE_KEY);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setupChatFragment() {
        // Create the transaction
        FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
        // Replace the content of the container
        chatFragment = ChatFragment.newInstance(eventId);
        fts.replace(R.id.flChatContainer, chatFragment);
        fts.commit();
    }
}
