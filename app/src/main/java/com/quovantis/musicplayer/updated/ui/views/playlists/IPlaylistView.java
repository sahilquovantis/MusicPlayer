package com.quovantis.musicplayer.updated.ui.views.playlists;

import com.quovantis.musicplayer.updated.models.UserPlaylistModel;

import java.util.List;

/**
 * Created by sahil-goel on 30/8/16.
 */
public interface IPlaylistView {
    void onUpdateUI(List<UserPlaylistModel> list);

    void onFetchingPlaylist(List<UserPlaylistModel> playlistList);

    void onShowProgress();

    void onHideProgres();

    void onNoPlaylist();

    void onSuccessfullyRenaming();
}
