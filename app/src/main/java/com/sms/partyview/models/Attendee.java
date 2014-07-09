package com.sms.partyview.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.sms.partyview.AttendanceStatus;

/**
 * Created by sandra on 7/8/14.
 */
public class Attendee implements Parcelable {
    private String username;
    private AttendanceStatus status;
    private double latitude;
    private double longitude;

    public Attendee(Parcel in) {
        super();
        readFromParcel(in);
    }


    public Attendee(EventUser eventUser) {
        this.username = eventUser.getUser().getUsername();
        this.status = eventUser.getStatus();
        this.latitude = eventUser.getLocation().getLatitude();
        this.longitude = eventUser.getLocation().getLongitude();
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
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(status.toString());
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
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
}
