package com.quovantis.musicplayer.updated.ui.views.playlistsongslist;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

/**
 * Created by sahil-goel on 14/10/16.
 */

public interface IPlaylistSongsPresenter {
    void updateUI(long id);

    void onDestroy();

    void removeFromPlaylist(long playlistId, SongDetailsModel model);
}
