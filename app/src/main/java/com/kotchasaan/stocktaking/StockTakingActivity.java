package com.kotchasaan.stocktaking;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kotchasaan.stocktaking.ListData.StockTakingList;
import com.kotchasaan.stocktaking.SQLite.InsertData;
import com.kotchasaan.stocktaking.SQLite.QueryData;
import com.kotchasaan.stocktaking.util.StockTake;
import com.kotchasaan.stocktaking.util.UserHelper;
import com.symbol.emdk.EMDKManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StockTakingActivity extends AppCompatActivity implements EMDKManager.EMDKListener {

    private String TAG = "StockTakingActivity.class";
    ListView list_adapter;
    ArrayList<StockTake> mlist;

    JSONObject jobj;
    JSONArray arr = new JSONArray();
    //Declare a variable to store the textViewBarcode
//    private TextView textViewBarcode = null;
    EditText editTextBarcode;
    Button on_submit, btn_senddata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_taking);
        getSupportActionBar().hide();
        //Get the textViewBarcode
//        textViewBarcode = (TextView) findViewById(R.id.textViewBarcode);
        editTextBarcode = (EditText) findViewById(R.id.editTextBarcode);
        on_submit = (Button) findViewById(R.id.on_submit);
        on_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsertData insertData = new InsertData(StockTakingActivity.this);
                UserHelper userHelper = new UserHelper(StockTakingActivity.this);
                insertData.InsertCountProdudct(editTextBarcode.getText().toString(), userHelper.getUserName());
                editTextBarcode.setText("");
                finish();
                startActivity(getIntent());
            }
        });
        btn_senddata = (Button) findViewById(R.id.btn_senddata);

        //In case we have been launched by the DataWedge intent plug-in
        Intent i = getIntent();

        list_adapter = (ListView) findViewById(R.id.list_adapter);
        mlist = new ArrayList<>();
        QueryData queryData = new QueryData(this);

        Cursor cursor = queryData.create_datastock();
        while (cursor.moveToNext()) {
            jobj = new JSONObject();
            try {
                jobj.put("Id", cursor.getString(0));
                jobj.put("TagDoc", cursor.getString(1));
                jobj.put("ProductCode", cursor.getString(2));
                jobj.put("Total", cursor.getString(3));
                jobj.put("Date", cursor.getString(4));
                arr.put(jobj);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mlist.add(new StockTake(cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4)));
        }
        jobj = new JSONObject();
        try {
            jobj.put("CouterStock", arr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        list_adapter.setAdapter(new StockTakingList(StockTakingActivity.this, mlist));

        btn_senddata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CHECK", "***************" + jobj);
                RequestQueue requestQueue = Volley.newRequestQueue(StockTakingActivity.this);
                StringRequest request = new StringRequest(Request.Method.POST, "", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i("My success", "" + response);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                        Log.i("My error", "" + error);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("DataCount", jobj.toString());
                        return map;
                    }
                };
                requestQueue.add(request);
            }
        });
        handleDecodeData(i);
    }

    //We need to handle any incoming intents, so let override the onNewIntent method
    @Override
    public void onNewIntent(Intent i) {
        handleDecodeData(i);

    }

    //This function is responsible for getting the data from the intent
    private void handleDecodeData(Intent i) {
//        Log.i(TAG,"++++++++++++++++++++++++++++++++++++++++++++++Intent get action:"+i.getAction());
        if (i.getAction() != null) {
            if (i.getAction().contentEquals("com.kotchasaan.stocktaking.RECVRST")) {
                //Get the source of the data
                String source = i.getStringExtra("com.motorolasolutions.emdk.datawedge.source");

                //Check if the data has come from the Barcode scanner
                if (source.equalsIgnoreCase("scanner")) {
                    //Get the data from the intent
                    String data = i.getStringExtra("com.motorolasolutions.emdk.datawedge.data_string");

                    //Check that we have received data
                    if (data != null && data.length() > 0) {
                        //Display the data to the text view
//                        textViewBarcode.setText("Data = " + data);
                        editTextBarcode.setText("");
                        InsertData insertData = new InsertData(this);
                        UserHelper userHelper = new UserHelper(this);
                        insertData.InsertCountProdudct(data, userHelper.getUserName());

                        finish();
                        startActivity(getIntent());
                    }

                }
            }
        }
        //Check the intent action is for us
//        Log.i(TAG,"++++++++++++++++++++++++++++++++++++++++++++++Intent get action:"+i.getAction());

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //Clean up the objects created by EMDK manager

    }

    @Override
    public void onOpened(EMDKManager emdkManager) {

    }

    @Override
    public void onClosed() {

    }
}
