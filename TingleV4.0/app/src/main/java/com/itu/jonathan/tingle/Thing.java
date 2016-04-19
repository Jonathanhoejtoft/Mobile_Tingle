package com.itu.jonathan.tingle;

import android.content.Context;
import android.provider.Settings.Secure;

/**
 * Created by Jonathan on 07-02-2016.
 */
public class Thing {
    private int mUUID = 0;
    private String mWhat = null;
    private String mWhere = null;


    public Thing(String what, String where) {
        mWhat = what;
        mWhere = where;
    }
    public Thing(String what, String where, int uuid) {
        mWhat = what;
        mWhere = where;
        mUUID = uuid;
    }
    @Override
    public String toString() { return oneLine("UUID:", "Item: ","is here: "); }
    public String getWhat() { return mWhat; }
    public void setWhat(String what) { mWhat = what; }
    public String getWhere() { return mWhere; }
    public int getUUID() { return mUUID; }
    public void setUUID(int UUID) { mUUID = UUID; }
    public void setWhere(String where) { mWhere = where; }
    public String oneLine(String id, String pre, String post) {return id+mUUID + " " + pre+mWhat + " "+post + mWhere;
    }

}
