package com.sms.partyview.models;

import java.util.Date;

public class UserMarker
{
    private String mLabel;
    private Date lastUpdated;
    private Double mLatitude;
    private Double mLongitude;

    public UserMarker(String label, Double latitude, Double longitude)
    {
        this.mLabel = label;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
    }

    public String getmLabel()
    {
        return mLabel;
    }

    public void setmLabel(String mLabel)
    {
        this.mLabel = mLabel;
    }

    public Double getmLatitude()
    {
        return mLatitude;
    }

    public void setmLatitude(Double mLatitude)
    {
        this.mLatitude = mLatitude;
    }

    public Double getmLongitude()
    {
        return mLongitude;
    }

    public void setmLongitude(Double mLongitude)
    {
        this.mLongitude = mLongitude;
    }
}
