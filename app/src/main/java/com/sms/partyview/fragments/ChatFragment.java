package com.sms.partyview.fragments;



import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;
import com.sms.partyview.R;
import com.sms.partyview.adapters.AttendeeArrayAdapter;
import com.sms.partyview.adapters.ChatMessageAdapter;
import com.sms.partyview.models.Attendee;
import com.sms.partyview.models.ChatMessage;
import com.sms.partyview.models.EventUser;

import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ChatFragment extends Fragment {
    private List<ChatMessage> messages;
    private ChatMessageAdapter messageAdapter;
    private ListView messagesView;

    public static final String PUBLISH_KEY = "pub-c-adf5251f-8c96-477d-95fd-ab1907f93905";
    public static final String SUBSCRIBE_KEY = "sub-c-2f5285ae-08b6-11e4-9ae5-02ee2ddab7fe";

    private Pubnub pubnub;

    private String eventId;

    private EditText etMessage;

    private OnFragmentInteractionListener mListener;


    protected static final DateTimeFormatter DISPLAY_TIME_FORMATTER = DateTimeFormat
            .forPattern("h:mm a");

    public static ChatFragment newInstance(String eventId) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        fragment.setArguments(args);
        return fragment;
    }
    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize event list and adapter.

        messages = new ArrayList<ChatMessage>();
        messageAdapter = new ChatMessageAdapter(getActivity(), messages);

        eventId = getArguments().getString("eventId");

        subscribeToChannel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout.
        View view = inflater.inflate(
                R.layout.fragment_chat, container, false);

        messagesView = (ListView) view.findViewById(R.id.lvChat);
        messagesView.setAdapter(messageAdapter);

        etMessage = (EditText) view.findViewById(R.id.etSendMessage);
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

        // Return it.
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
    }

    public void onSendMessage(View v) {
        publishUserMessage(etMessage.getText().toString());
        etMessage.setText("");
    }


    public void subscribeToChannel () {
        pubnub = new Pubnub(PUBLISH_KEY, SUBSCRIBE_KEY);
        try {
            pubnub.subscribe(eventId, new Callback() {
                @Override
                public void connectCallback(String channel, Object message) {
                    Log.d("PUBNUB", "SUBSCRIBE : CONNECT on channel:" + channel
                            + " : " + message.getClass() + " : "
                            + message.toString());
                }

                @Override
                public void disconnectCallback(String channel, Object message) {
                    Log.d("PUBNUB","SUBSCRIBE : DISCONNECT on channel:" + channel
                            + " : " + message.getClass() + " : "
                            + message.toString());
                }

                public void reconnectCallback(String channel, Object message) {
                    Log.d("PUBNUB","SUBSCRIBE : RECONNECT on channel:" + channel
                            + " : " + message.getClass() + " : "
                            + message.toString());
                }

                @Override
                public void successCallback(String channel, Object message) {
                    Log.d("PUBNUB","SUBSCRIBE : " + channel + " : "
                            + message.getClass() + " : " + message.toString());
                    try {
                        if (message instanceof JSONObject) {
                            JSONObject data = (JSONObject) message;
                            if (data.getJSONObject("chat") != null) {
                                JSONObject chatMessageObj = data.getJSONObject("chat");
                                final ChatMessage chatMessage = new ChatMessage(
                                        chatMessageObj.getString("username"),
                                        chatMessageObj.getString("message"),
                                        chatMessageObj.getString("timestamp"));
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        messageAdapter.add(chatMessage);
                                    }
                                });
                            }
                        }
                    } catch (JSONException e) {

                    }
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    Log.d("PUBNUB","SUBSCRIBE : ERROR on channel " + channel
                            + " : " + error.toString());
                }
            }
            );
        } catch (PubnubException e) {
            Log.d("PUBNUB",e.toString());
        }
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

            MutableDateTime time = new MutableDateTime();
            message.put("timestamp", time.toString(DISPLAY_TIME_FORMATTER));

            dataToPublish.put("chat", message);
            pubnub.publish(eventId, dataToPublish, callback);
        } catch (JSONException jsonException) {

        }
    }

}
