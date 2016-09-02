package com.quovantis.musicplayer.updated.ui.views.search;

import android.content.Context;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.List;

/**
 * Created by sahil-goel on 2/9/16.
 */
public interface ISearchInteractor {
    void fetchSongsList(ISearchInteractor.Listener listener);

    interface Listener {
        void onFetchingList(List<SongDetailsModel> list);
    }
}
