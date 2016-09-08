package com.quovantis.musicplayer.updated.utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.quovantis.musicplayer.updated.interfaces.ICommonKeys;

/**
 * Created by sahil-goel on 23/8/16.
 */
public class SharedPrefrence implements ICommonKeys {

    public static void saveSharedPrefrenceBoolean(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, false);
        editor.commit();
    }

    public static boolean getSharedPrefrenceBoolean(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, true);
    }

    public static void saveCurrentPosition(Context context, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getCurrentPosition(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }
}
