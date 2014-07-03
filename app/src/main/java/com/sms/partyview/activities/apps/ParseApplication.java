package com.sms.partyview.activities.apps;

import android.app.Application;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;

/**
 * Created by myho on 7/3/14.
 */
public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Add your initialization code here
        Parse.initialize(this, "B29aQbOLKvph6s5n6kyt03lO2Spku4IotpCXVzJq", "eLqJBEWNzRrFso9Dj0AWzcocRRDaplnlDVGPAUvU");
    }
}
