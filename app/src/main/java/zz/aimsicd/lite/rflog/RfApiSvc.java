package zz.aimsicd.lite.rflog;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;


import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;

import android.widget.Toast;
import android.util.Log;

import android.support.annotation.Nullable;

/**
 * Name:    RfApi.java
 * Date:    2017-06-13
 *
 * Description:
 *
 *      This is the RF collector using the AOS API provided listeners.
 *
 *      The mobile network connection details and signal strengths are collected and immediately pushed into the database tables:
 *          DBi_phy, DBi_vol, DBi_con
 *      It is pushed into the DB via a bundle helper of
 *
 *      It is started by the settings activity and should remain in the background at all times until explicitly stopped my app itself.
 *      Thus is will also need to auto-restart in case of GC FC, poweroutage or reboot.
 *
 * Depends:
 *
 *      [ ]
 *
 * Calls/Broadcasts:
 *
 *      [ ]
 *
 *
 * ToDo: Developer Notes:
 *
 *      [ ]  Move and Rename to:  ./collectors/RfApi.java   (Where does previous RfAPi.java go?)
 *      [ ]  Make a XXX type bundle helper
 *      [ ]
 *
 *
 */
public class RfApiSvc extends Service {

    private static final String TAG = "AIMSICD";
    private static final String mTAG = "RfApiSvc";

    //public RfApiSvc() {    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        // Let it continue running until it is stopped.

        gatherRadioData();

