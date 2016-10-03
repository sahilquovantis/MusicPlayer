package com.quovantis.musicplayer.updated.application;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Custom Application Class For Realm Configuration.
 */
public class MusicPlayerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
