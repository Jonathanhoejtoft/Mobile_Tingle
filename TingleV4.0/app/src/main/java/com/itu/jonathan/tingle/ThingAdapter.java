package com.itu.jonathan.tingle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jonathan on 01-03-2016.
 */
class ThingAdapter extends ArrayAdapter<Thing>{
    public ThingAdapter(Context context, ArrayList<Thing> things) {
        super(context, 0, things);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Thing thing = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, parent, false);
        }

        TextView tvWhat = (TextView) convertView.findViewById(R.id.thing);
        TextView tvWhere = (TextView) convertView.findViewById(R.id.where);

        tvWhat.setText(thing.getWhat());
        tvWhere.setText(thing.getWhere());

        return convertView;
    }

}
