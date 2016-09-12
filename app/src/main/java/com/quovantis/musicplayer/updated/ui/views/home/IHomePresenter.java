package com.quovantis.musicplayer.updated.ui.views.home;

import android.content.Context;

/**
 * Created by sahil-goel on 11/9/16.
 */
public interface IHomePresenter {
    void onDestroy();

    void syncMusic(Context context);

    void firstTimeSync(Context context);
}
