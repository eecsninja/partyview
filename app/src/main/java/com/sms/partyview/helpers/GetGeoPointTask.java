package com.sms.partyview.helpers;

import com.parse.ParseGeoPoint;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.List;

/**
 * Adapted from android developer guide: http://developer.android.com/training/location/display-address.html
 *
 * A subclass of AsyncTask that calls getFromLocationName() in the
 * background. The class definition has these generic types:
 * String          - the address string to convert to Address object
 * Void            - indicates that progress units are not used
 * ParseGeopoint   - the object passed to onPostExecute()
 */
public class GetGeoPointTask extends AsyncTask<String, Void, ParseGeoPoint> {

    Context mContext;

    public GetGeoPointTask(Context context) {
        super();
        mContext = context;
    }

    /**
     * Get a Geocoder instance, get the latitude and longitude
     * look up the address, and return it
     *
     * @return A string containing the address of the current
     * location, or an empty string if no address can be found,
     * or an error message
     * @params params One or more Location objects
     */
    @Override
    protected ParseGeoPoint doInBackground(String... locationNames) {
        Geocoder geocoder = new Geocoder(mContext);

        // Create a list to contain the result address
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(locationNames[0], 1);
        } catch (IOException e1) {
            Log.e(mContext.getClass().getSimpleName(), "IO Exception in getFromLocationName()");
            e1.printStackTrace();
            return null;
        } catch (IllegalArgumentException e2) {
            String errorString = "null arguments passed to address service";
            Log.e(mContext.getClass().getSimpleName(), errorString);
            e2.printStackTrace();
            return null;
        }

        // If the reverse geocode returned an address
        if (addresses != null && addresses.size() > 0) {
            // Get the first address
            Address address = addresses.get(0);
            return new ParseGeoPoint(address.getLatitude(), address.getLongitude());
        } else {
            Log.e(mContext.getClass().getSimpleName(), "No address found");
            return null;
        }
    }
}
