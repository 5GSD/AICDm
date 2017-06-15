package zz.aimsicd.lite.rflog;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;

import android.support.v7.app.AlertDialog;

public class PermissionUtils {

    //--------------------------------------------------
    // Permissions Methods
    //--------------------------------------------------

    public static boolean hasPermission(Context activity, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (PackageManager.PERMISSION_GRANTED == activity.checkSelfPermission(permission));
        }
        return false;
    }

    public static void alertAndFinish(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.app_name).setMessage(activity.getString(R.string.permissions_denial));

        // Add the buttons.
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static Boolean canAccessCoarseLocation(Activity activity) {
        return (PermissionUtils.hasPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION));
    }

    public static Boolean canReadPhoneState(Activity activity) {
        return (PermissionUtils.hasPermission(activity, Manifest.permission.READ_PHONE_STATE));
    }

    public static Boolean canAccessNetworkState(Activity activity) {
        return (PermissionUtils.hasPermission(activity, Manifest.permission.ACCESS_NETWORK_STATE));
    }

    /*public static Boolean canReadCellBroadcasts(Activity activity) {
        return (PermissionUtils.hasPermission(activity, Manifest.permission.READ_CELL_BROADCASTS));
    }*/

    public static Boolean canReadExternalStorage(Activity activity) {
        return (PermissionUtils.hasPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE));
    }

    public static Boolean canWriteExternalStorage(Activity activity) {
        return (PermissionUtils.hasPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }

}
