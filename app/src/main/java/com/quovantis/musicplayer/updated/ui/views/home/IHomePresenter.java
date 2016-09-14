package com.quovantis.musicplayer.updated.ui.views.home;

import android.app.Activity;
import android.content.Context;

/**
 * Created by sahil-goel on 11/9/16.
 */
public interface IHomePresenter {
    void onDestroy();

    void getSongsListFromFolder(String path, boolean isClearQueue, boolean isPlaythisSong, Context context,
                                Activity activity);
}
