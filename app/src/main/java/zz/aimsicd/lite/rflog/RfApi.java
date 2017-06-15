package zz.aimsicd.lite.rflog;

import android.content.Context;
import android.os.Build;

import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;

import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

import android.util.Log;
import android.widget.TextView;

import java.util.List;


/*
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

public class RfApi extends PhoneStateListener {

    private static final String TAG = "AIMSICD";
    private static final String mTAG = "RfApi";

    // Attributes
    private final TextView mTextView;
    public TelephonyManager mTM;
    private Context context;

    // Constructor
    public RfApi(TextView textView) {
        mTextView = textView;
    }


    //===================================================================================
    //  Phone State methods
    //===================================================================================

    // LISTEN_SERVICE_STATE
    // DB:  [emergency_only,in_service,out_of_service,power_off]  or [emo,ins,out,off]
    private String getServiceState(int state) {
        switch (state) {
            case ServiceState.STATE_IN_SERVICE:             return "ins";   // in_service
            case ServiceState.STATE_OUT_OF_SERVICE:         return "out";   // out_of_service
            case ServiceState.STATE_EMERGENCY_ONLY:         return "emo";   // emergency_only
            case ServiceState.STATE_POWER_OFF:              return "off";   // power_off
            default: return "unknown";
        }
    }

    // LISTEN_CALL_STATE
    // DB:  [idle, offhook, ringing]
    private String getCallState(int state) {
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:          return "idle";      // No activity
            case TelephonyManager.CALL_STATE_RINGING:       return "ringing";   // A new call arrived and is ringing or waiting.
            case TelephonyManager.CALL_STATE_OFFHOOK:       return "offhook";   // At least one call is dialing, active, or on hold
            default: return "unknown";
        }
    }

    // LISTEN_DATA_CONNECTION_STATE
    // DB:  [Di,Ct,Cd,Su]
    private String getConnectionState(int state) {
        switch (state) {
            case TelephonyManager.DATA_DISCONNECTED:        return "disconnected";  // Di   - IP traffic not available.
            case TelephonyManager.DATA_CONNECTING:          return "setup";         // Ct   - Currently setting up a data connection.
            case TelephonyManager.DATA_CONNECTED:           return "connected";     // Cd   - IP traffic should be available.
            case TelephonyManager.DATA_SUSPENDED:           return "suspended";     // Su   - The connection is up, but IP traffic is temporarily unavailable.
            default: return "unknown";
        }
    }

    // LISTEN_DATA_ACTIVITY
    // DB:  [No,In,Ou,IO,Do]
    private String getDataActivity(int state) {
        switch (state) {
            case TelephonyManager.DATA_ACTIVITY_NONE:       return "No";    // No traffic.
            case TelephonyManager.DATA_ACTIVITY_IN:         return "In";    // Currently receiving IP PPP traffic.
            case TelephonyManager.DATA_ACTIVITY_OUT:        return "Ou";    // Currently sending IP PPP traffic.
            case TelephonyManager.DATA_ACTIVITY_INOUT:      return "IO";    // Currently both sending and receiving IP PPP traffic.
            case TelephonyManager.DATA_ACTIVITY_DORMANT:    return "Do";    // Data connection is active, but physical link is down
            default: return "unknown"; // "Un" ?
        }
    }


    //==========================================
    // TODO:
    //==========================================

    // LISTEN_CELL_INFO
    // DB:  []
    // todo

    // LISTEN_CELL_LOCATION
    // LISTEN_SIGNAL_STRENGTHS

    // LISTEN_CALL_FORWARDING_INDICATOR
    // LISTEN_MESSAGE_WAITING_INDICATOR

    //===================================================================================
    //  Phone State Listeners
    //===================================================================================
    /*
        AOS API Available Listeners:

        [x]      onCallForwardingIndicatorChanged(boolean cfi)
        [x]      onCallStateChanged(int state, String incomingNumber)
        [!]      onCellInfoChanged(List<CellInfo> cellInfo)
        [-]      onCellLocationChanged(CellLocation location)
        [x]      onDataActivity(int direction)
        [x]      onDataConnectionStateChanged(int state, int networkType), (int state)
        [x]      onMessageWaitingIndicatorChanged(boolean mwi)
        [x]      onServiceStateChanged(ServiceState serviceState)
        [ ]      onSignalStrengthsChanged(SignalStrength signalStrength)
        [?]      onVoLteServiceStateChanged(VoLteServiceState stateInfo)
    }

        WHERE:
        [x] = implemented, [?] = should we implement? ToDo: [-] = Need work, [!] = problem
    */

    @Override
    public void onCallForwardingIndicatorChanged(boolean cfi) {
        super.onCallForwardingIndicatorChanged(cfi);
        Log.i(TAG, "CFI: " + cfi);
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        String CS = getCallState(state);
        //String message = "CallState: " + getCallState(state); //  + "incomingNumber: " + incomingNumber;
        Log.i(TAG, "CS: " + CS);
        //mTextView.setText(CS);
    }

    @Override
    public void onCellInfoChanged(List<CellInfo> cellInfo) {
        super.onCellInfoChanged(cellInfo);
        Log.i(TAG, "CI 0/3: cellInfo: This ain\'t working yet!");
        //Log.i(TAG, "CI 0/3: cellInfo: " + cellInfo);

        //String CI = gatherRadioData();
        //Log.i(TAG, "CI 3/3: " + CI);
    }

    @Override
    public void onCellLocationChanged(CellLocation location) {
        super.onCellLocationChanged(location);
        //String CL = getCellLocation(location);    // [deprecated]
        // NOTE: If there is only one radio in the device and that radio has an LTE connection, this method will return null
        //       (or erroneous data).
        String message = "";
        if (location instanceof GsmCellLocation) {
            GsmCellLocation gcLoc = (GsmCellLocation) location;
            message += "CL GSM: "                           + gcLoc + "\n";
            message += "CL GSM CID: "                       + gcLoc.getCid() + "\n";
            message += "CL GSM LAC: "                       + gcLoc.getLac() + "\n";
            message += "CL GSM PSC: "                       + gcLoc.getPsc();
            Log.i(TAG, message);
        } else if (location instanceof CdmaCellLocation) {
            CdmaCellLocation ccLoc = (CdmaCellLocation) location;
            message += "CL CDMA: "                          + ccLoc + "\n";
            message += "CL CDMA BaseStationId: "            + ccLoc.getBaseStationId() + "\n";
            message += "CL CDMA BaseStationLatitude: "      + ccLoc.getBaseStationLatitude() + "\n";
            message += "CL CDMA BaseStationLongitude: "     + ccLoc.getBaseStationLongitude() + "\n";
            message += "CL CDMA NetworkId: "                + ccLoc.getNetworkId() + "\n";
            message += "CL CDMA SystemId: "                 + ccLoc.getSystemId();
            Log.i(TAG, message);
        } else {
            Log.i(TAG, "onCellLocationChanged: " + location);
        }
    }

    @Override
    public void onDataActivity(int direction) {
        super.onDataActivity(direction);
        String DA = getDataActivity(direction);
        //if (!DA.equals("unknown")) {
        Log.i(TAG, "DA: " + DA);
        //}
    }

    @Override
    public void onDataConnectionStateChanged(int state) {
        super.onDataConnectionStateChanged(state);
        String DC = getConnectionState(state);
        //String message = "DataConnectionState: " + getConnectionState(state);
        Log.i(TAG, "DC: " + DC);
        //mTextView.setText(DC);
    }

    @Override
    public void onMessageWaitingIndicatorChanged(boolean mwi) {
        super.onMessageWaitingIndicatorChanged(mwi);
        Log.i(TAG, "MWI: " + mwi);
    }


    @Override
    public void onServiceStateChanged(ServiceState serviceState) {
        super.onServiceStateChanged(serviceState);
        String SS = getServiceState(serviceState.getState());
        //String message ="";
        //message += "SS: ==> " + serviceState + "\n";
        //message += "onServiceStateChanged: getOperatorAlphaLong "   + serviceState.getOperatorAlphaLong() + "\n";
        //message += "onServiceStateChanged: getOperatorAlphaShort "  + serviceState.getOperatorAlphaShort() + "\n";
        //message += "onServiceStateChanged: getOperatorNumeric "     + serviceState.getOperatorNumeric() + "\n";
        //message += "SS: ManualSelection: "  + serviceState.getIsManualSelection() + "\n";
        //message += "SS: Roaming: "          + serviceState.getRoaming() + "\n";
        //message += "SS: " + getServiceState(serviceState.getState());
        Log.i(TAG, "SS: " + SS);
        if (serviceState.getRoaming()) {
            Log.i(TAG, "SS: Roaming! (" + serviceState.getOperatorNumeric() + ")" );
        }
        //mTextView.setText(SS);
    }

    /*
    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        //String SS = getSignalStrength(signalStrength.getState());
        String SI = "not implemented";
        Log.i(TAG, "SIG: " + SI);
    }*/

    /*
    @Override
    public void onVoLteServiceStateChanged(VoLteServiceState stateInfo) {

    }*/

}
