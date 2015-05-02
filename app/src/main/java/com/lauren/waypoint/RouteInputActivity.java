package com.lauren.waypoint;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;

/**
 * Created by Lauren on 4/19/15.
 */
public class RouteInputActivity extends FragmentActivity {
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_input);

        ResultsDBHelper db = new ResultsDBHelper(this);
        database = db.getWritableDatabase();
        System.out.println("this is a console message");
        Log.i("messagetest", "this is a log message");
        final Button button = (Button) findViewById(R.id.submit_route);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Get LatLong of start
                EditText startText = (EditText) findViewById(R.id.route_start_input);
                String startNameString = startText.getText().toString();

                //GeocodingLocation locationAddress = new GeocodingLocation();
                //HashMap startLatLong = locationAddress.getLatLong(startNameString, getApplicationContext());

                //Get lat and long from the result string
                //String startLat = startLatLong.get("lat").toString();
                //String startLong = startLatLong.get("long").toString();

                //Get LatLong of destination
                EditText destinationText = (EditText) findViewById(R.id.route_end_input);
                String destNameString = destinationText.getText().toString();
                //GeocodingLocation locationAddressDest = new GeocodingLocation();
                //HashMap destinationLatLong = locationAddressDest.getLatLong(destNameString,mgetApplicationContext());

                //Get lat and long from the result string
                //String destLat = destinationLatLong.get("lat").toString();
                //String destLong = destinationLatLong.get("long").toString();

                //Get hours and minutes
                EditText hours = (EditText) findViewById(R.id.hours_input);
                int hoursVal = Integer.parseInt(hours.getText().toString());

                EditText minutes = (EditText) findViewById(R.id.minutes_input);
                int minutesVal = Integer.parseInt(minutes.getText().toString());

                int seconds = minutesVal * 60 + hoursVal * 3600;
                //Finish getting time offset

                //Get route
                //Route router = new Route(startLat, startLong, destLat, destLong, seconds);
                Route router = new Route(startNameString, destNameString, seconds, database);
                Thread t = new Thread(router);
                t.start();
            }
        });

    }
    //handles submit button click to query for route results
    public void onSubmitRoute(View v){
        Intent intent = new Intent(this, ListItemResults.class);
        startActivity(intent);
        //System.out.println("this is a console message");
    }
}
