package com.parcelhub.zebra_scanner;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import io.flutter.plugin.common.PluginRegistry;

public class ZebraScannerDelegate implements PluginRegistry.ActivityResultListener {

    private String intentAction = "com.parcelhubexpress.ACTION";

    private final Activity activity;
    private Context c;

    public ZebraScannerDelegate(Activity activity) {
        this.activity = activity;
        c = this.activity.getApplicationContext();

        this.createProfile();
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

        Bundle paramList = new Bundle();
//        paramList.putString("workflow_name","license_plate");
//        paramList.putString("workflow_input_source","2");
//
//        Bundle paramSetContainerDecoderModule = new Bundle();
//        paramSetContainerDecoderModule.putString("module","LicenseDecoderModule");
//        Bundle moduleContainerDecoderModule = new Bundle();
//        moduleContainerDecoderModule.putString("session_timeout", "15000");
//        moduleContainerDecoderModule.putString("output_image", "full");
//        moduleContainerDecoderModule.putString("scanMode", "unitedstates"); //unitedstates, auto
//        paramSetContainerDecoderModule.putBundle("module_params",moduleContainerDecoderModule);

//        Bundle paramSetCameraModule = new Bundle();
//        paramSetCameraModule.putString("module","CameraModule");
//        Bundle moduleCameraModule = new Bundle();
//        moduleCameraModule.putString("illumination", "on");
//        paramSetCameraModule.putBundle("module_params",moduleCameraModule);

//        Bundle paramSetFeedbackModule = new Bundle();
//        paramSetFeedbackModule.putString("module","FeedbackModule");
//        Bundle moduleParamsFeedback = new Bundle();
//        moduleParamsFeedback.putString("decode_haptic_feedback", "true");
//        moduleParamsFeedback.putString("decode_audio_feedback_uri", "none");
//        moduleParamsFeedback.putString("volume_slider_type", "2");// 0- Ringer, 1- Music and Media, 2-Alarms, 3- Notification
//        moduleParamsFeedback.putString("decoding_led_feedback", "false");
//        paramSetFeedbackModule.putBundle("module_params",moduleParamsFeedback);

//        ArrayList<Bundle> paramSetList = new ArrayList<>();
//        paramSetList.add(paramSetContainerDecoderModule);
//        paramSetList.add(paramSetFeedbackModule);
//        paramSetList.add(paramSetCameraModule);

//        paramList.putParcelableArrayList("workflow_params", paramSetList);

//        ArrayList<Bundle> workFlowList = new ArrayList<>();
//        workFlowList.add(paramList);
//
//        bConfigWorkflow.putParcelableArrayList("PARAM_LIST", workFlowList);
//        bundlePluginConfig.add(bConfigWorkflow);

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
                    String decodedData = intent.getStringExtra("com.symbol.datawedge.data_string");
                      Log.d("zebra_scanner", "data: " + decodedData);
                    ZebraScannerPlugin.getInstance().onReceived(decodedData);
                } catch (Exception e) {
                    //  Catch if the UI does not exist when we receive the broadcast
                }
            }
        }
    };
}
