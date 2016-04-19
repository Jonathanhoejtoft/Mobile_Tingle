package com.itu.jonathan.tingle;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.itu.jonathan.tingle.database.TingleBaseHelper;
import com.itu.jonathan.tingle.database.TingleDBSchema;

import java.util.List;


public class TingleFragment extends Fragment {

    // GUI variables
    private Button addThing;
    private Button searchThing;
    private Button Listbtn;
    private TextView lastAdded;
    private TextView newWhat, newWhere;
    public TextView searchT;
    //fake database
    //private List<Thing> thingsDB;
    private static ThingsDB thingsDB;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thingsDB = ThingsDB.get(this.getActivity());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_tingle,container,false);
        lastAdded = (TextView) v.findViewById(R.id.last_thing);
        //updateUI();

        // Button
        addThing = (Button) v.findViewById(R.id.add_button);
        searchThing = (Button) v.findViewById(R.id.search_go_btn_button);
        //Listbtn = (Button) v.findViewById(R.id.Listbtn);

        // Textfields for describing a thing

        newWhat = (TextView) v.findViewById(R.id.what_text);
        newWhere = (TextView) v.findViewById(R.id.where_text);
        //searchT = (TextView) v.findViewById(R.id.search_text);

        // view products click event
        addThing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((newWhat.getText().length() > 0) && (newWhere.getText().length() > 0
                )) {

                    TingleBaseHelper db = new TingleBaseHelper(getActivity());

                    /**
                     * CRUD Operations
                     * */
                    // Inserting Contacts
                    Log.d("Insert: ", "Inserting ..");
                    db.addTing(new Thing(newWhat.getText().toString(), newWhere.getText().toString()));

                    Log.d("Reading: ", "Reading all contacts..");
                    List<Thing> things = db.getAllThings();

                    for (Thing tn : things) {
                        String log = " Ting: " + tn.getWhat() + " ,Lokation: " + tn.getWhere();
                        // Writing Contacts to log
                        Log.d("Ting: ", log);
                    }
                    /*ContentValues values = new ContentValues();
                    values.put(TingleDBSchema.TingleTable.Cols.UUID, "x1");
                    values.put(TingleDBSchema.TingleTable.Cols.TING, newWhat.getText().toString());
                    values.put(TingleDBSchema.TingleTable.Cols.LOCATION, newWhere.getText().toString());

                    mDatabase.insert(TingleDBSchema.TingleTable.NAME,null,values);*/
                    //thingsDB.addThing(
                            //new Thing(newWhat.getText().toString(), newWhere.getText().toString()));

                    newWhat.setText("");
                    newWhere.setText("");
                    //updateUI();
                    ((TingleActivity)getActivity()).UpdateList();
                }
            }
        });
        // searchThing
        searchThing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Find entry");
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setMessage("Type in the entry item to find its location");

                // Set up the input
                final EditText input = new EditText(getActivity());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Thing t = null;
                        for (Thing d : thingsDB.getThingsDB()) {
                            if (d.getWhat() != null && d.getWhat().contains(input.getText().toString())) {
                                t = d;
                                break;
                            }
                        }

                        if (t != null)
                            Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(getActivity(),R.string.error_none_found, Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
        Listbtn = (Button) v.findViewById(R.id.Listbtn);
        if (Listbtn != null) {
            Listbtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), ListActivity.class));
                }
            });
        }
        return v;


    }


/*    private void updateUI() {
        int s = thingsDB.size();

        if (s > 0) {
            lastAdded.setText(thingsDB.get(s - 1).toString());
        }

    }*/


}
