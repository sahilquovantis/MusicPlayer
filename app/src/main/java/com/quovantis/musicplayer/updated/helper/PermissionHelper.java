package com.quovantis.musicplayer.updated.helper;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.quovantis.musicplayer.updated.interfaces.ICommonKeys;

/**
 * Created by sahil-goel on 15/9/16.
 */
public class PermissionHelper {
    public static boolean checkPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_CONTACTS)) {
                createDialog(activity);
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        ICommonKeys.MY_PERMISSIONS_REQUEST_READ_STORAGE);
            }
            return false;
        } else {
            return true;
        }
    }

    private static void createDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Permission");
        builder.setCancelable(false);
        builder.setMessage("Read Storage Permission is needed for reading the songs. Please, grant permission from the settings.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
