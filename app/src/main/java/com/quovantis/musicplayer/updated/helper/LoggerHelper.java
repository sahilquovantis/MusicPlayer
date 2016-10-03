package com.quovantis.musicplayer.updated.helper;

import android.util.Log;

/**
 * Created by sahil-goel on 3/10/16.
 */

public class LoggerHelper {
    private static final String TAG = "Training";
    private static boolean isDebugEnabled = true;

    public static void debug(String message) {
        if (isDebugEnabled) {
            Log.d(TAG, message);
        }
    }

    public static void setIsDebugEnabled(boolean value) {
        isDebugEnabled = value;
    }
}
