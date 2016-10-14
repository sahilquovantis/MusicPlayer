package com.quovantis.musicplayer.updated.ui.views.songslist;

import android.app.Activity;
import android.content.Context;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.ArrayList;

/**
 * Created by sahil-goel on 24/8/16.
 */
public interface ISongsInteractor {
    void getSongsList(String path, ISongsInteractor.Listener listener, Activity activity);

    interface Listener {
        void onUpdateSongsList(ArrayList<SongDetailsModel> list);
    }
}
