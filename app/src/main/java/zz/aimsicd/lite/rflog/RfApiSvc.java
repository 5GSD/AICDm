package zz.aimsicd.lite.rflog;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.support.annotation.Nullable;
import android.widget.Toast;
import android.util.Log;


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
