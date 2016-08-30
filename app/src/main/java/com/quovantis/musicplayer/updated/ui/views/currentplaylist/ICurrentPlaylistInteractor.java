package com.quovantis.musicplayer.updated.ui.views.currentplaylist;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.ArrayList;

/**
 * Created by sahil-goel on 29/8/16.
 */
public interface ICurrentPlaylistInteractor {
    void getCurrentPlaylist(ICurrentPlaylistInteractor.Listener listener);

    void createPlaylist(String name,ICurrentPlaylistInteractor.Listener listener);

    interface Listener {
        void onUpdateUI(ArrayList<SongDetailsModel> list);

        void onPlaylistCreated(boolean isCreated);
    }
}
