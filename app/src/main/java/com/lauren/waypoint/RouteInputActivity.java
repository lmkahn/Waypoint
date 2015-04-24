package com.lauren.waypoint;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

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
        Intent intent = new Intent(this, ListItemResults.class);
        startActivity(intent);
    }
}
