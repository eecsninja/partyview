package com.sms.partyview.models;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sms.partyview.AttendanceStatus;

import java.util.Date;

/**
 * Created by sandra on 7/6/14.
 */
@ParseClassName("EventUser")
public class EventUser extends ParseObject {
    // Required: public default constructor
    public EventUser() {
    }

    public EventUser(
            AttendanceStatus status,
            ParseGeoPoint location,
            ParseUser user,
            Event event
    ) {
        super();
        setStatus(status);
        setLocation(location);
        setUser(user);
        setEvent(event);
    }


    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser value) {
        put("user", value);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }

    public void setLocation(ParseGeoPoint value) {
        put("location", value);
    }

    public AttendanceStatus getStatus() {
        return AttendanceStatus.valueOf(getString("status"));
    }
    public void setStatus(AttendanceStatus status) {
        put("status", status.toString());
    }

    public void setEvent(Event event) {
        put("event", event);
    }

    public Event getEvent() {
        return (Event) getParseObject("event");
    }

    public static ParseQuery<EventUser> getQuery() {
        return ParseQuery.getQuery(EventUser.class);
    }

}