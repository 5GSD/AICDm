package zz.aimsicd.lite.rflog;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.support.annotation.Nullable;
import android.widget.Toast;
import android.util.Log;

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
 *      [ ]  Move and Rename to:  ./collectors/RfApi.java
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
        // Let it continue running until it is stopped.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

}
