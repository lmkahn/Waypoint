package com.lauren.waypoint;

import android.util.Log;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.LowLevelHttpRequest;

import java.io.IOException;

/**
 * Created by Andy on 4/29/15.
 */
public class Route {
    static final HttpTransport transport = new HttpTransport() {
        @Override
        protected LowLevelHttpRequest buildRequest(String method, String url) throws IOException {
            return null;
        }
    };

    public Route() {
    }

    public static void route() throws IOException {
        HttpRequestFactory factory = transport.createRequestFactory();
        String urlString = "http://maps.googleapis.com/maps/api/directions/xml?"
                + "origin=" + 42.2667 + "," + 71.8000
                + "&destination=" + 42.360 + "," + 71.0589
                + "&sensor=false&units=metric&mode=driving";
        GenericUrl url = new GenericUrl(urlString);
        url.put("key", "AIzaSyAvPCZzdtfCJy_bDpXQsHs1znICS-LqMpo");
        HttpRequest request = factory.buildGetRequest(url);
        com.google.api.client.http.HttpResponse response = request.execute();
        Log.i("RESPONSE", response.toString());
        Log.i("TEST", "TEST");
        System.out.print(response.toString());
        Log.e("HELLO", "HI");

    }
}
