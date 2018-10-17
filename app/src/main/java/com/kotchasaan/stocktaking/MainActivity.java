package com.kotchasaan.stocktaking;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.symbol.emdk.*;
import com.symbol.emdk.EMDKManager.EMDKListener;

public class MainActivity extends AppCompatActivity implements EMDKListener{

    //Assign the profile name used in EMDKConfig.xml
    private String profileName = "MainDataCapture";

    //Declare a variable to store ProfileManager object
    private ProfileManager mProfileManager = null;

    //Declare a variable to store EMDKManager object
    private EMDKManager emdkManager = null;

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