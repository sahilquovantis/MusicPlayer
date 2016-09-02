package com.quovantis.musicplayer.updated.ui.views.search;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.List;

/**
 * Created by sahil-goel on 2/9/16.
 */
public interface ISearchView {
    void onShowProgress();

    void onHideProgress();

    void onUpdateUI(List<SongDetailsModel> list, String query);

    void onFetchingAllSongList(List<SongDetailsModel> list);
}
