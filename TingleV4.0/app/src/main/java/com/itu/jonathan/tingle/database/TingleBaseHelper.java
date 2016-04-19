package com.itu.jonathan.tingle.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.itu.jonathan.tingle.Thing;
import com.itu.jonathan.tingle.database.TingleDBSchema.TingleTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonathan on 19-04-2016.
 */
public class TingleBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "TingleBase.db";
    private static final String Table_name = "Ting";
    public TingleBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TingleTable.NAME + "(" +
                        " _id integer primary key autoincrement, " +
                        TingleTable.Cols.UUID + ", " +
                        TingleTable.Cols.TING + ", " +
                        TingleTable.Cols.LOCATION +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addTing(Thing thing) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TingleTable.Cols.UUID,"uuid1"); // Contact Name
        values.put(TingleTable.Cols.TING, thing.getWhat()); //
        values.put(TingleTable.Cols.LOCATION, thing.getWhere()); //

        // Inserting Row
        db.insert(Table_name, null, values);
        db.close(); // Closing database connection
    }


    public List<Thing> getAllThings() {
        List<Thing> thingList = new ArrayList<Thing>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Table_name;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Thing thing = new Thing("test","test");
                thing.setUUID(cursor.getInt(1));
                thing.setWhat(cursor.getString(2));
                thing.setWhere(cursor.getString(3));
                // Adding contact to list
                thingList.add(thing);
            } while (cursor.moveToNext());
        }

        // return contact list
        return thingList;
    }
}
