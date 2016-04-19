package com.itu.jonathan.tingle;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.itu.jonathan.tingle.database.TingleBaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JonathanLab on 11-02-2016.
 */
public class ThingsDB {
    private static ThingsDB sthingsDB;
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private List<Thing> mThingsDB;

    public static ThingsDB get(Context context){
        if(sthingsDB == null){
            sthingsDB = new ThingsDB(context);
        }
        return sthingsDB;

    }
    public List<Thing> getThingsDB() {return mThingsDB;}
    public void addThing (Thing thing){mThingsDB.add(thing);}
    public int size(){return mThingsDB.size();}
    public void removeThing(int index){
        mThingsDB.remove(index);
    }
    public Thing get(int i){ return mThingsDB.get(i);}



// old function
/*    private ThingsDB(Context context) {
        mThingsDB = new ArrayList<Thing>();
        mThingsDB.add(new Thing("skammel","underbordet"));
        mThingsDB.add(new Thing("sko","hylde"));
    }*/
// using a db
        private ThingsDB(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new TingleBaseHelper(mContext).getWritableDatabase();
        mThingsDB = new ArrayList<Thing>();
    }
}
