package com.quovantis.musicplayer.updated.ui.views.currentplaylist;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.ArrayList;

/**
 * Created by sahil-goel on 29/8/16.
 */
public interface ICurrentPlaylistView {
    void onShowProgress();
    void onHideProgress();
    void onUpdateUI(ArrayList<SongDetailsModel> currentPlaylistList);
    void onEmptyList();
    void onCurrentPlayingSongRemoved();
    void onCancelCreatePlaylistProgressDialog(boolean isCreated);
}
