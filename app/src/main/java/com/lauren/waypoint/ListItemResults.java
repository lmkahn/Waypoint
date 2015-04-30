package com.lauren.waypoint;

import android.app.ListActivity;
import android.content.Intent;
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
import android.widget.TextView;
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
        TextView resultIdText = (TextView) v.findViewById(R.id.list_item_id);
        String resultId = (String) resultIdText.getText();

        Intent intent = new Intent(this, ResultMapActivity.class);
        intent.putExtra("ID", resultId);

        startActivity(intent);


    }
}
