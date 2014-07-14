package com.sms.partyview.models;

import com.parse.ParseGeoPoint;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by sandra on 7/13/14.
 */
public class LocalEvent implements Serializable {
    String title;
    Date startDate;
    Date endDate;
    String description;
    String address;
    Double latitude;
    Double longitude;
    String hostName;
    String objectId;

    public LocalEvent(Event event) {
        this.title = event.getTitle();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
        this.description = event.getDescription();
        this.address = event.getAddress();
        if (event.getLocation() != null) {
            this.latitude = event.getLocation().getLatitude();
            this.longitude = event.getLocation().getLongitude();
        }
        this.hostName = event.getHost().getUsername();
        this.objectId = event.getObjectId();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }


    public String getHost() {
        return hostName;
    }

    public void setHost(String host) {
        this.hostName = host;
    }

    public String getObjectId() { return objectId; }

    public void setObjectId(String objectId) { this.objectId = objectId; }
}