        /*while (gatherRadioData() == "ok") {
            //gatherRadioData();
            try {
                wait(10000); // [ms] --> wait 1000 ms = 1 sec
            } catch (Exception ee) {
                Log.e(TAG, mTAG + ": Wait Failed! Exception: " +ee );
            }
        }*/
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }


    /*
    ===========================================================================
                                TEST CODE
    ===========================================================================
    */


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
    //
    //            From Google sources (as of 2017-06-14) [4] we have:
    //
    //            @Override
    //            public String toString() {
    //                return "CellSignalStrengthLte:"
    //                        + " ss=" + mSignalStrength
    //                        + " rsrp=" + mRsrp
    //                        + " rsrq=" + mRsrq
    //                        + " rssnr=" + mRssnr
    //                        + " cqi=" + mCqi
    //                        + " ta=" + mTimingAdvance;
    //            }
    //
    //
    //  References:
    //
    //  [1] https://sites.google.com/site/androiddevelopmentproject/home/rf-signal-tracker/a-very-basic-how-to
    //  [2] https://github.com/demantz/WearNetworkNotifications/blob/master/common/src/main/java/com/mantz_it/common/ConnectionData.java
    //  [3] https://github.com/parksjg/SignalStrength/tree/master
    //  [4] https://android.googlesource.com/platform/frameworks/base/+/master/telephony/java/android/telephony/CellSignalStrengthLte.java
    //
    // Probably this need to be split into two parts:
    //
    //      1. getCellIdentity()        for use in:  DBi_phy
    //      2. getCellSignalStrength    for use in:  DBi_vol
    //
    //-----------------------------------------------------------------------------------------


    public String gatherRadioData() {

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

        String tas = "";

        TelephonyManager mTM = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        CellInfo info = mTM.getAllCellInfo().get(0);

        if(info != null && !mTM.getAllCellInfo().isEmpty()) {
            // Todo: What's in get(0) exactly?
            //CellInfo info = mTM.getAllCellInfo().get(0);

            if (info instanceof CellInfoLte) {
                // "d" for details...
                //CellInfoLte d = (CellInfoLte) info;
                CellIdentityLte dci = ((CellInfoLte) info).getCellIdentity();
                CellSignalStrengthLte dss = ((CellInfoLte) info).getCellSignalStrength();

                // All these get: Integer.MAX_VALUE if unknown
                // d.getCellIdentity() --> dci
                mcc = dci.getMcc();                 // Mobile Country Code:                 [0..999]
                mnc = dci.getMnc();                 // Mobile Network Code:                 [0..999]
                tac = dci.getTac();                 // 16-bit Tracking Area Code:           [?..]
                ci  = dci.getCi();                  // 28-bit Cell Identity:                [?..0xffffff]
                pci = dci.getPci();                 // Physical Cell Id:                    [0..503]
                //ecn = dci.getEarfcn();            // 18-bit Absolute RF Channel Number:   [?..65535]?      !! API 24

                // d.getCellSignalStrength() --> dss
                ta  = dss.getTimingAdvance();       // LTE Timing Advance:                  [0..63], Integer.MAX_VALUE if no active RRC
                asu = dss.getAsuLevel();            // Signal level as ASU value            [0..97], 99 is unknown (based on RSRP)  //3GPP TS 36.213 Sec 4.2.3
                dbm = dss.getDbm();                 // Signal Strength                      [dBm]
                //lev = dss.getLevel();             // signal level as an int from          [0..4]          !! Useless !

                if (Build.VERSION.SDK_INT >= 26 ) { // Build.VERSION_CODES.O
                /*
                    srp = dss.getRsrp();            // Reference Signal Received Power          [-140..-44] dBm     !! API 26   //3GPP TS 36.133 V8.9.0
                    srq = dss.getRsrq();            // Reference Signal Received Quality        [-3..-19.5] dB   ?   !! API 26
                    snr = dss.getRssnr();           // Reference Signal Signal-to-Noise Ratio   [] !! API 26
                    cqi = dss.getCqi();             // Channel Quality Indicator                [] !! API 26
                */
                } else {
                    //                return "CellSignalStrengthLte:"
                    //                        + " ss=" + mSignalStrength
                    //                        + " rsrp=" + mRsrp
                    //                        + " rsrq=" + mRsrq
                    //                        + " rssnr=" + mRssnr
                    //                        + " cqi=" + mCqi
                    //                        + " ta=" + mTimingAdvance;

                    String tmp = dss.toString();
                    String[] parts = tmp.split(" ");
                    // check that we have all parts!
                    Log.v(TAG, "LTE signal strengths bundle:\nparts: " + tmp);
                    // parts: CellSignalStrengthLte: ss=31 rsrp=-77 rsrq=-6 rssnr=2147483647 cqi=2147483647 ta=2147483647
                    if (parts.length == 7) {                // AOS 6.0+ has 7 parts
                        //lss = Integer.valueOf(parts[1]);    //
                        srp = Integer.parseInt(parts[2].replace("rsrp=",""));   //
                        srq = Integer.parseInt(parts[3].replace("rsrq=",""));   //
                        snr = Integer.parseInt(parts[4].replace("rssnr=",""));  //
                        cqi = Integer.parseInt(parts[5].replace("cqi=",""));    //
                        ta  = Integer.valueOf(parts[6].replace("ta=",""));      //
                    } else if (parts.length >= 13) {        // older API with AOS < 6.0 (M) may have other parts?  ToDo: check!
                        srp = Integer.valueOf(parts[9]);    //
                        srq = Integer.valueOf(parts[10]);   //
                        snr = Integer.valueOf(parts[11]);   //
                        cqi = Integer.valueOf(parts[12]);   //
                        //ta  = Integer.valueOf(parts[13]);   //
                    } else {
                        Log.w(TAG, "Unusual parts in the LTE signal strengths bundle. (parts=" + parts.length + ") \nparts: " + tmp);
                    }
                }
                // IF value is MAX_INT, then return minimum dB ???
                // NOTE: Perhaps a bad idea, as a non working measurement would give the impression it is working.
                if (srp == 2147483647) srp = -141;  //
                if (srq == 2147483647) srq = -30;   //
                if (snr == 2147483647) snr = -1;    //
                if (cqi == 2147483647) cqi = -20;   //
                if ( ta == 2147483647)  ta = -1;    // Returns Integer.MAX_VALUE if no RRC

                Log.i(TAG, "LTE info 1/3: " + "MCC:" + mcc + ", MNC:" + mnc + ", TAC:" + tac + ", CI:" + ci + ", PCI:" + pci);
                Log.i(TAG, "LTE info 2/3: " + "TA:" + ta + ", ASU:" + asu + ", dBm:" + dbm);
                Log.i(TAG, "LTE info 3/3: " + "RSRP:" + srp + ", RSRQ:" + srq + ", RSSNR:" + snr + ", CQI:" + cqi + ", TA:" + ta); // tas

            } else if (info instanceof CellInfoWcdma) {
                CellIdentityWcdma dci = ((CellInfoWcdma) info).getCellIdentity();
                CellSignalStrengthWcdma dss = ((CellInfoWcdma) info).getCellSignalStrength();

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
                CellIdentityGsm dci = ((CellInfoGsm) info).getCellIdentity();
                CellSignalStrengthGsm dss = ((CellInfoGsm) info).getCellSignalStrength();

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

                CellIdentityCdma dci = ((CellInfoCdma) info).getCellIdentity();
                CellSignalStrengthCdma dss = ((CellInfoCdma) info).getCellSignalStrength();

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
