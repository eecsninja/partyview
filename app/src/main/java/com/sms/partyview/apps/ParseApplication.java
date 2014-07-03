package com.sms.partyview.apps;

import com.parse.Parse;

import android.app.Application;

/**
 * Created by myho on 7/3/14.
 */
public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Add your initialization code here
        Parse.initialize(this, "B29aQbOLKvph6s5n6kyt03lO2Spku4IotpCXVzJq",
                "eLqJBEWNzRrFso9Dj0AWzcocRRDaplnlDVGPAUvU");
    }
}
