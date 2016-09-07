package com.quovantis.musicplayer.updated.ui.views.createplaylist;

import com.quovantis.musicplayer.updated.models.UserPlaylistModel;

import java.util.List;

/**
 * Created by sahil-goel on 7/9/16.
 */
public interface ICreatePlaylistInteractor {
    void getPlaylists(ICreatePlaylistInteractor.Listener listener);

    void createNewPlaylist(String name, String id, String action,
                           ICreatePlaylistInteractor.Listener listener);

    void addToExistingPlaylist(UserPlaylistModel model, String id, String action,
                               ICreatePlaylistInteractor.Listener listener);

    interface Listener {
        void onGettingPlaylists(List<UserPlaylistModel> list);

        void onPlaylistCreated(boolean isCreated);
    }
}
