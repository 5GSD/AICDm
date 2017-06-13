package zz.aimsicd.lite.rflog;

import java.util.ArrayList;

import android.util.Log;


/**
 * Name:    Analyzer.java
 * Date:    2017-06-13
 *
 * Description:
 *
 *      This is main the analyzer loop that that iterate through every Detection Test and collects the test results.
 *      Each test results (DT-n) are multiplied with its corresponding tuning parameter (risk factor) from DetectionFlags table.
 *      All the results are then added together to determine the current threat level (or indicate an immediate attack).
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
 * Developer Notes:
 *      ToDo:
 *      [ ]
 *      [ ]
 *
 *
 *
 *
 */
public class Analyzer {

    private static final String TAG = "AIMSICD";
    private static final String mTAG = "Analyzer: ";

    // ========================================================================
    //   ADD / ENABLE / DISABLE Detection Tests here:
    //      - To Enable:    add Detection Test id to this list as "DT<id>"
    //      - To Disable:   just comment out the "DTn" you don't want to run.
    // ========================================================================
    enum test_id {
        DT1,        //
        DT2,        //
        DT3,        //
        DTn         // This is an empty template! Do not remove.
    }

    // ========================================================================
    //   START:     Detection Test Loop
    // ========================================================================
    //  Test Loop PSEUDO-CODE!
    //  ToDo: Make it real!
    /*
    for(String id in test_id) {
        // (dtime, err, score)[i]
        try {
            ArrayList TestResult[ test] = detect.RunTest(id);
        } catch (exception ee) {
            Log.e(TAG, mTAG + "DT-" + id + " Error:" + ee);
        }

        if(!err && Tdebug) {
            Log.v(TAG, mTAG + "DT-" + id + " score: " + p1 + " in %s seconds.", time);
        }
    }
    */

    // ========================================================================
    //   END
    // ========================================================================

}
