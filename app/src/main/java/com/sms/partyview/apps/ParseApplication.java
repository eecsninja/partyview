package com.sms.partyview.apps;

import com.parse.Parse;
import com.parse.ParseObject;
import com.sms.partyview.models.Invites;
import com.sms.partyview.models.MyEvent;

import android.app.Application;

/**
 * Created by myho on 7/3/14.
 */
public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Register your parse models
        ParseObject.registerSubclass(MyEvent.class);
        ParseObject.registerSubclass(Invites.class);

        // Add your initialization code here
        Parse.initialize(this, "B29aQbOLKvph6s5n6kyt03lO2Spku4IotpCXVzJq",
                "eLqJBEWNzRrFso9Dj0AWzcocRRDaplnlDVGPAUvU");
    }
}
