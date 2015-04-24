package com.lauren.waypoint;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.lauren.waypoint.R;

import java.util.ArrayList;

/**
 * Created by Lauren on 4/19/15.
 */
public class ListItemResults extends ListActivity {

    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_results_listview);
        /*
        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
        */

        ResultsDBHelper db = new ResultsDBHelper(this);
        database = db.getWritableDatabase();

        String queryString = "SELECT * FROM yelpResults";
        Cursor c = database.rawQuery(queryString, null);
        // Find ListView to populate
        ListView resultsListView = (ListView) findViewById(android.R.id.list);
        // Setup cursor adapter using cursor from last step
        ResultsArrayAdapter resultsAdapter = new ResultsArrayAdapter(this, c);
        // Attach cursor adapter to the ListView
        resultsListView.setAdapter(resultsAdapter);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
    }
}
