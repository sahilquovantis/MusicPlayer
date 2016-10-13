package com.quovantis.musicplayer.updated.ui.views.playlists;

import com.quovantis.musicplayer.updated.models.UserPlaylistModel;

import java.util.List;

/**
 * Created by sahil-goel on 30/8/16.
 */
interface IPlaylistInteractor {
    void getPlaylists(IPlaylistInteractor.Listener listener);

    void renamePlaylist(UserPlaylistModel model, String newName, Listener listener);

    interface Listener {
        void onGettingPlaylists(List<UserPlaylistModel> list);

        void onSuccessfullyRenaming();
    }
}
