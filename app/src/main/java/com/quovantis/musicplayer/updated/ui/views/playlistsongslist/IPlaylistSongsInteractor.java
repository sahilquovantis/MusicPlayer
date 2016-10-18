package com.quovantis.musicplayer.updated.ui.views.playlistsongslist;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sahil-goel on 14/10/16.
 */

interface IPlaylistSongsInteractor {
    void getSongsList(long id, Listener listener);

    void removeSongFromPlaylist(long playlistId, SongDetailsModel model, Listener listener);

    void removeSongFromPlaylist(long playlistId, int pos, Listener listener);

    void saveToPlaylist(List<SongDetailsModel> list, long playlistId, Listener listener);

    interface Listener {
        void onUpdateSongsList(ArrayList<SongDetailsModel> list);
    }
}
