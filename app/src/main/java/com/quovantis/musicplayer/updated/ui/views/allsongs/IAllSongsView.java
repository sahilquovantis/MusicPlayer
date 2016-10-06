package com.quovantis.musicplayer.updated.ui.views.allsongs;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.models.SongPathModel;

import java.util.List;

/**
 * Created by sahil-goel on 11/9/16.
 */
public interface IAllSongsView {
    void updateUi(List<SongDetailsModel> list);

    void onFetchingAllSongsList(List<SongDetailsModel> songsList);

    void showProgress();

    void hideProgress();
}
