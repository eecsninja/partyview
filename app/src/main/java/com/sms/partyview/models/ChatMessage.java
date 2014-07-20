package com.sms.partyview.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sandra on 7/12/14.
 */
public class ChatMessage implements Parcelable {
    private String username;
    private String message;
    private String dateSent;

    public ChatMessage(Parcel in) {
        super();
        readFromParcel(in);
    }


    public ChatMessage(String username, String message, String time) {
        this.username = username;
        this.message = message;
        this.dateSent = time;
    }

    public static final Parcelable.Creator<ChatMessage> CREATOR = new Parcelable.Creator<ChatMessage>() {
        public ChatMessage createFromParcel(Parcel in) {
            return new ChatMessage(in);
        }

        public ChatMessage[] newArray(int size) {

            return new ChatMessage[size];
        }

    };

    public void readFromParcel(Parcel in) {
        username = in.readString();
        message = in.readString();
        dateSent = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(message);
        dest.writeString(dateSent);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDateSent(String date) {
        this.dateSent = date;
    }

    public String getDateSent() {
        return dateSent;
    }

}