package com.quovantis.musicplayer.updated.ui.views.songslist;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.ArrayList;

/**
 * Created by sahil-goel on 24/8/16.
 */
public interface ISongsView {
    void onUpdateUI(ArrayList<SongDetailsModel> list);
    void onShowProgress();
    void onHideProgres();
}
