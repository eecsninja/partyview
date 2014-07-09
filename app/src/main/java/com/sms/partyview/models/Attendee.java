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

    public Attendee(Parcel in) {
        super();
        readFromParcel(in);
    }


    public Attendee(String username, AttendanceStatus status) {
        this.username = username;
        this.status = status;
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
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(status.toString());
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
}
