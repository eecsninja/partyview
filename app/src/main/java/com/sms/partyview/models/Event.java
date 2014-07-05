package com.sms.partyview.models;

import java.io.Serializable;

/**
 * Created by sque on 7/4/14.
 */
public class Event implements Serializable {
    String title;
    String time;
    User host;
    String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public User getHost() {
        return host;
    }

    public void setHost(User host) {
        this.host = host;
    }

    public String getDescription() { return description; }

    public void setDescription(String desc) {
        this.description = desc;
    }

}

