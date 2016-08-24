package com.quovantis.musicplayer.updated.songslist;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.ArrayList;

/**
 * Created by sahil-goel on 24/8/16.
 */
public interface ISongsInteractor {
    void getSongsList(long id, ISongsInteractor.Listener listener);

    interface Listener {
        void onUpdateSongsList(ArrayList<SongDetailsModel> list);
    }
}
