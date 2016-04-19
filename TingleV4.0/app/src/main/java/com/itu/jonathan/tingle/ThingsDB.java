package com.itu.jonathan.tingle;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JonathanLab on 11-02-2016.
 */
public class ThingsDB {
    private static ThingsDB sthingsDB;
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




    private ThingsDB(Context context) {
        mThingsDB = new ArrayList<Thing>();
        mThingsDB.add(new Thing("skammel","underbordet"));
        mThingsDB.add(new Thing("sko","hylde"));
    }
}
