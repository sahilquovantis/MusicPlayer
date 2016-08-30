package com.quovantis.musicplayer.updated.ui.views.folders;

import android.content.Context;

/**
 * Created by sahil-goel on 23/8/16.
 */
public interface IFoldersPresenter {
    void updateUI(Context context);
    void onDestroy();
    void syncMusic(Context context);
}
