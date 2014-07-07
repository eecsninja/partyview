package com.sms.partyview.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by myho on 7/3/14.
 */
@ParseClassName("TestEvent")
public class MyEvent extends ParseObject {

    // Required: public default constructor
    public MyEvent() {
    }

    public MyEvent(
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

    public ParseUser getHost()
    {
        return getParseUser("host");
    }

    public void setInvites(Invites invites) {
        put("invites", invites);
    }

    public Invites getInvites()
    {
        return (Invites) getParseObject("invites");
    }
}
