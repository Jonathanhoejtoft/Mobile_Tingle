package com.itu.jonathan.tingle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class ListFragment extends Fragment {
    private static ThingsDB thingsDB;
    private ListView Thingslist;
    private ThingAdapter arrayAdapter ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_list, container, false);
        thingsDB = ThingsDB.get(getActivity());
        List<String> list = new ArrayList<String>();
        for (Thing t : thingsDB.getThingsDB()) {
            list.add(t.toString());
        }
        // setting arrayadapter
        //ArrayAdapter<Thing> arrayAdapter;

        //Thingslist.setAdapter(arrayAdapter);
        arrayAdapter = new ThingAdapter(getActivity(), (ArrayList)thingsDB.getThingsDB());

        Thingslist = (ListView) v.findViewById(R.id.list_item);
        Thingslist.setAdapter(arrayAdapter);
        Thingslist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final int savePosition = position;

                new AlertDialog.Builder(getActivity())
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ThingsDB.get(getActivity()).removeThing(savePosition);
                                ListFragment.this.UpdateList();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return false;
            }
        });
        return v;
    }
    public void UpdateList()
    {
        arrayAdapter.notifyDataSetChanged();
    }
}
