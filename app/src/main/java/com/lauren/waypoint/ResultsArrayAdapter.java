package com.lauren.waypoint;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Created by cassie on 4/22/2015.
 */
public class ResultsArrayAdapter extends CursorAdapter {

    public ResultsArrayAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.results_list_item, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView resName = (TextView) view.findViewById(R.id.list_item_service_name);
        RatingBar resRating = (RatingBar) view.findViewById(R.id.list_item_rating);
        TextView resLocation = (TextView) view.findViewById(R.id.list_item_location);
        TextView resMilesAway = (TextView) view.findViewById(R.id.list_item_miles_away);
        TextView resArrivalTime = (TextView) view.findViewById(R.id.list_item_arrival_time);
        // Extract properties from cursor
        String item_name = cursor.getString(cursor.getColumnIndexOrThrow("res_name"));
        String item_rating = cursor.getString(cursor.getColumnIndexOrThrow("res_rating"));
        String item_location = cursor.getString(cursor.getColumnIndexOrThrow("res_location"));
        String item_miles = cursor.getString(cursor.getColumnIndexOrThrow("res_miles_away"));
        String item_arrival = cursor.getString(cursor.getColumnIndexOrThrow("res_arrival_time"));

        // Populate fields with extracted properties
        //tvBody.setText(body);
       // tvPriority.setText(String.valueOf(priority));

        resName.setText(item_name);
        //resRating.setRating(item_rating);
        resLocation.setText(item_location);
        resMilesAway.setText(item_miles);
        resArrivalTime.setText(item_arrival);


    }
}
