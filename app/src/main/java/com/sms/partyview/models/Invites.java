package com.sms.partyview.models;

import com.google.common.base.Splitter;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;

import android.util.Log;

import java.io.Serializable;

/**
 * Created by myho on 7/6/14.
 */
@ParseClassName("Invites")
public class Invites extends ParseObject implements Serializable {

    // Required: public default constructor
    public Invites() {
    }

    public Invites(
            String invites
    ) {
        super();
        setInvites(invites);
    }

    public void setInvites(String invitesString) {
        JSONArray myArray = new JSONArray();
        Iterable<String> tokens = Splitter.on(',').omitEmptyStrings().trimResults()
                .split(invitesString);
        Log.d("DEBUG", tokens.toString());

        for (String t : tokens) {
            myArray.put(t);
        }

        put("invites", myArray);
    }

    public JSONArray getInvites() {
        return getJSONArray("invites");
    }
}
