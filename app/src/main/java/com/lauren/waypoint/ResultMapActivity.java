package com.lauren.waypoint;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class ResultMapActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = ResultMapActivity.class.getSimpleName();
    private LocationRequest mLocationRequest;
    private GoogleMap mMap;
    SQLiteDatabase database;
    Double latitude = 0.0;
    Double longitude = 0.0;
    String name;
    String waypoint;
    String lat;
    String lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_map);

        // Find fields to populate in inflated template
        TextView resName = (TextView) findViewById(R.id.item_service_name);
        RatingBar resRating = (RatingBar) findViewById(R.id.item_rating);
        TextView resLocation = (TextView) findViewById(R.id.item_location);
        TextView resMilesAway = (TextView) findViewById(R.id.item_miles_away);
        TextView resId = (TextView) findViewById(R.id.item_id);
        TextView resCategories = (TextView) findViewById(R.id.item_categories);

        String location;
        Float rating;
        String milesAway;
        String arrivalTime;
        String id;
        String categories;

        if (savedInstanceState == null) {
            Bundle bundle = getIntent().getExtras();
            if(bundle == null) {
                id = null;
            } else {
               id = bundle.getString("ID");
            }
        } else {
            id = (String) savedInstanceState.getSerializable("ID");
        }

        ResultsDBHelper db = new ResultsDBHelper(this);
        database = db.getWritableDatabase();

        String queryString = "SELECT * FROM YelpData WHERE _id=" + "'" + id + "'";
        Cursor c = database.rawQuery(queryString, null);
        c.moveToFirst();

        latitude = c.getDouble(c.getColumnIndexOrThrow("latitude"));
        longitude = c.getDouble(c.getColumnIndexOrThrow("longitude"));

        lat = latitude.toString();
        lon = longitude.toString();

        name = c.getString(c.getColumnIndexOrThrow("name"));
        location = c.getString(c.getColumnIndexOrThrow("address"));
        rating = c.getFloat(c.getColumnIndexOrThrow("rating"));
        milesAway = c.getString(c.getColumnIndexOrThrow("distance"));
        arrivalTime = c.getString(c.getColumnIndexOrThrow("latitude"));
        categories = c.getString(c.getColumnIndexOrThrow("categories"));

        waypoint = location;

        resName.setText(name);
        resLocation.setText(location);
        resMilesAway.setText(milesAway);
        resRating.setRating(rating);
        resCategories.setText(categories);

        setUpMapIfNeeded();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
    }

    public void addPointToRoute(View v){
//        Intent intent = new Intent(this, RouteSummaryActivity.class);
//        startActivity(intent);
        String start = Route.getStart();
        String end = Route.getDestination();
        //use lat long of waypoint address

        String waypointStr = lat + "," + lon ;
        String uriString = "http://maps.google.com/maps?output=classic&saddr=" + start + "&daddr=" + waypointStr + "+to:" + end;

        database.delete("YelpData", null, null);

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(uriString));
        startActivity(intent);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();

    }

    @Override
    protected void onPause() {
        super.onPause();
        // if you unregister the last listener, the hardware will stop detecting step events
//        sensorManager.unregisterListener(this);
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            handleNewLocation(location);
        };
    }

    private void handleNewLocation(Location location) {
        LatLng latLng = new LatLng(latitude,longitude);
        mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).title("Marker"));
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
