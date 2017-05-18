package zz.aimsicd.lite.rflog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "AIMSICD";
    private static final String mTAG = "Main: ";

    // For displayTelephonyInfo()
    private static final int INFO_DEVICE_INFO_INDEX = 7;
    /*private static final int[] info_ids= {
            R.id.serviceState_info,
            R.id.cellLocation_info,
            R.id.callState_info,
            R.id.connectionState_info,
            R.id.signalLevel,
            R.id.signalLevelInfo,
            R.id.dataDirection,
            R.id.device_info
    };*/

    // Permission "Constants"
    private static final int PERMISSION_REQUEST = 100;

    private static final String[] PERMISSIONS = {
        Manifest.permission.READ_PHONE_STATE,           // PHONE
        Manifest.permission.ACCESS_COARSE_LOCATION,     // LOCATION
        Manifest.permission.ACCESS_FINE_LOCATION,       // LOCATION
        Manifest.permission.ACCESS_NETWORK_STATE,       //
        Manifest.permission.READ_SMS,                   // SMS
        Manifest.permission.WRITE_EXTERNAL_STORAGE      // STORAGE
    };

    // Attributes
    private TelephonyManager mTM;

    // Sets the textview contents
    private void setTextViewText(int id,String text) {
        ((TextView)findViewById(id)).setText(text);
    }


    //===================================================================================
    //  Activity Life Cycle
    //===================================================================================
    // Because of the way lifecycle events are propagated, not all of them are necessary.
    // See: https://i.stack.imgur.com/LXnx7.png

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, mTAG + "onCreate: ------->> START");
        setContentView(R.layout.activity_main);
        //setContentView(R.layout.phonestatus);
        checkPermissions();
        displayTelephonyInfo(); // only shown once...
        //startRFListener();
    }

    // not necessary
    @Override
    protected void onStart() {  // <-- also via onRestart() when activity come into foreground
        super.onStart();
        Log.i(TAG, mTAG + "onStart event: do nothing");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, mTAG + "onResume event: startRFListener");
        //subscribes to the telephony related events
        startRFListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, mTAG + "onPause event: stopRFListener since we have no UI");
        //Stop listening to the telephony events
        stopRFListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, mTAG + "onStop event: stopRFListener");
        //Stop listening to the telephony events
        stopRFListener();
    }

    // not necessary
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, mTAG + "onDestroy: ------->> STOP");
    }

    //===================================================================================
    //  Service Life-cycle:
    //===================================================================================
    // https://developer.android.com/reference/android/app/Service.html#ServiceLifecycle
    // https://developer.android.com/images/service_lifecycle.png
    // We follow:  https://www.tutorialspoint.com/android/android_services.htm

    public void startService(View view) {
        Log.i(TAG, mTAG + "startService: RfApiSvc");
        startService(new Intent(getBaseContext(), RfApiSvc.class));
    }

    // Method to stop the service
    public void stopService(View view) {
        Log.i(TAG, mTAG + "stopService: RfApiSvc");
        stopService(new Intent(getBaseContext(), RfApiSvc.class));
    }



    //===================================================================================
    //  Permissions
    //===================================================================================

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST: {
                isPermissionGranted(grantResults);
            }
        }
    }

    private void isPermissionGranted(int[] grantResults) {
        if (grantResults.length > 0) {
            Boolean permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (permissionGranted) {
                startRFListener();
            } else {
                PermissionUtils.alertAndFinish(this);
            }
        }
    }

    private void checkPermissions() {
        // Implement the new AOS permission system for API >= 23 (M)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i(TAG, mTAG + "Permission check: Yes, we are using API > 22(L)...");
            Boolean canWriteExternalStorage = PermissionUtils.canReadPhoneState(this);
            Boolean canReadExternalStorage = PermissionUtils.canAccessCoarseLocation(this);
            if (!canWriteExternalStorage || !canReadExternalStorage) {
                requestPermissions(PERMISSIONS, PERMISSION_REQUEST);
            } else {
                // Permission was granted.
                Log.i(TAG, mTAG + "Permission granted!");
                startRFListener();
            }
        } else {
            // API < 23 doesn't use these permissions
            Log.i(TAG, mTAG + "Permission check: No, we are using API < 23(M)");
            startRFListener();
        }
    }

    //===================================================================================
    //  Listeners
    //===================================================================================

    /**
     * NOTES:
     *          [1] https://developer.android.com/reference/android/telephony/PhoneStateListener.html#LISTEN_CELL_LOCATION
     *          [2]
     * ToDo: Cleanup of text below
     ===================================================================================

     LISTEN_CALL_FORWARDING_INDICATOR   : Listen for changes to the call-forwarding indicator.
     LISTEN_CALL_STATE                  : Listen for changes to the device call state.

        CALL_STATE_IDLE                 Device call state: No activity.
        CALL_STATE_OFFHOOK              Device call state: Off-hook. At least one call exists that is dialing, active, or on hold,
                                        and no calls are ringing or waiting.
        CALL_STATE_RINGING              Device call state: Ringing. A new call arrived and is ringing or waiting. In the latter case,
                                        another call is already active.

     LISTEN_CELL_INFO                   : Listen for changes to observed cell info.
     LISTEN_CELL_LOCATION               : Listen for changes to the device's cell location.
     LISTEN_DATA_ACTIVITY               : Listen for changes to the direction of data traffic on the data connection (cellular).

        DATA_ACTIVITY_DORMANT           Data connection is active, but physical link is down
        DATA_ACTIVITY_IN                Data connection activity: Currently receiving IP PPP traffic.
        DATA_ACTIVITY_INOUT             Data connection activity: Currently both sending and receiving IP PPP traffic.
        DATA_ACTIVITY_NONE              Data connection activity: No traffic.
        DATA_ACTIVITY_OUT               Data connection activity: Currently sending IP PPP traffic.

     LISTEN_DATA_CONNECTION_STATE       : Listen for changes to the data connection state (cellular).

        DATA_CONNECTED                  Data connection state: Connected. IP traffic should be available.
        DATA_CONNECTING                 Data connection state: Currently setting up a data connection.
        DATA_DISCONNECTED               Data connection state: Disconnected. IP traffic not available.
        DATA_SUSPENDED                  Data connection state: Suspended. The connection is up, but IP traffic is temporarily unavailable.
                                        For example, in a 2G network, data activity may be suspended when a voice call arrives.

     LISTEN_SERVICE_STATE               : Listen for changes to the network service state (cellular).

        STATE_EMERGENCY_ONLY            The phone is registered and locked. Only emergency numbers are allowed.
        STATE_IN_SERVICE                Normal operation condition, the phone is registered with an operator either in home network or in roaming.
        STATE_OUT_OF_SERVICE            Phone is not registered with any operator, the phone can be currently searching a new operator to
                                        register to, or not searching to registration at all, or registration is denied, or radio signal
                                        is not available.
        STATE_POWER_OFF                 Radio of telephony is explicitly powered off.

        Also provide:
         * Roaming indicator
         * Operator name, short name and numeric id
         * Network selection mode

     LISTEN_SIGNAL_STRENGTHS            : Listen for changes to the network signal strengths (cellular).
     LISTEN_MESSAGE_WAITING_INDICATOR   : Listen for changes to the message-waiting indicator.
     ===================================================================================
     */

    //private void callPhoneManager() {
    private void startRFListener() {
        Log.i(TAG, mTAG + "startRFListener: <<------- START ------- >>");
        TextView textView = (TextView)findViewById(R.id.id_text_view);
        mTM = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mTM.listen(new RfApi(textView),
              PhoneStateListener.LISTEN_CALL_STATE                  // idle, offhook, ringing
            | PhoneStateListener.LISTEN_CELL_INFO                   //      [API 17]
            | PhoneStateListener.LISTEN_CELL_LOCATION               //      [Use location manager instead!]
            | PhoneStateListener.LISTEN_DATA_ACTIVITY               // No,In,Ou,IO,Do
            | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE       // Di,Ct,Cd,Su
            | PhoneStateListener.LISTEN_SERVICE_STATE               // emergency_only,in_service,out_of_service,power_off
            | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS            //
            | PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR   //      CFI
            | PhoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR   //      MWI
        );
        Log.i(TAG, mTAG + "startRFListener: <<------- END --------- >>");
    }

    // De-register the telephony events
    private void stopRFListener() {
        TextView textView = (TextView)findViewById(R.id.id_text_view);              // new
        //TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        //tm.listen(phoneListener, PhoneStateListener.LISTEN_NONE);
        mTM = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mTM.listen(new RfApi(textView), PhoneStateListener.LISTEN_NONE);
    }

    //===================================================================================
    //  Other... ??
    //===================================================================================

     // Display the telephony related information
    private void displayTelephonyInfo() {

        Log.i(TAG, mTAG + "displayTelephonyInfo: <<------- START ------- >>");
        mTM = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        // ToDo: maybe not here!?
        GsmCellLocation gsmLoc = (GsmCellLocation) mTM.getCellLocation();    // require:  ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION
        String networktype = getNetworkTypeString(mTM.getNetworkType());     // NETWORK_TYPE_XXX (RAT)
        //String phonetype = getPhoneTypeString(mTM.getPhoneType());           // PHONE_TYPE:  [GSM, CDMA, SIP, NONE]

        String deviceinfo = "";
        deviceinfo += ("RAT: " + networktype + "\n");
        //deviceinfo += ("PHONE_TYPE: " + phonetype + "\n");
        //setTextViewText(info_ids[INFO_DEVICE_INFO_INDEX],deviceinfo);

        Log.i(TAG, mTAG + deviceinfo);
        Log.i(TAG, mTAG + "displayTelephonyInfo: <<------- END --------- >>");
    }

    // Fixme:  We can probably remove this, as TelephonyManager.java already has calls to do this:
    // Fixme:  getNetworkType, getNetworkClass, getNetworkTypeName

    // NETWORK_TYPE_XXX
    private String getNetworkTypeString(int type){
        switch(type) {
            // ToDo: Sort by 2/3/4G
            case TelephonyManager.NETWORK_TYPE_EDGE:    return "EDGE";
            case TelephonyManager.NETWORK_TYPE_GPRS:    return "GPRS";
            case TelephonyManager.NETWORK_TYPE_UMTS:    return "UMTS";
            case TelephonyManager.NETWORK_TYPE_HSDPA:   return "HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSUPA:   return "HSUPA";
            case TelephonyManager.NETWORK_TYPE_HSPA:    return "HSPA";
            case TelephonyManager.NETWORK_TYPE_CDMA:    return "CDMA";
            case TelephonyManager.NETWORK_TYPE_EVDO_0:  return "EVDO_0";
            case TelephonyManager.NETWORK_TYPE_EVDO_A:  return "EVDO_A";
            case TelephonyManager.NETWORK_TYPE_EVDO_B:  return "EVDO_B";
            case TelephonyManager.NETWORK_TYPE_1xRTT:   return "1xRTT";
            case TelephonyManager.NETWORK_TYPE_IDEN:    return "IDEN";
            case TelephonyManager.NETWORK_TYPE_EHRPD:   return "EHRPD";
            case TelephonyManager.NETWORK_TYPE_HSPAP:   return "HSPAP";
            case TelephonyManager.NETWORK_TYPE_LTE:     return "LTE";
            default: return "unknown";
        }
    }

    // PHONE_TYPE
    private String getPhoneTypeString(int type){
        switch(type) {
            case TelephonyManager.PHONE_TYPE_GSM:   return "GSM";
            case TelephonyManager.PHONE_TYPE_CDMA:  return "CDMA";
            case TelephonyManager.PHONE_TYPE_SIP:   return "SIP";
            case TelephonyManager.PHONE_TYPE_NONE:  return "NONE";
            default: return "unknown";
        }
    }

}
