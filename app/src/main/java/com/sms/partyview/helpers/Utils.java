package com.sms.partyview.helpers;

import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by myho on 7/7/14.
 */
public class Utils {
    public static final int NEW_EVENT_REQUEST_CODE = 20;
    public static final int RESPOND_TO_INVITE_EVENT_REQUEST_CODE = 30;

    public static final DateTimeFormatter DISPLAY_DATE_FORMATTER = DateTimeFormat
            .forPattern("E, MMM d");
    
    public static final DateTimeFormatter DISPLAY_TIME_FORMATTER = DateTimeFormat
            .forPattern("h:mm a");

    public static final DateTimeFormatter DISPLAY_DATE_TIME_FORMATTER = DateTimeFormat
            .forPattern("E, MMM d, h:mm a");

    public static final DateTimeFormatter DISPLAY_MONTH_FORMATTER = DateTimeFormat
            .forPattern("MMM");

    public static final DateTimeFormatter DISPLAY_DAY_FORMATTER = DateTimeFormat.forPattern("d");

    public static void cacheAppUsers(FindCallback<ParseUser> callBack) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();

        // Query for new results from the network.
        query.findInBackground(callBack);
    }

    public static String joinStrings(List<String> strings, String joinedByText) {
        String result = "";
        for (String string : strings) {
            if (!result.isEmpty()) {
                result += joinedByText;
            }
            result += string;
        }
        return result;
    }

    public static List<String> splitString(String input, String splitToken) {
        String[] splitArray = input.split(splitToken);
        List<String> results = new ArrayList<String>(Arrays.asList(splitArray));
        for (int i = 0; i < results.size(); ++i) {
            String trimmedResult = results.get(i).replaceAll("\\s+","");
            results.set(i, trimmedResult);
        }
        // Remove any empty strings from the split.
        for (int i = results.size() - 1; i >= 0; --i) {
            if (!results.get(i).isEmpty()) {
                continue;
            }
            results.remove(i);
        }
        return results;
    }
}
