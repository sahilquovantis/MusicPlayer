package com.quovantis.musicplayer.updated.ui.views.playlistsongs;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.List;

/**
 * Created by sahil-goel on 30/8/16.
 */
public interface IPlaylistSongsInteractor {
    void getSongsList(long id, IPlaylistSongsInteractor.Listener listener);

    interface Listener {
        void onUpdateSongsList(List<SongDetailsModel> list);
    }
}
