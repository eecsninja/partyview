package com.sms.partyview.models;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

/**
 * Created by myho on 7/3/14.
 */
@ParseClassName("Event")
public class Event extends ParseObject {

    // Required: public default constructor
    public Event() {
    }

    public Event(
            String title,
            Date startDate,
            Date endDate,
            String description,
            String address,
            ParseGeoPoint location,
            ParseUser host
    ) {
        super();
        setTitle(title);
        setStartDate(startDate);
        setEndDate(endDate);
        setDescription(description);
        setAddress(address);
        setLocation(location);
        setHost(host);
    }

    public String getTitle() {
        return getString("title");
    }

    public void setTitle(String title) {
        put("title", title);
    }

    public Date getStartDate() {
        return getDate("start_date");
    }

    public void setStartDate(Date date) {
        put("start_date", date);
    }

    public Date getEndDate() {
        return getDate("end_date");
    }

    public void setEndDate(Date date) {
        put("end_date", date);
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

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }

    public void setLocation(ParseGeoPoint location) {
        put("location", location);
    }

    public void setHost(ParseUser user) {
        put("host", user);
    }

    public ParseUser getHost() {
        return getParseUser("host");
    }
}
