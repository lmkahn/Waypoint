package com.lauren.waypoint;

/**
 * Created by cassie on 4/22/2015.
 */
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.database.Cursor;

public class ResultsDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String RESULTS_TABLE_NAME = "yelpResults";
    private static final String RESULTS_TABLE_CREATE =
            "CREATE TABLE " + RESULTS_TABLE_NAME + " (" +
                    "_id" + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "res_name" + " TEXT, " +
                    "res_rating" + " TEXT, " +
                    "res_location" + " TEXT, " +
                    "res_miles_away" + " TEXT, " +
                    "res_arrival_time" + " TEXT);";

    ResultsDBHelper(Context context) {
        super(context, RESULTS_TABLE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RESULTS_TABLE_CREATE);
        //initialize minute labels here probably
        db.execSQL("INSERT INTO yelpResults(_id,res_name,res_rating,res_location,res_miles_away,res_arrival_time) VALUES ('1','someservice','3','Somewhere','30','430')");
        db.execSQL("INSERT INTO yelpResults(_id,res_name,res_rating,res_location,res_miles_away,res_arrival_time) VALUES ('2','somesafe','4','SomewhereElse','50','500')");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int OldVersion, int NewVersion){
        return;
    }

    public Cursor query(String query, SQLiteDatabase db) {
        String select = null;
        if(query.length() > 1) {
            select = query;
        }

        return db.rawQuery(query, null);
    }
}
