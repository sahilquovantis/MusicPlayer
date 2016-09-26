package com.quovantis.musicplayer.updated.ui.views.fullscreenmusiccontrols;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.ArrayList;

/**
 * Created by sahil-goel on 29/8/16.
 */
public interface ICurrentPlaylistView {
    void onShowProgress();

    void onHideProgress();

    void onEmptyList();

    void onCurrentPlayingSongRemoved();

    void onSuccessfullyRemovedSong();
}
