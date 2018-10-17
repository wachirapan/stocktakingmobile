package com.kotchasaan.stocktaking;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.symbol.emdk.EMDKManager;

public class StockTakingActivity extends AppCompatActivity {

    private String TAG = "StockTakingActivity.class";

    //Declare a variable to store the textViewBarcode
    private TextView textViewBarcode = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_taking);

        //Get the textViewBarcode
        textViewBarcode = (TextView) findViewById(R.id.textViewBarcode);

        //In case we have been launched by the DataWedge intent plug-in
        Intent i = getIntent();
        handleDecodeData(i);
    }

    //We need to handle any incoming intents, so let override the onNewIntent method
    @Override
    public void onNewIntent(Intent i) {
        handleDecodeData(i);

    }

    //This function is responsible for getting the data from the intent
    private void handleDecodeData(Intent i) {
        Log.i(TAG,"++++++++++++++++++++++++++++++++++++++++++++++Intent get action:"+i.getAction());
        if (i.getAction()!=null) {
            if (i.getAction().contentEquals("com.kotchasaan.stocktaking.RECVRST") ) {
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
        //Check the intent action is for us
        Log.i(TAG,"++++++++++++++++++++++++++++++++++++++++++++++Intent get action:"+i.getAction());

    }
}
