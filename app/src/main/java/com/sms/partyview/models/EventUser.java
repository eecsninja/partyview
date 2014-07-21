package com.sms.partyview.models;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

import static com.sms.partyview.models.AttendanceStatus.ACCEPTED;
import static com.sms.partyview.models.AttendanceStatus.INVITED;
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
        List<String> statuses = Arrays.asList(PRESENT.toString(), ACCEPTED.toString());
        return getQueryForCurrentUserEventsWithStatus(statuses);
    }

    public static ParseQuery<EventUser> getQueryForPendingEvents() {
        List<String> statuses = Arrays.asList(INVITED.toString());
        return getQueryForCurrentUserEventsWithStatus(statuses);
    }

    public static ParseQuery<EventUser> getQueryForAttendeeList(String eventId) {
        // Define the class we would like to query
        ParseQuery<EventUser> query = ParseQuery.getQuery(EventUser.class);
        query.whereEqualTo("event", ParseObject.createWithoutData(Event.class, eventId));
        query.include("user");

        return query;
    }

    public static ParseQuery<EventUser> getQueryForEventUserWith(String userId,
            String eventId) {
        ParseQuery<EventUser> query = new ParseQuery(EventUser.class);
        query.whereEqualTo("user", ParseObject.createWithoutData(ParseUser.class, userId));
        query.whereEqualTo("event", ParseObject.createWithoutData(Event.class, eventId));
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

    // Given a list of attendance statuses in string format, return a query that for all EventUser
    // objects with that status, for the current user.
    private static ParseQuery<EventUser> getQueryForCurrentUserEventsWithStatus(
            List<String> statuses) {
        // Define the class we would like to query
        ParseQuery<EventUser> query = ParseQuery.getQuery(EventUser.class);
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.whereContainedIn("status", statuses);
        query.include("event.host");

        return query;
    }
}