package zz.aimsicd.lite.rflog;

import android.os.Build;

import android.content.Context;

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
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

import android.util.Log;
import android.widget.TextView;

import java.util.List;


/*
    =========================================================================================
     LISTEN_CALL_FORWARDING_INDICATOR   : Listen for changes to the call-forwarding indicator.
     LISTEN_CALL_STATE                  : Listen for changes to the device call state.

        CALL_STATE_IDLE                    Device call state: No activity.
        CALL_STATE_OFFHOOK                 Device call state: Off-hook. At least one call exists that is dialing, active, or on hold,
                                           and no calls are ringing or waiting.
        CALL_STATE_RINGING                 Device call state: Ringing. A new call arrived and is ringing or waiting. In the latter case,
                                           another call is already active.

     LISTEN_CELL_INFO                   : Listen for changes to observed cell info.
     LISTEN_CELL_LOCATION               : Listen for changes to the device's cell location.
     LISTEN_DATA_ACTIVITY               : Listen for changes to the direction of data traffic on the data connection (cellular).

        DATA_ACTIVITY_DORMANT              Data connection is active, but physical link is down
        DATA_ACTIVITY_IN                   Data connection activity: Currently receiving IP PPP traffic.
        DATA_ACTIVITY_INOUT                Data connection activity: Currently both sending and receiving IP PPP traffic.
        DATA_ACTIVITY_NONE                 Data connection activity: No traffic.
        DATA_ACTIVITY_OUT                  Data connection activity: Currently sending IP PPP traffic.

     LISTEN_DATA_CONNECTION_STATE       : Listen for changes to the data connection state (cellular).

        DATA_CONNECTED                     Data connection state: Connected. IP traffic should be available.
        DATA_CONNECTING                    Data connection state: Currently setting up a data connection.
        DATA_DISCONNECTED                  Data connection state: Disconnected. IP traffic not available.
        DATA_SUSPENDED                     Data connection state: Suspended. The connection is up, but IP traffic is temporarily unavailable.
                                           For example, in a 2G network, data activity may be suspended when a voice call arrives.

     LISTEN_SERVICE_STATE               : Listen for changes to the network service state (cellular).

        STATE_EMERGENCY_ONLY               The phone is registered and locked. Only emergency numbers are allowed.
        STATE_IN_SERVICE                   Normal operation condition, the phone is registered with an operator either in home network or in roaming.
        STATE_OUT_OF_SERVICE               Phone is not registered with any operator, the phone can be currently searching a new operator to
                                           register to, or not searching to registration at all, or registration is denied, or radio signal
                                           is not available.
        STATE_POWER_OFF                    Radio of telephony is explicitly powered off.

     Also provide:
     * Roaming indicator
     * Operator name, short name and numeric id
     * Network selection mode

     LISTEN_SIGNAL_STRENGTHS            : Listen for changes to the network signal strengths (cellular).
     LISTEN_MESSAGE_WAITING_INDICATOR   : Listen for changes to the message-waiting indicator.
    =========================================================================================
*/

public class PhoneCallback extends PhoneStateListener {

    private static final String TAG = "AIMSICD";
    private static final String mTAG = "logRF";

    // Attributes
    private final TextView mTextView;
    public TelephonyManager mTM;
    private Context context;

