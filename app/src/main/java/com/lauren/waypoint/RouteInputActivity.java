package com.lauren.waypoint;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

/**
 * Created by Lauren on 4/19/15.
 */
public class RouteInputActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Andy", "AndyTest");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_input);
    }
}
