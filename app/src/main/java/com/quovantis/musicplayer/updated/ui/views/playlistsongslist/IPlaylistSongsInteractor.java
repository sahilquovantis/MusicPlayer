package com.quovantis.musicplayer.updated.ui.views.playlistsongslist;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.ArrayList;

/**
 * Created by sahil-goel on 14/10/16.
 */

interface IPlaylistSongsInteractor {
    void getSongsList(long id, Listener listener);

    void removeSongFromPlaylist(long playlistId, SongDetailsModel model, Listener listener);

    interface Listener {
        void onUpdateSongsList(ArrayList<SongDetailsModel> list);
    }
}
