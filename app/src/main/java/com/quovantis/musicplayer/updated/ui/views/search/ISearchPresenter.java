package com.quovantis.musicplayer.updated.ui.views.search;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.List;

/**
 * Created by sahil-goel on 2/9/16.
 */
public interface ISearchPresenter {
    void onDestroy();
    void fetchSongsList();
    void filterResults(List<SongDetailsModel> list, String query);
}
