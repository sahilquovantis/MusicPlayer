package com.quovantis.musicplayer.updated.ui.views.createplaylist;

import com.quovantis.musicplayer.updated.models.UserPlaylistModel;

import java.util.List;

/**
 * Created by sahil-goel on 7/9/16.
 */
public interface ICreatePlaylistView {
    void updateUI(List<UserPlaylistModel> list);

    void showProgress();

    void hidePRogress();

    void onCancelCreatePlaylistProgressDialog(boolean isCreated);
}
