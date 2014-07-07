package com.sms.partyview.models;

import com.google.common.collect.ImmutableList;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.util.Log;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by myho on 7/3/14.
 */
@ParseClassName("Event")
public class Event extends ParseObject implements Serializable{

    // Required: public default constructor
    public Event() {
    }

    public Event(
            String title,
            Date date,
            String description,
            String address,
            ParseUser host,
            Invites invites
    ) {
        super();
        setTitle(title);
        setDate(date);
        setDescription(description);
        setAddress(address);
        setHost(host);
        setInvites(invites);
    }

    public String getTitle() {
        return getString("title");
    }

    public void setTitle(String title) {
        put("title", title);
    }

    public Date getDate() {
        return getDate("date");
    }

    public void setDate(Date date) {
        put("date", date);
    }

    public String getDescription() {
        return getString("description");
    }

    public void setDescription(String description) {
        put("description", description);
    }

    public String getAddress() {
        return getString("address");
    }

    public void setAddress(String address) {
        put("address", address);
    }

    public void setHost(ParseUser user) {
        put("host", user);
    }

    public ParseUser getHost() {
        return getParseUser("host");
    }

    public void setInvites(Invites invites) {
        put("invites", invites);
    }

    public Invites getInvites() {
        return (Invites) getParseObject("invites");
    }
}
