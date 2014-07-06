package com.sms.partyview.models;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by sandra on 7/6/14.
 */
@ParseClassName("EventUser")
public class EventUser extends ParseObject {
    public ParseGeoPoint location;
    public ParseUser user;
    public int attendanceStatus;

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

    public int getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(int attendanceStatus) {
        put("attendanceStatus", attendanceStatus);
    }

    public static ParseQuery<EventUser> getQuery() {
        return ParseQuery.getQuery(EventUser.class);
    }
}