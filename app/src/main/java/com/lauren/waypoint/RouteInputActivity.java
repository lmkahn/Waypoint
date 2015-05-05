package com.lauren.waypoint;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
//import com.lauren.waypoint.Route.returnResults;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

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

        String error;
        if (savedInstanceState == null) {
            Bundle bundle = getIntent().getExtras();
            if(bundle == null) {
                error = null;
            } else {
                error = bundle.getString("Error");
            }
        } else {
            error = (String) savedInstanceState.getSerializable("ID");
        }

        if(error != null){
            Context context = getApplicationContext();
            CharSequence text = "No Results";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.TOP, 0, 150);
            toast.show();
        }

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
            String hoursString = hours.getText().toString();
            if(!(hoursString.equals(""))) {
                hoursVal = Integer.parseInt(hoursString);
            }

            int minutesVal = 0;
            EditText minutes = (EditText) findViewById(R.id.minutes_input);
            String minutesString = minutes.getText().toString();
            if(!(minutesString.equals(""))) {
                minutesVal = Integer.parseInt(minutesString);
            }

            int seconds = minutesVal * 60 + hoursVal * 3600;
            //Finish getting time offset

            //Get route
            Route router = new Route(getApplicationContext(), categoryStr, startNameString, destNameString, seconds, database);
            Thread t = new Thread(router);
            t.start();

            //Pause so thread can finish and populate DB
            try {
                TimeUnit.MILLISECONDS.sleep(3500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(this, ListItemResults.class);
            startActivity(intent);

        }
    }
}
