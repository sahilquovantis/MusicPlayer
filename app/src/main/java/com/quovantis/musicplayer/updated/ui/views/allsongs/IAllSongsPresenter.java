package com.quovantis.musicplayer.updated.ui.views.allsongs;

import android.app.Activity;
import android.content.Context;

/**
 * Created by sahil-goel on 11/9/16.
 */
public interface IAllSongsPresenter {
    void getSongsList(Context context, Activity activity);

    void onDestroy();
}
