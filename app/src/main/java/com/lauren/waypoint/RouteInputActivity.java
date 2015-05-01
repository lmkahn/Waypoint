package com.lauren.waypoint;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

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
                //Geocode to get lat and long
                String lat1 = "";
                String long1 = "";
                String lat2 = "";
                String long2 = "";
                Route router = new Route(lat1, long1, lat2, long2);
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
