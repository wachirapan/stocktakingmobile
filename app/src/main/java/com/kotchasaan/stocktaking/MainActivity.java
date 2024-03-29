package com.kotchasaan.stocktaking;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kotchasaan.stocktaking.SQLite.SQLdb;
import com.kotchasaan.stocktaking.util.HttpHandler;
import com.symbol.emdk.*;
import com.symbol.emdk.EMDKManager.EMDKListener;

import android.content.Intent;
import android.util.Log;

import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements EMDKListener{

    private String TAG = MainActivity.class.getSimpleName();

    //Assign the profile name used in EMDKConfig.xml
    private String profileName = "MainDataCapture";

    //Declare a variable to store ProfileManager object
    private ProfileManager mProfileManager = null;

    //Declare a variable to store EMDKManager object
    private EMDKManager emdkManager = null;

    //Declare a variable to store the textViewBarcode

    //Declare a variable to store the buttonMSR
    private Button buttonStockTaking = null;

    private TextView textViewProductId = null;
    private TextView textViewIdValue = null;
    private TextView textViewInternalName = null;
    private TextView textViewQoh = null;
    private TextView textViewSalePrice = null;
    private TextView textViewPurchasePrice = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        //create sqlite
        SQLdb db = new SQLdb(this);
        db.getWritableDatabase();
        //The EMDKManager object will be created and returned in the callback.
        EMDKResults results = EMDKManager.getEMDKManager(getApplicationContext(), this);

        //Check the return status of getEMDKManager
        if(results.statusCode == EMDKResults.STATUS_CODE.FAILURE)
        {
            //Failed to create EMDKManager object

        }

        buttonStockTaking = (Button) findViewById(R.id.buttonStockTaking);
        //Add an OnClickListener for buttonMSR
        buttonStockTaking.setOnClickListener(buttonStockTakingOnClick);

        textViewProductId = (TextView) findViewById(R.id.textViewProductId);
        textViewIdValue = (TextView) findViewById(R.id.textViewIdValue);
        textViewInternalName = (TextView) findViewById(R.id.textViewInternalName);
        textViewQoh = (TextView) findViewById(R.id.textViewQoh);
        textViewSalePrice = (TextView) findViewById(R.id.textViewSalePrice);
        textViewPurchasePrice = (TextView) findViewById(R.id.textViewPurchasePrice);

        //In case we have been launched by the DataWedge intent plug-in
        Intent i = getIntent();
        handleDecodeData(i);

    }

    private class GetProductInfo extends AsyncTask<Void, Void, Void> {
        private String pData = null;
        private Map productInfo =  new HashMap();
        public GetProductInfo(String data) {
            this.pData = data;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this,"Product Data is downloading",Toast.LENGTH_LONG).show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "http://192.168.1.120:8080/warehousemgr/control/getProductInformationStock?" +
                    "idValue="+this.pData +
                    "&productId="+this.pData+
                    "&login.username=oposs" +
                    "&login.password=ofbiz";
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    productInfo.put("productId", jsonObj.getString("productId"));
                    productInfo.put("idValue", jsonObj.getString("idValue"));
                    productInfo.put("internalName", jsonObj.getString("internalName"));
                    productInfo.put("productPriceList", jsonObj.getJSONArray("productPriceList"));
                    productInfo.put("qoh", jsonObj.getString("qoh"));
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
                Log.e(TAG, "Couldn't get Product from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get Product from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            textViewProductId.setText("รหัสสินค้า: "+productInfo.get("productId").toString());
            textViewIdValue.setText("บาร์โค้ด: "+productInfo.get("idValue").toString());
            textViewInternalName.setText("ชื่อสินค้า: "+productInfo.get("internalName").toString());
            textViewQoh.setText("สินค้าคงเหลือ: "+productInfo.get("qoh").toString());
            JSONArray productPriceList = (JSONArray) productInfo.get("productPriceList");
            for (int i = 0; i < productPriceList.length(); i++) {
                try {
                    JSONObject c = productPriceList.getJSONObject(i);
                    if ("DEFAULT_PRICE".equals(c.getString("productPriceTypeId"))) {
                        textViewSalePrice.setText("ราคาขาย: "+ c.getString("price"));
                    }
                    if ("PROMO_PRICE".equals(c.getString("productPriceTypeId"))) {
                        textViewPurchasePrice.setText("ราคาทุน: "+ c.getString("price"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            /*
            ListAdapter adapter = new SimpleAdapter(MainActivity.this, contactList,
                    R.layout.list_item, new String[]{ "email","mobile"},
                    new int[]{R.id.email, R.id.mobile});
            lv.setAdapter(adapter);
            */
        }
    }

    //OnClickListener for buttonMSR
    private OnClickListener buttonStockTakingOnClick = new OnClickListener() {
        public void onClick(View v) {
            //Launch DocumentsActivity
            Intent myIntent = new Intent(MainActivity.this, DocumentsActivity.class);
            startActivity(myIntent);
        }
    };

    //We need to handle any incoming intents, so let override the onNewIntent method
    @Override
    public void onNewIntent(Intent i) {
        handleDecodeData(i);

    }

    //This function is responsible for getting the data from the intent
    private void handleDecodeData(Intent i) {
        //Check the intent action is for us
        if (i.getAction().contentEquals("com.kotchasaan.stocktaking.RECVR") ) {
            //Get the source of the data
            String source = i.getStringExtra("com.motorolasolutions.emdk.datawedge.source");

            //Check if the data has come from the Barcode scanner
            if(source.equalsIgnoreCase("scanner")) {
                //Get the data from the intent
                String data = i.getStringExtra("com.motorolasolutions.emdk.datawedge.data_string");

                //Check that we have received data
                if(data != null && data.length() > 0) {
                    //Display the data to the text view
                    //textViewBarcode.setText("Data = " + data);
                    Log.e(TAG, "=========================================================1.Response from url: " + data);
                    new GetProductInfo(data).execute();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //Clean up the objects created by EMDK manager
        emdkManager.release();
    }

    @Override
    public void onOpened(EMDKManager emdkManager) {
        this.emdkManager = emdkManager;
        //Get the ProfileManager object to process the profiles
        mProfileManager = (ProfileManager) emdkManager.getInstance(EMDKManager.FEATURE_TYPE.PROFILE);
        if(mProfileManager != null)
        {
            try{

                String[] modifyData = new String[1];
                //Call processPrfoile with profile name and SET flag to create the profile. The modifyData can be null.

                EMDKResults results = mProfileManager.processProfile(profileName, ProfileManager.PROFILE_FLAG.SET, modifyData);
                if(results.statusCode == EMDKResults.STATUS_CODE.FAILURE)
                {
                    //Failed to set profile
                }
            }catch (Exception ex){
                // Handle any exception
            }


        }
    }

    @Override
    public void onClosed() {

    }
}