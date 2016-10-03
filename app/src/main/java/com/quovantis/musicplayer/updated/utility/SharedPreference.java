package com.quovantis.musicplayer.updated.utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.quovantis.musicplayer.updated.constants.AppPreferenceKeys;

/**
 * App Preferences
 */
public class SharedPreference {

    private static SharedPreference sInstance;

    public static SharedPreference getInstance() {
        if (sInstance == null)
            sInstance = new SharedPreference();
        return sInstance;
    }

    public void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppPreferenceKeys.USER_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getBoolean(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppPreferenceKeys.USER_INFO, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, true);
    }

    public void putInt(Context context, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppPreferenceKeys.USER_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public int getInt(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppPreferenceKeys.USER_INFO, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }
}
