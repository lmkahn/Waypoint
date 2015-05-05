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
        TextView resId = (TextView) view.findViewById(R.id.list_item_id);
        TextView resCategories = (TextView) view.findViewById(R.id.list_item_categories);

        // Extract properties from cursor
        String item_name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        Float item_rating = cursor.getFloat(cursor.getColumnIndexOrThrow("rating"));
        String item_address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
        String item_categories = cursor.getString(cursor.getColumnIndexOrThrow("categories"));
        String item_id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));

        // Populate fields with extracted properties
        //tvBody.setText(body);
       // tvPriority.setText(String.valueOf(priority));

        resName.setText(item_name);
        resRating.setRating(item_rating);
        resLocation.setText(item_address);
        resId.setText(item_id);
        resCategories.setText(item_categories);


    }
}
