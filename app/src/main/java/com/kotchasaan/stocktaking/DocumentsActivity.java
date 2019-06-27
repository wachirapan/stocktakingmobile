package com.kotchasaan.stocktaking;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.kotchasaan.stocktaking.SQLite.InsertData;
import com.kotchasaan.stocktaking.util.HttpHandler;
import com.kotchasaan.stocktaking.util.UserHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DocumentsActivity extends AppCompatActivity {
    private String TAG = DocumentsActivity.class.getSimpleName();
    String IDTag = "";
    private ListView docstListView;

    ArrayList<HashMap<String, String>> docsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);
        getSupportActionBar().hide();

        docsList = new ArrayList<>();
        docstListView = (ListView) findViewById(R.id.docstListView);

        new GetDocuments().execute();
    }
    private class GetDocuments extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(DocumentsActivity.this,"Data is downloading",Toast.LENGTH_LONG).show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "http://192.168.1.120:8080/warehousemgr/control/getWarehouseCountDraftAll?" +
                    "&login.username=oposs" +
                    "&login.password=ofbiz";
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray docs = jsonObj.getJSONArray("warehouseCountList");

                    // looping through All Contacts
                    for (int i = 0; i < docs.length(); i++) {
                        JSONObject c = docs.getJSONObject(i);
                        String id = c.getString("stockCountId");

                        IDTag = c.getString("stockCountId");

                        JSONObject createdDate = c.getJSONObject("createdDate");
                        long time = createdDate.getLong("time");
                        Date d = new Date(time );
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String date = sdf.format(d);


                        String status = c.getString("statusId");


                        // tmp hash map for single contact
                        HashMap<String, String> doc = new HashMap<>();

                        // adding each child node to HashMap key => value
                        doc.put("id", id);
                        doc.put("date", date.toString());
                        doc.put("status", status);

                        // adding contact to contact list
                        docsList.add(doc);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ListAdapter adapter = new SimpleAdapter(DocumentsActivity.this, docsList,
                    R.layout.docs_list_item, new String[]{ "id","date","status"},
                    new int[]{R.id.id, R.id.date,R.id.status});
            docstListView.setAdapter(adapter);
            docstListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Map<String, Object> map = (Map<String, Object>)docstListView.getItemAtPosition(position);
                    String _id = (String) map.get("id");
                    String _date = (String) map.get("date");
                    //Double _status = (Double) map.get("status");
                    Intent myIntent = new Intent(DocumentsActivity.this, StockTakingActivity.class);

                    UserHelper userHelper = new UserHelper(DocumentsActivity.this);
                    userHelper.createSession(IDTag);

                    startActivity(myIntent);
                }
            });
        }
    }
}
