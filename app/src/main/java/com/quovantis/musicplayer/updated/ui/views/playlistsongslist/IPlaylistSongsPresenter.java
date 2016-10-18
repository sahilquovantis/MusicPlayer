package com.quovantis.musicplayer.updated.ui.views.playlistsongslist;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.List;

/**
 * Created by sahil-goel on 14/10/16.
 */
interface IPlaylistSongsPresenter {
    void updateUI(long id);

    void onDestroy();

    void removeFromPlaylist(long playlistId, SongDetailsModel model);

    void removeFromPlaylist(long playlistId, int pos);

    void saveToPlaylist(List<SongDetailsModel> list, long playlistId);
}
