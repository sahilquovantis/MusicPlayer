package com.quovantis.musicplayer.updated.ui.views.allsongs;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.List;

/**
 * Created by sahil-goel on 11/9/16.
 */
public interface IAllSongsView {
    void updateUi(List<SongDetailsModel> list);

    void showProgress();

    void hideProgress();
}
