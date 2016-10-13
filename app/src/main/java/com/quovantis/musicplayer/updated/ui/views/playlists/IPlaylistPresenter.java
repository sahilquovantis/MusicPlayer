package com.quovantis.musicplayer.updated.ui.views.playlists;

import com.quovantis.musicplayer.updated.models.UserPlaylistModel;

import java.util.List;

/**
 * Created by sahil-goel on 30/8/16.
 */
interface IPlaylistPresenter {
    void updateUI();

    void filterResults(List<UserPlaylistModel> list, String query);

    void onDestroy();

    void renamePlaylist(UserPlaylistModel model, String newName);
}
