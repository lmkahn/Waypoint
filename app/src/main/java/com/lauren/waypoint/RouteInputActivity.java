package com.lauren.waypoint;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Lauren on 4/19/15.
 */
public class RouteInputActivity extends FragmentActivity {
    SQLiteDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
            Intent intent = new Intent(this, ListItemResults.class);
            startActivity(intent);
        }
    }
}
