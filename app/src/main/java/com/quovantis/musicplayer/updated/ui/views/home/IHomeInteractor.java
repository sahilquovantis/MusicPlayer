package com.quovantis.musicplayer.updated.ui.views.home;

import android.app.Activity;
import android.content.Context;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.ui.views.folders.IFoldersInteractor;

import java.util.List;

/**
 * Created by sahil-goel on 11/9/16.
 */
public interface IHomeInteractor {
    void getSongsListFromFolder(String path, boolean isClearQueue, final boolean isPlaythisSong, Context context, Activity activity,
                                IHomeInteractor.Listener listener);

    interface Listener {
        void onSuccess(List<SongDetailsModel> list, boolean isClearQueue, boolean isPlaythisSong);
    }
}
