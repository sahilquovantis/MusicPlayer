package com.quovantis.musicplayer.updated.ui.views.search;

import android.app.Activity;
import android.content.Context;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.List;

/**
 * Created by sahil-goel on 2/9/16.
 */
public interface ISearchPresenter {
    void onDestroy();

    void fetchSongsList(Context context, Activity activity);

    void filterResults(List<SongDetailsModel> list, String query);
}
