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
import android.widget.Toast;

import com.itu.jonathan.tingle.database.TingleBaseHelper;

import java.util.ArrayList;
import java.util.List;


public class ListFragment extends Fragment {
    private static ThingsDB thingsDB;
    private ListView Thingslist;
    private ArrayAdapter arrayAdapter ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_list, container, false);
/*        thingsDB = ThingsDB.get(getActivity());
        List<String> list = new ArrayList<String>();
        for (Thing t : thingsDB.getThingsDB()) {
            list.add(t.toString());
        }*/

        // test
        TingleBaseHelper db = new TingleBaseHelper(getActivity());

        List<Thing> thing = new ArrayList<Thing>();
        thing= db.getAllThings();
        arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, thing);
        //listContent.setAdapter(adapter);
        // test

        // setting arrayadapter
        //ArrayAdapter<Thing> arrayAdapter;

        //Thingslist.setAdapter(arrayAdapter);
       // arrayAdapter = new ThingAdapter(getActivity(), (ArrayList) thingsDB.getThingsDB());

        Thingslist = (ListView) v.findViewById(R.id.list_item);
        Thingslist.setAdapter(arrayAdapter);

        Thingslist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
                //Thing thing = (Thing) Thingslist.getItemAtPosition(position);
                final int savePosition = position;
                final long delID = id+1;

                new AlertDialog.Builder(getActivity())
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Thing thing = (Thing) Thingslist.getItemAtPosition(position);
                                TingleBaseHelper db = new TingleBaseHelper(getActivity());
                                //db.deleteTing(delID);
                                try{
                                    db.deleteThing(thing);
                                    Toast.makeText(getActivity(),"Deleted "+thing.getWhat(),Toast.LENGTH_LONG).show();
                                    arrayAdapter.remove(thing);
                                }
                                catch (Exception ex){
                                    Toast.makeText(getActivity(),"Delete failed" + delID + "pos" +position ,Toast.LENGTH_LONG).show();
                                }
                                finally {
                                    db.close();
                                }
                                /*db.deleteThing(thing);
                                arrayAdapter.remove(thing);

                                if (db.deleteTing(delID))
                                    Toast.makeText(getActivity(),"Deleted entry",Toast.LENGTH_LONG).show();

                                else
                                    Toast.makeText(getActivity(),"Delete failed" + delID + "pos" +position ,Toast.LENGTH_LONG).show();
                                db.close();*/

                                //db.deleteThing(delID);
                                Thingslist.invalidateViews();
                                //ListFragment.this.UpdateList();

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