    // Constructor
    public PhoneCallback(TextView textView) {
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
    // TODO: WTF!?
    //==========================================

    // LISTEN_CELL_INFO
    // DB:  []
    private String getCellInfo(List<CellInfo> cellInfo) {
        //switch (cellInfo) {
        //    case TelephonyManager.DATA_ACTIVITY_NONE:       return "No";    // No traffic.
        //    default: return "unknown"; // "Un" ?
        //}

        // LTE
        int mcc = -1, mnc = -1, tac = -1,  ci = -1, pci = -1, ecn = -1;     // getCellIdentity()
        int ta = -1, lev = -1, srp = -1, srq = -1, snr = -1, cqi = -1;      // getCellSignalStrength()
        // UMTS
        int cid = -1, lac = -1, psc = -1, ucn = -1;

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if(tm.getAllCellInfo() != null && !tm.getAllCellInfo().isEmpty()) {
            CellInfo info = tm.getAllCellInfo().get(0); // Todo: What's in get(0) exactly?

            if (info instanceof CellInfoLte) {
                CellIdentityLte         dci = ((CellInfoLte) info).getCellIdentity();

                // All these get: Integer.MAX_VALUE if unknown
                mcc = dci.getMcc();                 // Mobile Country Code:                 [0..999]
                mnc = dci.getMnc();                 // Mobile Network Code:                 [0..999]
                tac = dci.getTac();                 // 16-bit Tracking Area Code:           [?..]
                ci = dci.getCi();                   // 28-bit Cell Identity:                [?..0xffffff]
                pci = dci.getPci();                 // Physical Cell Id:                    [0..503]
                //ecn = dci.getEarfcn();            // 18-bit Absolute RF Channel Number:   [?..65535]?      !! API 24

                Log.i(TAG, "LTE info 1/3: " + "MCC:" + mcc + ", MNC:" + mnc + ", TAC:" + tac + ", CI:" + ci + ", PCI:" + pci);

            } else if (info instanceof CellInfoWcdma) {
                CellIdentityWcdma         dci = ((CellInfoWcdma) info).getCellIdentity();

                mcc = dci.getMcc();
                mnc = dci.getMnc();
                cid = dci.getCid();                 // 28-bit UMTS Cell Identity                [0..268435455] / [0..0xffffff]?
                lac = dci.getLac();                 // 16-bit Location Area Code:               [0..65535]
                psc = dci.getPsc();                 // 9-bit UMTS Primary Scrambling Code:      [0..511]
                //ucn = dci.getUarfcn();            // 16-bit UMTS Absolute RF Channel Number:  [?..65535?]?      !! API 24

                Log.i(TAG, "UMTS info 1/2: " + "MCC:" + mcc + ", MNC:" + mnc + ", LAC:" + lac + ", CID:" + cid + ", PSC:" + psc);

            } else if (info instanceof CellInfoGsm) {
                CellIdentityGsm dci = ((CellInfoGsm) info).getCellIdentity();

                // CellIdentity
                mcc = dci.getMcc();
                mnc = dci.getMnc();
                cid = dci.getCid();                 // 28-bit Cell Identity:                    [?..0xffffff]
                lac = dci.getLac();                 // 16-bit Location Area Code:               [0..65535]
                //bss = dci.getBsic();              // 6-bit Base Station Identity Code:        [0..63]          !! API 24
                //acn = dci.getArfcn();             // 18-bit Absolute RF Channel Number:       [?..65535]?      !! API 24

                Log.i(TAG, "GSM info 1/2: " + "MCC:" + mcc + ", MNC:" + mnc + ", LAC:" + lac + ", CID:" + cid);
            }

        } else {
            Log.w(TAG, mTAG + "getAllCellInfo() returned NULL or an empty list!");
            return "fail";
        }

        // also add a timestamp:
        //data.putLong(TIMESTAMP, System.currentTimeMillis());
        return "ok";
    }



    // LISTEN_CELL_LOCATION
    // LISTEN_SIGNAL_STRENGTHS

    // LISTEN_CALL_FORWARDING_INDICATOR
    // LISTEN_MESSAGE_WAITING_INDICATOR

    //===================================================================================
    //  Phone State Listeners
    //===================================================================================
    /*
        AOS API Available Listeners:

        [-]      onCallForwardingIndicatorChanged(boolean cfi)
        [x]      onCallStateChanged(int state, String incomingNumber)
        [?]      onCellInfoChanged(List<CellInfo> cellInfo)
        [-]      onCellLocationChanged(CellLocation location)
        [x]      onDataActivity(int direction)
        [x]      onDataConnectionStateChanged(int state, int networkType), (int state)
        [-]      onMessageWaitingIndicatorChanged(boolean mwi)
        [x]      onServiceStateChanged(ServiceState serviceState)
        [ ]      onSignalStrengthsChanged(SignalStrength signalStrength)

        WHERE:
        [x] = implemented, ToDo: [-] = Need work, [?] = problem
    */

    @Override
    public void onDataActivity(int direction) {
        super.onDataActivity(direction);
        String DA = getDataActivity(direction);
        Log.i(TAG, "DA: " + DA);
    }

    @Override
    public void onCellInfoChanged(List<CellInfo> cellInfo) {
        super.onCellInfoChanged(cellInfo);
        Log.i(TAG, "CI 0/3: cellInfo: This ain\'t working yet!");
        //Log.i(TAG, "CI 0/3: cellInfo: " + cellInfo);

        //String CI = getCellInfo(cellInfo);
        //Log.i(TAG, "CI 1/3: " + CI);

        //String CI = getCellularInfo(mTM);
        //Log.i(TAG, "CI 2/3: " + CI);
        //String CI = gatherRadioData();
        //Log.i(TAG, "CI 3/3: " + CI);
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
        mTextView.setText(SS);
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        String CS = getCallState(state);
        //String message = "CallState: " + getCallState(state); //  + "incomingNumber: " + incomingNumber;
        Log.i(TAG, "CS: " + CS);
        mTextView.setText(CS);
    }

    @Override
    public void onDataConnectionStateChanged(int state) {
        super.onDataConnectionStateChanged(state);
        String DC = getConnectionState(state);
        //String message = "DataConnectionState: " + getConnectionState(state);
        Log.i(TAG, "DC: " + DC);
        mTextView.setText(DC);
    }

    @Override
    public void onCellLocationChanged(CellLocation location) {
        super.onCellLocationChanged(location);
        //String CL = getCellLocation(location);
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
    public void onCallForwardingIndicatorChanged(boolean changed) {
        super.onCallForwardingIndicatorChanged(changed);
        String CFI = "n/a";
        Log.i(TAG, "CFI: " + CFI);
    }

    @Override
    public void onMessageWaitingIndicatorChanged(boolean changed) {
        super.onMessageWaitingIndicatorChanged(changed);
        String MWI = "n/a";
        Log.i(TAG, "MWI: " + MWI);
    }

    //===================================================================================
    //  More... ???
    //===================================================================================

    public String getCellularInfo(TelephonyManager telephonyManager) {
        Log.v(TAG, "inside getCellularInfo");
        String cellularInfo = "";
        String log = "";

        for (final CellInfo info : telephonyManager.getAllCellInfo()) {
            if (info instanceof CellInfoGsm) {
                log += "GSM@";
                CellIdentityGsm gsm_cell = ((CellInfoGsm) info).getCellIdentity();
                log += gsm_cell.getCid() + "#" + gsm_cell.getLac() + "#" + gsm_cell.getMcc() + "#" + gsm_cell.getMnc() + "_";

                final CellSignalStrengthGsm gsm = ((CellInfoGsm) info).getCellSignalStrength();
                log += gsm.getDbm() + "#" + gsm.getLevel();
            }
            else if (info instanceof CellInfoCdma) {
                log += "CDMA@";
                CellIdentityCdma cdma_cell = ((CellInfoCdma) info).getCellIdentity();
                log += cdma_cell.getBasestationId() + "#" + cdma_cell.getNetworkId() + "#" + cdma_cell.getSystemId() + "#" + cdma_cell.getSystemId() + "_";

                final CellSignalStrengthCdma cdma = ((CellInfoCdma) info).getCellSignalStrength();
                log += cdma.getDbm() + "#" + cdma.getLevel();
            }
            else if (info instanceof CellInfoLte) {
                log += "LTE@";
                CellIdentityLte lte_cell = ((CellInfoLte) info).getCellIdentity();
                log += lte_cell.getCi() + "#" + lte_cell.getPci() + "#" + lte_cell.getMcc() + "#" + lte_cell.getMnc() + "_";

                final CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
                log += lte.getDbm() + "#" + lte.getLevel();
            }
            else if (info instanceof CellInfoWcdma) {
                log += "WCDMA@";
                CellIdentityWcdma wcdma_cell = ((CellInfoWcdma) info).getCellIdentity();
                log += wcdma_cell.getCid() + "#" + wcdma_cell.getLac() + "#" + wcdma_cell.getMcc() + "#" + wcdma_cell.getMnc() + "_";

                final CellSignalStrengthWcdma wcdma = ((CellInfoWcdma) info).getCellSignalStrength();
                log += wcdma.getDbm() + "#" + wcdma.getLevel();
            } else {
                Log.v(TAG, "Unknown Network Type");
            }
        }
        cellularInfo = log;
        return cellularInfo;
    }


    /**
     * Probably this need to be split into two parts:
     *      1. getCellIdentity()        for use in:  DBi_bts
     *      2. getCellSignalStrength    for use in:  DBi_measure
     *
     * @return
     */
    //private TelephonyManager mTM;
    //public String gatherRadioData(TelephonyManager tm) {
    public String gatherRadioData() {

        //mTM = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        //TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        //this.context = mycont;
        //TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        //TelephonyManager tm = this.getSystemService(TELEPHONY_SERVICE); ; // = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        // LTE
        int mcc = -1, mnc = -1, tac = -1,  ci = -1, pci = -1, ecn = -1;     // getCellIdentity()
        int ta = -1, lev = -1, srp = -1, srq = -1, snr = -1, cqi = -1;      // getCellSignalStrength()
        int dbm = Integer.MIN_VALUE;
        int asu = Integer.MIN_VALUE;
        // UMTS
        int cid = -1, lac = -1, psc = -1, ucn = -1;
        // GSM
        int bss = -1, acn = -1;
        // CDMA
        // int

        //-----------------------------------------------------------------------------------------
        //  For getAllCellInfo():
        //      (a) there are 4 objects:    [CellInfoGsm, CellInfoCdma, CellInfoLte, CellInfoWcdma]
        //      (b) with 2 methods, each:   getCellIdentity(), getCellSignalStrength()
        //
        //  But:
        //      Because LTE signal strengths are not yet available from API's below 26,
        //      we have to manually extract the info that is actually there! [idiots]
        //
        //            String ltestr = signalStrength.toString();
        //            String[] parts = temp.split(" ");
        //
        //            The parts[] array will then contain these elements:
        //
        //            parts[0] = "Signalstrength:"  _ignore this, it's just the title_
        //            parts[1] = GsmSignalStrength
        //            parts[2] = GsmBitErrorRate
        //            parts[3] = CdmaDbm
        //            parts[4] = CdmaEcio
        //            parts[5] = EvdoDbm
        //            parts[6] = EvdoEcio
        //            parts[7] = EvdoSnr
        //            parts[8] = LteSignalStrength
        //         *  parts[9] = LteRsrp
        //         *  parts[10] = LteRsrq
        //         *  parts[11] = LteRssnr
        //         *  parts[12] = LteCqi
        //            parts[13] = gsm|lte
        //            parts[14] = TA?
        //
        //  References:
        //
        //  [1] https://sites.google.com/site/androiddevelopmentproject/home/rf-signal-tracker/a-very-basic-how-to
        //  [2] https://github.com/demantz/WearNetworkNotifications/blob/master/common/src/main/java/com/mantz_it/common/ConnectionData.java
        //  [3] https://github.com/parksjg/SignalStrength/tree/master
        //
        //-----------------------------------------------------------------------------------------
        if(tm.getAllCellInfo() != null && !tm.getAllCellInfo().isEmpty()) {
            CellInfo info = tm.getAllCellInfo().get(0); // Todo: What's in get(0) exactly?

            if (info instanceof CellInfoLte) {
                // "d" for details...
                //CellInfoLte d = (CellInfoLte) info;
                CellIdentityLte         dci = ((CellInfoLte) info).getCellIdentity();
                CellSignalStrengthLte   dss = ((CellInfoLte) info).getCellSignalStrength();

                // All these get: Integer.MAX_VALUE if unknown
                // d.getCellIdentity() --> dci
                mcc = dci.getMcc();                 // Mobile Country Code:                 [0..999]
                mnc = dci.getMnc();                 // Mobile Network Code:                 [0..999]
                tac = dci.getTac();                 // 16-bit Tracking Area Code:           [?..]
                 ci = dci.getCi();                  // 28-bit Cell Identity:                [?..0xffffff]
                pci = dci.getPci();                 // Physical Cell Id:                    [0..503]
                //ecn = dci.getEarfcn();            // 18-bit Absolute RF Channel Number:   [?..65535]?      !! API 24

                // d.getCellSignalStrength() --> dss
                 ta = dss.getTimingAdvance();       // LTE Timing Advance:                  [0..63.]
                asu = dss.getAsuLevel();            // Signal level as ASU value            [0..97], 99 is unknown (based on RSRP)
                dbm = dss.getDbm();                 // Signal Strength                      [dBm]
                //lev = dss.getLevel();             // signal level as an int from          [0..4]          !! Useless !

                if (Build.VERSION.SDK_INT >= 26 ) { // Build.VERSION_CODES.O
/*
                    srp = dss.getRsrp();            // Reference Signal Received Power                      !! API 26
                    srq = dss.getRsrq();            // Reference Signal Received Quality                    !! API 26
                    snr = dss.getRssnr();           // Reference Signal Signal-to-Noise Ratio               !! API 26
                    cqi = dss.getCqi();             // Channel Quality Indicator                            !! API 26
*/
                } else {
                    String tmp = dss.toString();
                    String[] parts = tmp.split(" ");
                    if (parts.length >= 13) {               // check that we have all parts!
                        srp = Integer.valueOf(parts[9]);
                        srq = Integer.valueOf(parts[10]);
                        snr = Integer.valueOf(parts[11]);
                        cqi = Integer.valueOf(parts[12]);
                    } else {
                        Log.w(TAG, "Missing parts in the LTE signal strengths bundle. (parts=" + parts.length + ") \nparts: " + tmp);
                    }
                }
                Log.i(TAG, "LTE info 1/3: " + "MCC:" + mcc + ", MNC:" + mnc + ", TAC:" + tac + ", CI:" + ci + ", PCI:" + pci);
                Log.i(TAG, "LTE info 2/3: " + "TA:" + ta + ", ASU:" + asu + ", dBm:" + dbm);
                Log.i(TAG, "LTE info 3/3: " + "RSRP:" + srp + ", RSRQ:" + srq + ", RSSNR:" + snr + ", CQI:" + cqi);

            } else if (info instanceof CellInfoWcdma) {
                CellIdentityWcdma         dci = ((CellInfoWcdma) info).getCellIdentity();
                CellSignalStrengthWcdma   dss = ((CellInfoWcdma) info).getCellSignalStrength();

                // CellIdentity
                mcc = dci.getMcc();
                mnc = dci.getMnc();
                cid = dci.getCid();                 // 28-bit UMTS Cell Identity                [0..268435455] / [0..0xffffff]?
                lac = dci.getLac();                 // 16-bit Location Area Code:               [0..65535]
                psc = dci.getPsc();                 // 9-bit UMTS Primary Scrambling Code:      [0..511]
                //ucn = dci.getUarfcn();            // 16-bit UMTS Absolute RF Channel Number:  [?..65535?]?      !! API 24

                // CellSignalStrength
                asu = dss.getAsuLevel();
                dbm = dss.getDbm();
                //lev = dss.getLevel();             // Useless!

                Log.i(TAG, "UMTS info 1/2: " + "MCC:" + mcc + ", MNC:" + mnc + ", LAC:" + lac + ", CID:" + cid + ", PSC:" + psc);
                Log.i(TAG, "UMTS info 2/2: " +"ASU:" + asu + ", dBm:" + dbm);

            } else if (info instanceof CellInfoGsm) {
                CellIdentityGsm         dci = ((CellInfoGsm) info).getCellIdentity();
                CellSignalStrengthGsm   dss = ((CellInfoGsm) info).getCellSignalStrength();

                // CellIdentity
                mcc = dci.getMcc();
                mnc = dci.getMnc();
                cid = dci.getCid();                 // 28-bit Cell Identity:                    [?..0xffffff]
                lac = dci.getLac();                 // 16-bit Location Area Code:               [0..65535]
                //bss = dci.getBsic();              // 6-bit Base Station Identity Code:        [0..63]          !! API 24
                //acn = dci.getArfcn();             // 18-bit Absolute RF Channel Number:       [?..65535]?      !! API 24

                // CellSignalStrength
                //ta = dss.getTimingAdvance();      // GSM Timing Advance: (0..219 symbols)     [0..63]          !! API 24
                asu = dss.getAsuLevel();            // signal level as ASU value:               [0..31]
                dbm = dss.getDbm();

                Log.i(TAG, "GSM info 1/2: " + "MCC:" + mcc + ", MNC:" + mnc + ", LAC:" + lac + ", CID:" + cid);
                Log.i(TAG, "GSM info 2/2: " +"ASU:" + asu + ", dBm:" + dbm);

            } else if (info instanceof CellInfoCdma) {
                CellInfoCdma d = (CellInfoCdma) info;
                dbm = d.getCellSignalStrength().getDbm();
                asu = d.getCellSignalStrength().getAsuLevel();

                CellIdentityCdma         dci = ((CellInfoCdma) info).getCellIdentity();
                CellSignalStrengthCdma   dss = ((CellInfoCdma) info).getCellSignalStrength();

                // todo: ...
                /*
                // CellIdentity
                bbid = getBasestationId()
                blat = getLatitude()
                blon = getLongitude()
                bnid = getNetworkId()
                bsid = getSystemId()

                // CellSignalStrength
                getAsuLevel()   // Get the signal level as an asu value between 0..97, 99 is unknown
                getCdmaDbm()    // Get the CDMA RSSI value in dBm
                getCdmaEcio()   // Get the CDMA Ec/Io value in dB*10
                getCdmaLevel()  // Get cdma as level 0..4
                getDbm()        // Get the signal strength as dBm
                getEvdoDbm()    // Get the EVDO RSSI value in dBm
                getEvdoEcio()   // Get the EVDO Ec/Io value in dB*10
                getEvdoLevel()  // Get Evdo as level 0..4
                getEvdoSnr()    // Get the signal to noise ratio.
                getLevel()      // Get signal level as an int from 0..4
                */

                //Log.i(TAG, "CDMA info 1/3: " + "MCC:" + mcc + ", MNC:" + mnc + ", TAC:" + tac + ", CI:" + ci + ", PCI:" + pci);
                //Log.i(TAG, "CDMA info 2/3: " + "TA:" + ta + ", ASU:" + asu + ", dBm:" + dbm);
                //Log.i(TAG, "CDMA info 3/3: " + "RSRP:" + srp + ", RSRQ:" + srq + ", RSSNR:" + snr + ", CQI:" + cqi);
            }
        } else {
            Log.w(TAG, mTAG + "getAllCellInfo() returned NULL or an empty list!");
            return "fail";
        }

        // also add a timestamp:
        //data.putLong(TIMESTAMP, System.currentTimeMillis());

        return "ok";
    }

}
