package com.quovantis.musicplayer.updated.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.quovantis.musicplayer.R;

/**
 * Created by sahil-goel on 29/8/16.
 */
public class ProgresDialog {
    public static android.app.ProgressDialog showProgressDialog(Context context, String message) {
        android.app.ProgressDialog dialog = new android.app.ProgressDialog(context);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }

    public static Dialog showProgressDialog(Context context) {
        View mView = LayoutInflater.from(context).inflate(R.layout.custom_progress_dialog_without_message, null);
        WindowManager.LayoutParams mDialogLayoutParams = new WindowManager.LayoutParams();
        Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialogLayoutParams.copyFrom(dialog.getWindow().getAttributes());
        mDialogLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mDialogLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        mDialogLayoutParams.gravity = Gravity.CENTER;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(mView);
        dialog.show();
        dialog.getWindow().setAttributes(mDialogLayoutParams);
        return dialog;
    }

    public static void cancelProgressDialog(android.app.ProgressDialog dialog) {
        if (dialog != null)
            dialog.dismiss();
    }
}
