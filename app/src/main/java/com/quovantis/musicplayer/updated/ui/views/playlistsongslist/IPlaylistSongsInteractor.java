package com.quovantis.musicplayer.updated.ui.views.playlistsongslist;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.ArrayList;

/**
 * Created by sahil-goel on 14/10/16.
 */

public interface IPlaylistSongsInteractor {
    void getSongsList(long id, Listener listener);

    interface Listener {
        void onUpdateSongsList(ArrayList<SongDetailsModel> list);
    }
}
