package com.quovantis.musicplayer.updated.ui.views.songslist;

import android.app.Activity;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.ArrayList;

/**
 * Created by sahil-goel on 24/8/16.
 */
public interface ISongsInteractor {
    void getSongsList(String path, String action, ISongsInteractor.Listener listener, Activity activity);
    void getSongsList(long id,String action, ISongsInteractor.Listener listener);

    interface Listener {
        void onUpdateSongsList(ArrayList<SongDetailsModel> list);
    }
}
