package com.quovantis.musicplayer.updated.ui.views.playlistsongs;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.List;

/**
 * Created by sahil-goel on 30/8/16.
 */
public interface IPlaylistSongsView {
    void onUpdateUI(List<SongDetailsModel> list);

    void onShowProgress();

    void onHideProgress();

    void onEmptyList();
}
