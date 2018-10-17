package com.kotchasaan.stocktaking;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.symbol.emdk.*;
import com.symbol.emdk.EMDKManager.EMDKListener;

import android.content.Intent;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends AppCompatActivity implements EMDKListener{

    //Assign the profile name used in EMDKConfig.xml
    private String profileName = "MainDataCapture";
    //Assign the profile name used in EMDKConfig.xml  for MSR handling
    private String profileNameStockTaking = "StockTakingDataCapture";

    //Declare a variable to store ProfileManager object
    private ProfileManager mProfileManager = null;

    //Declare a variable to store EMDKManager object
    private EMDKManager emdkManager = null;

    //Declare a variable to store the textViewBarcode
    private TextView textViewBarcode = null;

    //Declare a variable to store the buttonMSR
    private Button buttonStockTaking = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //The EMDKManager object will be created and returned in the callback.
        EMDKResults results = EMDKManager.getEMDKManager(getApplicationContext(), this);

        //Check the return status of getEMDKManager
        if(results.statusCode == EMDKResults.STATUS_CODE.FAILURE)
        {
            //Failed to create EMDKManager object

        }



        //Get the textViewBarcode
        textViewBarcode = (TextView) findViewById(R.id.textViewBarcode);

        buttonStockTaking = (Button) findViewById(R.id.buttonStockTaking);
        //Add an OnClickListener for buttonMSR
        buttonStockTaking.setOnClickListener(buttonStockTakingOnClick);
        //In case we have been launched by the DataWedge intent plug-in
        Intent i = getIntent();
        handleDecodeData(i);

    }

    //OnClickListener for buttonMSR
    private OnClickListener buttonStockTakingOnClick = new OnClickListener() {
        public void onClick(View v) {
            //Launch StockTakingActivity
            Intent myIntent = new Intent(MainActivity.this, StockTakingActivity.class);
            //startActivity(new Intent(MainActivity.this, StockTakingActivity.class));

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
                    textViewBarcode.setText("Data = " + data);
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

                //Call processPrfoile for profile MSR
                results = mProfileManager.processProfile(profileNameStockTaking, ProfileManager.PROFILE_FLAG.SET, modifyData);

                if(results.statusCode == EMDKResults.STATUS_CODE.FAILURE)
                {
                    //Failed to set profile MSR
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