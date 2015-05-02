package com.lauren.waypoint;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.LowLevelHttpRequest;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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
    String start;
    String destination;
    int secondsToStart;
    SQLiteDatabase database;
    String category;

    public Route(String category, String start, String destination, int secondsToStart, SQLiteDatabase database) {
        this.category = category;
        this.start = start;
        this.destination = destination;
        this.secondsToStart = secondsToStart;
        this.database = database;
    }

    public Route(String lat1, String long1, String lat2, String long2, int secondsToStart) {
        this.lat1 = lat1;
        this.lat2 = lat2;
        this.long1 = long1;
        this.long2 = long2;
        this.secondsToStart = secondsToStart;
    }

    public void route() throws IOException {
        HttpRequestFactory factory = transport.createRequestFactory();

        String urlString = "https://maps.googleapis.com/maps/api/directions/json?";
        List<NameValuePair> params = new LinkedList<NameValuePair>();
        //params.add(new BasicNameValuePair("origin", 42.358056 + "," + -71.063611));
        //params.add(new BasicNameValuePair("destination", 42.266667 + "," + -71.8));
        params.add(new BasicNameValuePair("origin", start));
        params.add(new BasicNameValuePair("destination", destination));
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
        int secondsTraveled = 0;
        int secondsOfStep = 0;
        List<JSONObject> validSteps = new ArrayList<JSONObject>();
        List<HashMap> validCoords = new ArrayList<HashMap>();
        int lowerBoundSeconds = secondsToStart - 20 * 60;
        int upperBoundSeconds = secondsToStart + 20 * 60;
        try {
            //jSteps = (JSONObject) firstLeg.getJSONArray("steps");
            jSteps = (JSONArray) firstLeg.get("steps");

            /** Traversing all steps */
            for (int k = 0; k < jSteps.size(); k++) {
                JSONObject thisStep = (JSONObject) jSteps.get(k);
                JSONObject durationObj = (JSONObject) thisStep.get("duration");
                long secondsLong = (Long) durationObj.get("value");
                secondsOfStep = (int) secondsLong;

                //Add step if it's after the desired time and within 30 mins of the desired time
                if(secondsTraveled >= lowerBoundSeconds
                        && secondsTraveled <= upperBoundSeconds) {
                    validSteps.add((JSONObject) jSteps.get(k));

                    //If this is the first valid element, also add starting coords
                    if(validSteps.size() == 1) {
                        HashMap latLong = new HashMap();
                        JSONObject stepEndLocation = ((JSONObject) ((JSONObject) jSteps.get(k)).get("start_location"));
                        latLong.put("lat", stepEndLocation.get("lat"));
                        latLong.put("long", stepEndLocation.get("lng"));
                        validCoords.add(latLong);
                    }
                    HashMap latLong = new HashMap();
                    JSONObject stepEndLocation = ((JSONObject) ((JSONObject) jSteps.get(k)).get("end_location"));
                    String lat = stepEndLocation.get("lat").toString();
                    String lon = stepEndLocation.get("lng").toString();
                    latLong.put("lat", lat);
                    latLong.put("long", lon);
                    validCoords.add(latLong);
                }
                secondsTraveled += secondsOfStep;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //Have a list of latlong pairs to call on yelp API

        //List of yelp results
        List<HashMap<String, String>> yelpResultToStore = new ArrayList<>();
        List<String> establishmentNames = new ArrayList<>(); //Used to check uniqueness
        for (int i = 0; i < validCoords.size() -1 ; i++) {
            //Maybe puff up a value if the box is practically a line?

            HashMap beginCoords = validCoords.get(i);
            HashMap endCoords = validCoords.get(i+1);
            //Determine the distance between the beginning, end, and center points
            HashMap centerCoords = midPoint(Double.parseDouble(beginCoords.get("lat").toString()),
                    Double.parseDouble(beginCoords.get("long").toString()),
                    Double.parseDouble(endCoords.get("lat").toString()),
                    Double.parseDouble(endCoords.get("long").toString()));

            Yelp yelp = new Yelp(category, beginCoords.get("lat").toString(),
                    beginCoords.get("long").toString(), endCoords.get("lat").toString(),
                    endCoords.get("long").toString(), database);

            ArrayList<HashMap<String, String>> yelpResults = yelp.queryAPI();
            for(int j = 0; j < yelpResults.size(); j++) {
                HashMap<String, String> yelpResult = yelpResults.get(j);

                //Check that this isn't already in the list to add
                String establishmentName = yelpResult.get("name");
                if(!(establishmentNames.contains(establishmentName))) {

                    Double resultLat = Double.parseDouble(yelpResult.get("latitude"));
                    Double resultLong = Double.parseDouble(yelpResult.get("longitude"));
                    //For each yelp result, find how far it is from the route
                    double beginDistance = distance(Double.parseDouble(beginCoords.get("lat").toString()),
                            resultLat, Double.parseDouble(beginCoords.get("long").toString()), resultLong);

                    double middleDistance = distance(Double.parseDouble(String.valueOf(centerCoords.get("lat"))),
                            resultLat, Double.parseDouble(String.valueOf(centerCoords.get("long"))), resultLong);

                    double endDistance = distance(Double.parseDouble(endCoords.get("lat").toString()),
                            resultLat, Double.parseDouble(endCoords.get("long").toString()), resultLong);
                    double distanceEstimate = (beginDistance + middleDistance + endDistance) / 3;
                    yelpResult.put("distanceFromRoute", String.valueOf(distanceEstimate));
                    yelpResultToStore.add(yelpResult);
                    //yelpResults.set(i, yelpResult);
                }
            }

        }

        //We now have a list of yelp results. What do we do with them?
        int index = 1;
        for (HashMap<String, String> result : yelpResultToStore) {
            String linkLocal = result.get("link");
            String distanceLocal = result.get("distanceFromRoute");
            //String selectQuery = "SELECT * FROM YelpData WHERE link=" + linkLocal + ";";
            //String result = database.execSQL(selectQuery);
            String databaseString = "INSERT INTO YelpData (_id, Name, Rating, Address, Link, Latitude, Longitude, Categories, Distance) VALUES (" + index + ", " + '"' + result.get("name") + '"'+ ", " + Float.parseFloat(result.get("rating")) + ", '" + result.get("address") + "', '" + result.get("link") + "', '" + result.get("latitude") + "', '" + result.get("longitude") + "', '" + result.get("categories") + "', '" + distanceLocal + "');";
            database.execSQL(databaseString);
            index++;
            //String databaseString = "INSERT INTO YelpData (Distance) VALUES ( " + distanceLocal + ") WHERE link = " + linkLocal + ";";
            //database.execSQL(databaseString);
        }
        //Let's sort them by distance
        //yelpResultToStore

    }

    private double distance(double lat1, double lat2, double lon1, double lon2) {

        final int R = 6371; // Radius of the earth

        Double latDistance = deg2rad(lat2 - lat1);
        Double lonDistance = deg2rad(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        distance = Math.pow(distance, 2);
        return Math.sqrt(distance);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static HashMap midPoint(double lat1,double lon1,double lat2,double lon2){

        double dLon = Math.toRadians(lon2 - lon1);

        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);
        HashMap center = new HashMap();
        center.put("lat", Math.toDegrees(lat3));
        center.put("long", Math.toDegrees(lon3));
        return center;
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
