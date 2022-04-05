package com.parcelhub.zebra_scanner;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.PluginRegistry;

public class ZebraScannerDelegate implements PluginRegistry.ActivityResultListener {

    private String intentAction = "com.parcelhubexpress.ACTION";

    private final Activity activity;
    private Context c;

    public ZebraScannerDelegate(Activity activity) {
        this.activity = activity;
        c = this.activity.getApplicationContext();

        this.createProfile();
        this.subscribeReceiver();
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return false;
    }


    public void createProfile()
    {

        ArrayList<Bundle> bundlePluginConfig = new ArrayList<>();
        Bundle bConfigWorkflow = new Bundle();
        Bundle bMain = new Bundle();
        bMain.putString("PROFILE_NAME","Parcelhub");
        bMain.putString("PROFILE_ENABLED", "true");
        bMain.putString("CONFIG_MODE","CREATE_IF_NOT_EXIST");
        bMain.putString("RESET_CONFIG", "true");

        /*###### Configurations for BARCODE [Start] ######*/
        Bundle bConfigBarcode = new Bundle();
        Bundle bParamsBarcode = new Bundle();
        bConfigBarcode.putString("PLUGIN_NAME","BARCODE");
        bConfigBarcode.putString("RESET_CONFIG", "true");
        bParamsBarcode.putString("scanner_selection","auto");
        bParamsBarcode.putInt("volume_slider_type",1 );
        bParamsBarcode.putString("decode_audio_feedback_uri", "Silent");
        bConfigBarcode.putBundle("PARAM_LIST", bParamsBarcode);
        bundlePluginConfig.add(bConfigBarcode);
        /*###### Configurations for BARCODE [Finish] ######*/

         /*###### Configurations for Intent Output [Start] ######*/
        Bundle bConfigIntent = new Bundle();
        Bundle bParamsIntent = new Bundle();
        bConfigIntent.putString("PLUGIN_NAME", "INTENT");
        bConfigIntent.putString("RESET_CONFIG", "true"); //Reset existing configurations of intent output plugin
        bParamsIntent.putString("intent_output_enabled", "true"); //Enable intent output plugin
        bParamsIntent.putString("intent_action", intentAction); //Set the intent action
        bParamsIntent.putString("intent_category", Intent.CATEGORY_DEFAULT); //Set a category for intent
        bParamsIntent.putInt("intent_delivery", 2); // Set intent delivery mechanism, Use "0" for Start Activity, "1" for Start Service, "2" for Broadcast, "3" for start foreground service
        bParamsIntent.putString("intent_use_content_provider", "false"); //Enable content provider
        bConfigIntent.putBundle("PARAM_LIST", bParamsIntent);
        bundlePluginConfig.add(bConfigIntent);
        /*###### Configurations for Intent Output [Finish] ######*/

        //Putting the INTENT and BARCODE plugin settings to the PLUGIN_CONFIG extra
        bMain.putParcelableArrayList("PLUGIN_CONFIG", bundlePluginConfig);

        Bundle bundleApp1 = new Bundle();
        bundleApp1.putString("PACKAGE_NAME", activity.getPackageName()); //Input from Flutter(?)
        bundleApp1.putStringArray("ACTIVITY_LIST", new String[]{
                activity.getPackageName() + '.' + activity.getLocalClassName()}); //Input from Flutter(?)


        bMain.putParcelableArray("APP_LIST", new Bundle[]{
                bundleApp1
        });

        Intent i = new Intent();
        i.setAction("com.symbol.datawedge.api.ACTION");
        i.putExtra("com.symbol.datawedge.api.SET_CONFIG", bMain);
        c.sendBroadcast(i);

    }

    public void subscribeReceiver()
    {
        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(intentAction);

        c.registerReceiver(myBroadcastReceiver, filter);
    }

    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle b = intent.getExtras();

            //  This is useful for debugging to verify the format of received intents from DataWedge
            //for (String key : b.keySet())
            //{
            //    Log.v(LOG_TAG, key);
            //}

            Log.d("zebra_scanner", action.toString());

            if (action.equals(intentAction)) {
                //  Received a barcode scan
                try {
                    String decodedSource = intent.getStringExtra("com.symbol.datawedge.source");
                    String decodedData = intent.getStringExtra("com.symbol.datawedge.data_string");
                    String decodedLabelType = intent.getStringExtra("com.symbol.datawedge.label_type");

                    Log.d("zebra_scanner", "source: " + decodedSource);
                    Log.d("zebra_scanner", "data: " + decodedData);
                    Log.d("zebra_scanner", "type: " + decodedLabelType);

                    HashMap<String, String> result = new HashMap<String, String>();

                    result.put("source", decodedSource);
                    result.put("data", decodedData);
                    result.put("type", decodedLabelType);


                    ZebraScannerPlugin.getInstance().onReceived(result);
                } catch (Exception e) {
                    //  Catch if the UI does not exist when we receive the broadcast
                    Log.d("zebra_scanner", e.toString());
                }
            }
        }
    };
}
