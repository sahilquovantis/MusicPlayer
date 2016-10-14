package com.quovantis.musicplayer.updated.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.quovantis.musicplayer.R;

/**
 * Custom Progress Dialog for progress.
 */
public class CustomProgressDialog extends AlertDialog {

    public CustomProgressDialog(Context context) {
        super(context);
        setDialogTitle();
        setCancelable(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_progress_dialog);
    }

    public void setMessage(String message) {
        TextView messageTV = (TextView) findViewById(R.id.tv_progress_dialog_message);
        messageTV.setText(message);
    }

    private void setDialogTitle() {
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
    }
}
