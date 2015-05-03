package com.lauren.waypoint;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.io.IOException;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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

    }
    //handles submit button click to query for route results
    public void onSubmitRoute(View v){
        EditText startingPointInput = (EditText) findViewById(R.id.route_start_input);
        EditText endPointInput = (EditText) findViewById(R.id.route_end_input);
        TextView startEmpty = (TextView) findViewById(R.id.start_required);
        TextView endEmpty = (TextView) findViewById(R.id.end_required);
        if( startingPointInput.getText().toString().trim().equals("")){
            startEmpty.setVisibility(View.VISIBLE);
        }
        if( endPointInput.getText().toString().trim().equals("")){
            endEmpty.setVisibility(View.VISIBLE);
        }
        //wrap everything that was in this function with an else statement
        else {
            //Get search category
            Spinner category = (Spinner) findViewById(R.id.service_type_spinner);
            String categoryStr = category.getSelectedItem().toString();

            //Get LatLong of start
            EditText startText = (EditText) findViewById(R.id.route_start_input);
            String startNameString = startText.getText().toString();

            //Get LatLong of destination
            EditText destinationText = (EditText) findViewById(R.id.route_end_input);
            String destNameString = destinationText.getText().toString();

            //Get hours and minutes
            int hoursVal = 0;
            EditText hours = (EditText) findViewById(R.id.hours_input);
            hoursVal = Integer.parseInt(hours.getText().toString());

            int minutesVal = 0;
            EditText minutes = (EditText) findViewById(R.id.minutes_input);
            minutesVal = Integer.parseInt(minutes.getText().toString());

            int seconds = minutesVal * 60 + hoursVal * 3600;
            //Finish getting time offset

            //Get route
            //Route router = new Route(startLat, startLong, destLat, destLong, seconds);
            Route router = new Route(categoryStr, startNameString, destNameString, seconds, database);
            Thread t = new Thread(router);
            t.start();
            Intent intent = new Intent(this, ListItemResults.class);
            startActivity(intent);
        }
    }
}
