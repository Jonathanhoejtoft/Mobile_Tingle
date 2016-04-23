package com.itu.jonathan.tingle;

import android.app.*;
import android.app.ListFragment;
import android.content.ActivityNotFoundException;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itu.jonathan.tingle.database.TingleBaseHelper;
import com.itu.jonathan.tingle.database.TingleDBSchema;

import java.util.ArrayList;
import java.util.List;


public class TingleFragment extends Fragment {

    // GUI variables
    private Button ScannerQR;
    private Button ScannerBarcode;
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
    private ArrayAdapter arrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thingsDB = ThingsDB.get(this.getActivity());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_tingle,container,false);
        //lastAdded = (TextView) v.findViewById(R.id.last_thing);
        //updateUI();

        // Button
        addThing = (Button) v.findViewById(R.id.add_button);
        searchThing = (Button) v.findViewById(R.id.search_go_btn_button);
        //Listbtn = (Button) v.findViewById(R.id.Listbtn);

        // Textfields for describing a thing

        //newWhat = (TextView) v.findViewById(R.id.what_text);
        //newWhere = (TextView) v.findViewById(R.id.where_text);
        //searchT = (TextView) v.findViewById(R.id.search_text);

        // scanner code here
        ScannerQR = (Button)v.findViewById(R.id.scanner);
        ScannerBarcode = (Button)v.findViewById(R.id.scanner2);
        try {
            //ScannerQR = (Button)v.findViewById(R.id.scanner);

            ScannerQR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                    startActivityForResult(intent, 0);
                }

            });

            //ScannerBarcode = (Button)v.findViewById(R.id.scanner2);
            ScannerBarcode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
                    startActivityForResult(intent,0);
                }

            });

        } catch (ActivityNotFoundException anfe) {
            Log.e("onCreate", "Scanner Not Found", anfe);
        }
        // end scanner code

        // view products click event
        addThing.setOnClickListener(new View.OnClickListener() {

            /* add thing in dialog */
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add a thing");
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setMessage("Write the thing you want to add");

                Context context = getActivity();
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText inputTing = new EditText(context);
                inputTing.setHint("What thing?");
                layout.addView(inputTing);

                final EditText inputHvor = new EditText(context);
                inputHvor.setHint("Where");
                layout.addView(inputHvor);

/*                // Set up the input
                final EditText input = new EditText(getActivity());
                final EditText input = new EditText(getActivity());*/
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                //input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                builder.setView(layout);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TingleBaseHelper db = new TingleBaseHelper(getActivity());
                        try{
                            db.addTing(new Thing(inputTing.getText().toString(), inputHvor.getText().toString()));
                            Toast.makeText(getActivity(),"Added: " + inputTing.getText() + " to database", Toast.LENGTH_LONG).show();
                        }
                        catch (Exception ex){
                            Toast.makeText(getActivity(),"Failed to add!", Toast.LENGTH_LONG).show();
                        }
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

/*            @Override
            public void onClick(View view) {
                if ((newWhat.getText().length() > 0) && (newWhere.getText().length() > 0
                )) {

                    TingleBaseHelper db = new TingleBaseHelper(getActivity());

                    *//**
                     * CRUD Operations
                     * *//*
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
                    *//*ContentValues values = new ContentValues();
                    values.put(TingleDBSchema.TingleTable.Cols.UUID, "x1");
                    values.put(TingleDBSchema.TingleTable.Cols.TING, newWhat.getText().toString());
                    values.put(TingleDBSchema.TingleTable.Cols.LOCATION, newWhere.getText().toString());

                    mDatabase.insert(TingleDBSchema.TingleTable.NAME,null,values);*//*
                    //thingsDB.addThing(
                            //new Thing(newWhat.getText().toString(), newWhere.getText().toString()));
                    //arrayAdapter.notifyDataSetChanged();
                    newWhat.setText("");
                    newWhere.setText("");
                    //updateUI();

                            ((TingleActivity) getActivity()).UpdateList();
                }
            }*/
        });
        // searchThing
        searchThing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Find a thing");
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setMessage("Write the thing you want to find");

                // Set up the input
                final EditText input = new EditText(getActivity());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TingleBaseHelper db = new TingleBaseHelper(getActivity());

                        Thing t = null;
                        for (Thing d : db.getAllThings()) {
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
@Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == getActivity().RESULT_OK) {
               final String contents = intent.getStringExtra("SCAN_RESULT");
               final String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
// Handle successful scan
                //TingleBaseHelper db = new TingleBaseHelper(getActivity());

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Scanned Code");
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setMessage("Write the location");

                // Set up the input
                final EditText input = new EditText(getActivity());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                builder.setView(input);
                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TingleBaseHelper db = new TingleBaseHelper(getActivity());
                        Log.d("Insert: ", "Inserting ..");
                        db.addTing(new Thing(contents, input.getText().toString()));
                        Toast toast = Toast.makeText(getActivity(), "Scanned:" + contents + " Is here:" + input.getText().toString() , Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 25, 400);
                        toast.show();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                /**
                 * CRUD Operations
                 * */
                // Inserting Contacts
/*                Log.d("Insert: ", "Inserting ..");
                db.addTing(new Thing(contents, format));*/

                Toast toast = Toast.makeText(getActivity(), "Content:" + contents + " Format:" + format , Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 400);
                toast.show();
            } else if (resultCode == getActivity().RESULT_CANCELED) {
// Handle cancel
                Toast toast = Toast.makeText(getActivity(), "Scan was Cancelled!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 400);
                toast.show();
            }
        }
    }

/*    private void updateUI() {
        int s = thingsDB.size();

        if (s > 0) {
            lastAdded.setText(thingsDB.get(s - 1).toString());
        }

    }*/


}
