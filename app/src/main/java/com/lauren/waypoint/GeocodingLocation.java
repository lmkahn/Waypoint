package com.lauren.waypoint;

//Thanks to http://javapapers.com/android/android-geocoding-to-get-latitude-longitude-for-an-address/

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class GeocodingLocation {

    private static final String TAG = "GeocodingLocation";
    public static String destinationLatLong = "";
    public static HashMap latLong = new HashMap();

    public static HashMap getAddressFromLocation(final String locationAddress,
                                              final Context context) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List addressList = geocoder.getFromLocationName(locationAddress, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = (Address) addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        sb.append(address.getLatitude()).append("\n");
                        sb.append(address.getLongitude()).append("\n");
                        latLong.put("lat", address.getLatitude());
                        latLong.put("long", address.getLongitude());
                        result = sb.toString();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Unable to connect to Geocoder", e);
                }
                destinationLatLong = result;
            }
        };
        thread.start();
        return latLong;
    }

    public HashMap getLatLong(final String locationAddress,
                              final Context context) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String result = null;
        HashMap latLongTest = new HashMap();
        try {
            List addressList = geocoder.getFromLocationName(locationAddress, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = (Address) addressList.get(0);
                StringBuilder sb = new StringBuilder();
                sb.append(address.getLatitude()).append("\n");
                sb.append(address.getLongitude()).append("\n");
                latLongTest.put("lat", address.getLatitude());
                latLongTest.put("long", address.getLongitude());
                result = sb.toString();
            }
        } catch (IOException e) {
            Log.e(TAG, "Unable to connect to Geocoder", e);
        }
        return latLongTest;
    }
}