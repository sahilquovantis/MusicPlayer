package com.quovantis.musicplayer.updated.dialogs;

import android.content.Context;

/**
 * Created by sahil-goel on 29/8/16.
 */
public class ProgresDialog {
    public static android.app.ProgressDialog showProgressDialog(Context context,String message) {
        android.app.ProgressDialog dialog = new android.app.ProgressDialog(context);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }

    public static void cancelProgressDialog(android.app.ProgressDialog dialog) {
        if (dialog != null)
            dialog.dismiss();
    }
}
