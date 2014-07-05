package com.sms.partyview.models;

import java.io.Serializable;

/**
 * Created by sque on 7/3/14.
 */
public class User implements Serializable {
    String userName;
    String email;
    String password;    // TODO: consider security issues.
    long remoteId;

    // TODO: Add location and status info.

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(long remoteId) {
        this.remoteId = remoteId;
    }
}
