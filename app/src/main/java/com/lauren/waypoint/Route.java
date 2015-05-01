package com.lauren.waypoint;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.JSONTokener;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Andy on 4/29/15.
 */
public class Route implements Runnable{
    static final HttpTransport transport = new HttpTransport() {
        @Override
        protected LowLevelHttpRequest buildRequest(String method, String url) throws IOException {
            return null;
        }
    };
    String lat1;
    String lat2;
    String long1;
    String long2;

    public Route(String lat1, String long1, String lat2, String long2) {
        this.lat1 = lat1;
        this.lat2 = lat2;
        this.long1 = long1;
        this.long2 = long2;
    }

    public void route() throws IOException {
        HttpRequestFactory factory = transport.createRequestFactory();

        String urlString = "https://maps.googleapis.com/maps/api/directions/json?";
        List<NameValuePair> params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("origin", 42.358056 + "," + -71.063611));
        params.add(new BasicNameValuePair("destination", 42.266667 + "," + -71.8));
        params.add(new BasicNameValuePair("sensor", "false"));
        params.add(new BasicNameValuePair("key", "AIzaSyAvPCZzdtfCJy_bDpXQsHs1znICS-LqMpo"));
        String paramString = URLEncodedUtils.format(params, "utf-8");
        urlString += paramString;

        InputStream inputStream = null;
        String json = "";

        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(urlString);
            org.apache.http.HttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            inputStream = entity.getContent();
        }
        catch(Exception e) {
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"),8);
            StringBuilder sbuild = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sbuild.append(line);
            }
            inputStream.close();
            json = sbuild.toString();
        }
        catch(Exception e) {
        }

        JSONParser parser = new JSONParser();
        Object obj = null;
        try {
            obj = parser.parse(json);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONObject jb = (JSONObject) obj;

        //now read
        JSONArray jsonArray = null;
        jsonArray = (JSONArray) jb.get("routes");

        JSONObject firstDirection = null;
        firstDirection = (JSONObject)jsonArray.get(0);

        JSONObject polyline = null;
        polyline = (JSONObject)firstDirection.get("overview_polyline");

        JSONArray legs = null;
        legs = (JSONArray) firstDirection.get("legs");
        JSONObject firstLeg = (JSONObject) legs.get(0);
        JSONObject distance = null;
        distance = (JSONObject) firstLeg.get("distance");
        Long distanceValue = (Long) distance.get("value");
        double distanceMeters = Double.parseDouble(distanceValue.toString());
        double distanceKilometers = distanceMeters / 1000;
        Log.i("Distance", String.valueOf(distanceKilometers));

        Log.i("Polyline", polyline.toString());
        Log.i("Hello", "Hey");


        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
        JSONArray jSteps = null;
        List<LatLng> LatLongList = new ArrayList<LatLng>();
        try {
            //jSteps = (JSONObject) firstLeg.getJSONArray("steps");
            jSteps = (JSONArray) firstLeg.get("steps");
            List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

            /** Traversing all steps */
            for (int k = 0; k < jSteps.size(); k++) {
                String poly = "";
                poly = (String) ((JSONObject) ((JSONObject) jSteps
                        .get(k)).get("polyline")).get("points");
                List<LatLng> list = decodePoly(poly);
            }
            routes.add(path);

        } catch (Exception e) {
        }


    }

    @Override
    public void run() {
        try {
            route();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }
}
