package com.itu.jonathan.tingle;

import android.app.*;
import android.app.ListFragment;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.itu.jonathan.tingle.database.TingleBaseHelper;
import com.itu.jonathan.tingle.database.TingleDBSchema;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;



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
    public String scanResult;
    public int scanstatus = 0;
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
                        try {
                            db.addTing(new Thing(inputTing.getText().toString(), inputHvor.getText().toString()));
                            Toast.makeText(getActivity(), "Added: " + inputTing.getText() + " to database", Toast.LENGTH_LONG).show();
                            TingleActivity upd = new TingleActivity();
                            upd.UpdateList();
                        } catch (Exception ex) {
                            Toast.makeText(getActivity(), "Failed to add!", Toast.LENGTH_LONG).show();
                        }

                        //ListView list = (ListView) getActivity().findViewById(R.id.list_item);
//                        ((BaseAdapter)list.getAdapter()).notifyDataSetChanged();



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

    public EditText input;
    public EditText input2;
    public EditText inputqr;
    public String ScanType;


    int carljo = 0;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == getActivity().RESULT_OK) {

               final String contents = intent.getStringExtra("SCAN_RESULT");
               final String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                String string1 = "QR_CODE";
                String string2 = intent.getStringExtra("SCAN_RESULT_FORMAT");
                scanResult = contents;

// Handle successful scan



                if(format != null) {


                    //TingleBaseHelper db = new TingleBaseHelper(getActivity());
                    ScanType = format.toLowerCase();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Scanned Code:" + ScanType);
                    builder.setIcon(android.R.drawable.ic_dialog_info);
                    builder.setMessage("Write location: " + scanResult);
String checkval = "null";
                    if (scanResult != null) {
                        Toast toast = Toast.makeText(getActivity(), "scanned " + scanResult + ":id::" + scanstatus, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 25, 400);
                        toast.show();

                            LinearLayout layout = new LinearLayout(getActivity());
                            layout.setOrientation(LinearLayout.VERTICAL);
                        if(string1.compareTo(string2) == 0){
                            carljo = 1; // qr
                            input = new EditText(getActivity());
                            input.setHint("What thing?");
                            layout.addView(input);
                        }
                        else{
                            carljo = 2;
                            input = new EditText(getActivity());
                            input.setHint("What thing?");
                            layout.addView(input);

                            input2 = new EditText(getActivity());
                            input2.setHint("Where");
                            layout.addView(input2);
                        }

                            builder.setView(layout);

                        Toast toast1 = Toast.makeText(getActivity(), "Scanned Barcode: " + scanResult+ "id:" +carljo , Toast.LENGTH_LONG);
                        toast1.setGravity(Gravity.TOP, 25, 400);
                        toast1.show();




                    }
                     /*else if(carljo ==1) {
                        Toast toast = Toast.makeText(getActivity(), "Scanned QR: " + scanResult +carljo , Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 25, 400);
                        toast.show();

                    }*/
                    else {
                        Toast toast = Toast.makeText(getActivity(), "Scanned Barcode: " + scanResult +carljo , Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 25, 400);
                        toast.show();

                    }

                    // Set up the input


                    // excecute outpan lookup for barcode

                    String stringUrl = "https://api.outpan.com/v2/products/" + contents + "?apikey=ce347520123d90a02979d155e6d5e6c5";

                    ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected() && carljo ==2 ) {
                        new DownloadWebpageTask().execute(stringUrl);

                    } else {
                        Toast toast = Toast.makeText(getActivity(), "No network connection available", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 25, 400);
                        toast.show();
                    }

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            builder.setMessage("");
                            TingleBaseHelper db = new TingleBaseHelper(getActivity());
                            Log.d("Insert: ", "Inserting ..");

                            if(carljo ==1) {
                                db.addTing(new Thing(contents, input.getText().toString()));
                            }
                            else if(carljo ==2){
                                String temp1 = "null";
                                if (scanResult.compareTo(temp1) == 0) {

                                    String param = input.getText().toString();
                                    ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
                                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                                    if (networkInfo != null && networkInfo.isConnected()) {
                                        new PostnewItem().execute("https://api.outpan.com/v2/products/" + contents + "/name?apikey=ce347520123d90a02979d155e6d5e6c5", param);
                                    }
                                    db.addTing(new Thing(input.getText().toString(), input2.getText().toString()));
                                } else {
                                    db.addTing(new Thing(scanResult, input.getText().toString()));

                                }
                            }



                            Toast toast = Toast.makeText(getActivity(), "Scanned:" + scanResult + " Is here:" + input.getText().toString(), Toast.LENGTH_LONG);
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

                    //Toast toast = Toast.makeText(getActivity(), "Content:" + contents + " Format:" + format , Toast.LENGTH_LONG);
                    //toast.setGravity(Gravity.TOP, 25, 400);
                    // toast.show();
                }
                else{
                    Toast toast = Toast.makeText(getActivity(), "simulated qr " + scanResult, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 25, 400);
                    toast.show();
                }

                // end code here
            } else if (resultCode == getActivity().RESULT_CANCELED) {
// Handle cancel
                Toast toast = Toast.makeText(getActivity(), "Scan was Cancelled!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 400);
                toast.show();
            }
        }
    }

    private String sendPOST(String POST_URL, String POST_PARAMS) throws IOException {
        URL obj = new URL(POST_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        String POST_ARGS = "name="+POST_PARAMS;
        // For POST only - START
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        os.write(POST_ARGS.getBytes());
        os.flush();
        os.close();
        // For POST only - END

        int responseCode = con.getResponseCode();
       String resultat = null;
        System.out.println("POST Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) { //success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
            resultat = response.toString();
            return resultat;
        } else {
            System.out.println("POST request not worked");

        }
return resultat;
    }

private String getInfo(String myurl) throws IOException {
    InputStream is = null;
    // Only display the first 500 characters of the retrieved
    // web page content.
    int len = 500;

    try {
        URL url = new URL(myurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        int response = conn.getResponseCode();
        Log.d("HttpExample", "The Get response is: " + response);
        is = conn.getInputStream();

        // Convert the InputStream into a string
        String contentAsString =  readIt(is, len);
        Log.d("contentAsString: ",contentAsString);
        //JSON parser
        try {
            JSONObject jObject = new JSONObject(contentAsString);
            String temp = "null";
            if (jObject.getString("name").equals(temp)) {
                scanResult = "null";
                scanstatus = 0;
            } else {
                scanResult = jObject.getString("name");
                scanstatus = 1;
            }
        }
        catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return contentAsString;

        // Makes sure that the InputStream is closed after the app is
        // finished using it.
    } finally {
        if (is != null) {
            is.close();
        }
    }
}

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return getInfo(urls[0]);
                //return postInfo(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            try {
                JSONObject jObject = new JSONObject(result);
                String temp = jObject.getString("name");

                if(temp.contains("null")){
                    Toast toast = Toast.makeText(getActivity(), "Scan result: NULL" + scanResult + " result:" + temp, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 25, 400);
                    toast.show();
                    scanstatus = 0;
                }
                else{
                    Toast toast = Toast.makeText(getActivity(), "Scan sucess:" + scanResult + " result:" + temp, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 25, 400);
                    toast.show();
                    scanstatus = 1;
                }



            }
            catch (JSONException e) {
                throw new RuntimeException(e);
            }






        }
    }
    private class PostnewItem extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return sendPOST(urls[0],urls[1]);
                //return postInfo(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {


            Toast toast = Toast.makeText(getActivity(), "Result:" + result, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 400);
            toast.show();

        }
    }


}
