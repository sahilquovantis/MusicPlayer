package com.quovantis.musicplayer.updated.ui.views.songslist;

import android.app.Activity;

/**
 * Created by sahil-goel on 24/8/16.
 */
public interface ISongsPresenter {
    void updateUI(long id, String action, String path, Activity activity);
    void onDestroy();
}
