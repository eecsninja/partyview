package com.sms.partyview.helpers;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.util.Log;

import java.util.List;

/**
 * Created by myho on 7/7/14.
 */
public class Utils {
    public static final int NEW_EVENT_REQUEST_CODE = 20;
    public static final int RESPOND_TO_INVITE_EVENT_REQUEST_CODE = 30;

    public static void cacheAppUsers(FindCallback<ParseUser> callBack) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();

        // Query for new results from the network.
        query.findInBackground(callBack);
    }
}
