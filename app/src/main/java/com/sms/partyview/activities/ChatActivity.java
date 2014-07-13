package com.sms.partyview.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import com.parse.ParseUser;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.sms.partyview.R;
import com.sms.partyview.fragments.ChatFragment;

import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends FragmentActivity implements ChatFragment.OnFragmentInteractionListener {
    public static final String PUBLISH_KEY = "pub-c-adf5251f-8c96-477d-95fd-ab1907f93905";
    public static final String SUBSCRIBE_KEY = "sub-c-2f5285ae-08b6-11e4-9ae5-02ee2ddab7fe";

    private Pubnub pubnub;

    private EditText etMessage;

    private String eventId;

    private ChatFragment chatFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        etMessage = (EditText) findViewById(R.id.etSendMessage);
        etMessage.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    publishUserMessage(etMessage.getText().toString());
                    etMessage.setText("");
                }
                return false;
            }
        });

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



    public void onSendMessage(View v) {
        publishUserMessage(etMessage.getText().toString());
        etMessage.setText("");
    }

    public void publishUserMessage(String chatMessage) {

        Callback callback = new Callback() {
            public void successCallback(String channel, Object response) {
                Log.d("PUBNUB",response.toString());
            }
            public void errorCallback(String channel, PubnubError error) {
                Log.d("PUBNUB",error.toString());
            }
        };

        try {
            JSONObject dataToPublish = new JSONObject();
            JSONObject message = new JSONObject();
            message.put("username", ParseUser.getCurrentUser().getUsername());
            message.put("message", chatMessage);

            PrettyTime pt = new PrettyTime();
            message.put("timestamp", pt.format(new Date()));

            dataToPublish.put("chat", message);
            pubnub.publish(eventId, dataToPublish, callback);
        } catch (JSONException jsonException) {

        }
    }
}
