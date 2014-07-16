package com.sms.partyview.models;

import com.google.common.collect.ImmutableList;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import static com.sms.partyview.models.AttendanceStatus.ACCEPTED;
import static com.sms.partyview.models.AttendanceStatus.PRESENT;

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

    public static ParseQuery<EventUser> getQueryForAcceptedEvents() {
        // Define the class we would like to query
        ParseQuery<EventUser> query = ParseQuery.getQuery(EventUser.class);
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.whereContainedIn("status", ImmutableList.of(PRESENT.toString(), ACCEPTED.toString()));
        query.include("event.host");

        return query;
    }

    public static ParseQuery<EventUser> getQueryForPendingEvents() {
        // Define the class we would like to query
        ParseQuery<EventUser> query = ParseQuery.getQuery(EventUser.class);

        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.whereEqualTo("status", AttendanceStatus.INVITED.toString());
        query.include("event.host");

        return query;
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

    public Event getEvent() {
        return (Event) getParseObject("event");
    }

    public void setEvent(Event event) {
        put("event", event);
    }
}