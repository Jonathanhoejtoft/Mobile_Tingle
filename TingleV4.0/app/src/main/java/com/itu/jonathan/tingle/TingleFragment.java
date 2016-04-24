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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thingsDB = ThingsDB.get(this.getActivity());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        //sendPOST("https://api.outpan.com/v2/products/[GTIN]/name?apikey=[YOUR API KEY]")

/*        RequestQueue MyRequestQueue = Volley.newRequestQueue(getActivity());
        String url = "http://yourdomain.com/path";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("Field", "Value"); //Add the data you'd like to send to the server.
                return MyData;
            }
        };
        MyRequestQueue.add(MyStringRequest);*/

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
    public EditText input;
    public EditText input2;
@Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == getActivity().RESULT_OK) {
               final String contents = intent.getStringExtra("SCAN_RESULT");
               final String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
// Handle successful scan
                //TingleBaseHelper db = new TingleBaseHelper(getActivity());

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Scanned Code");
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setMessage("Write location: ");



                if(scanResult == null){
                    Toast toast = Toast.makeText(getActivity(), "Not found or scan failure: " + scanResult, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 25, 400);
                    toast.show();
                    LinearLayout layout = new LinearLayout(getActivity());
                    layout.setOrientation(LinearLayout.VERTICAL);

                    input = new EditText(getActivity());
                    input.setHint("What thing?");
                    layout.addView(input);

                    input2 = new EditText(getActivity());
                    input2.setHint("Where");
                    layout.addView(input2);

                    builder.setView(layout);

                }
                else{
                    Toast toast = Toast.makeText(getActivity(), "Scanned item: " + scanResult, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 25, 400);
                    toast.show();

                    input = new EditText(getActivity());
                    input.setHint("What thing?");

                    builder.setView(input);

                }
                // Set up the input


                // excecute outpan lookup for barcode

                String stringUrl = "https://api.outpan.com/v2/products/"+contents+"?apikey=ce347520123d90a02979d155e6d5e6c5";
                //String stringUrlPost = "https://api.outpan.com/v2/products/"+contents+"/"+input.getText().toString()+"?apikey=ce347520123d90a02979d155e6d5e6c5";
                ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
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
                        if(scanResult == null){

                            String param = input.getText().toString();
                            ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
                            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                            if (networkInfo != null && networkInfo.isConnected()) {
                                new PostnewItem().execute("https://api.outpan.com/v2/products/" + contents + "/name?apikey=ce347520123d90a02979d155e6d5e6c5", param);
                            }
                            db.addTing(new Thing(input.getText().toString(), input2.getText().toString()));
                        }
                        else {
                            db.addTing(new Thing(scanResult, input.getText().toString()));
                        }

                        Toast toast = Toast.makeText(getActivity(), "Scanned:" + scanResult + " Is here:" + input.getText().toString()  , Toast.LENGTH_LONG);
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
               // toast.show();
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
            if (jObject.getString("name") == "null") {
                scanResult = null;
            } else {
                scanResult = jObject.getString("name");
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
    private String postInfo(String myurl) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("HttpExample", "The Post response is: " + response);

            return response + "ex";
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        }
        catch (Exception ex){

        }
        return "sucess";
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
            String aJsonString= null;
/*            scanResult = null;
            //JSON parser
            try {
                JSONObject jObject = new JSONObject(result);
                if(jObject.getString("name") == "null"){
                    scanResult = null;
                }
                else{
                    scanResult = jObject.getString("name");
                }


            } catch (JSONException e) {
                throw new RuntimeException(e);
            }*/




/*            Toast toast = Toast.makeText(getActivity(), "Result:" + scanResult, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 400);
            toast.show();*/

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
