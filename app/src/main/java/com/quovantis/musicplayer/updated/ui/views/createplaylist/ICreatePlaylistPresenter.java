package com.quovantis.musicplayer.updated.ui.views.createplaylist;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.models.UserPlaylistModel;

import java.util.List;

/**
 * Created by sahil-goel on 7/9/16.
 */
public interface ICreatePlaylistPresenter {
    void updateUI();

    void createPlaylist(String name, String id, String action);

    void addToExistingPlaylist(UserPlaylistModel model, String id, String action);

    void onDestory();
}
