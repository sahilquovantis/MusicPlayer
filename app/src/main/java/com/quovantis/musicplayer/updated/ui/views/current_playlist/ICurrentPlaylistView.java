package com.quovantis.musicplayer.updated.ui.views.current_playlist;

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
}
