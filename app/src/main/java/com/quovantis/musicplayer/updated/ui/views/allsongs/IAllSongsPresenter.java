package com.quovantis.musicplayer.updated.ui.views.allsongs;

import android.app.Activity;
import android.content.Context;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.List;

/**
 * Created by sahil-goel on 11/9/16.
 */
public interface IAllSongsPresenter {
    void getSongsList(Context context, Activity activity);

    void filterResults(List<SongDetailsModel> list, String query);

    void onDestroy();
}
