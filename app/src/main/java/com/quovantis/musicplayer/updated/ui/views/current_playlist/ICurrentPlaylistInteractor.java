package com.quovantis.musicplayer.updated.ui.views.current_playlist;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.ArrayList;

/**
 * Created by sahil-goel on 29/8/16.
 */
public interface ICurrentPlaylistInteractor {
    void getCurrentPlaylist(ICurrentPlaylistInteractor.Listener listener);

    interface Listener {
        void onUpdateUI(ArrayList<SongDetailsModel> list);
    }
}
