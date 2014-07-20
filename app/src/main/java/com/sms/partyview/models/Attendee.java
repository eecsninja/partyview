package com.sms.partyview.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by sandra on 7/8/14.
 */
public class Attendee implements Parcelable {
    private String username;
    private AttendanceStatus status;
    private double latitude;
    private double longitude;
    private Date updatedAt;
    private String objectId;

    public Attendee(Parcel in) {
        super();
        readFromParcel(in);
    }


    public Attendee(EventUser eventUser) {
        this.username = eventUser.getUser().getUsername();
        this.status = eventUser.getStatus();
        this.latitude = eventUser.getLocation().getLatitude();
        this.longitude = eventUser.getLocation().getLongitude();
        this.updatedAt = eventUser.getUpdatedAt();
        this.objectId = eventUser.getObjectId();
    }

    public static final Parcelable.Creator<Attendee> CREATOR = new Parcelable.Creator<Attendee>() {
        public Attendee createFromParcel(Parcel in) {
            return new Attendee(in);
        }

        public Attendee[] newArray(int size) {

            return new Attendee[size];
        }

    };

    public void readFromParcel(Parcel in) {
        username = in.readString();
        status = AttendanceStatus.valueOf(in.readString());
        latitude = in.readDouble();
        longitude = in.readDouble();
        updatedAt = new Date(in.readLong());
        objectId = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(status.toString());
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeLong(updatedAt.getTime());
        dest.writeString(objectId);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getObjectId() {
        return objectId;
    }


}
