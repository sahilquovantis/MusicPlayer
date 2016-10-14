package com.quovantis.musicplayer.updated.ui.views.playlistsongslist;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.ArrayList;

/**
 * Created by sahil-goel on 14/10/16.
 */

public interface IPlaylistSongsView {
    void onUpdateUI(ArrayList<SongDetailsModel> list);

    void onShowProgress();

    void onHideProgress();

    void onEmptyList();
}
