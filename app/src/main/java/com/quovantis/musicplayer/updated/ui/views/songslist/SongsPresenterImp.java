package com.quovantis.musicplayer.updated.ui.views.songslist;

import android.app.Activity;
import android.content.Context;

import com.quovantis.musicplayer.updated.constants.AppKeys;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.ArrayList;

/**
 * Created by sahil-goel on 24/8/16.
 */
class SongsPresenterImp implements ISongsPresenter, ISongsInteractor.Listener {
    private ISongsView iSongsView;
    private ISongsInteractor iSongsInteractor;

    SongsPresenterImp(ISongsView iSongsView) {
        this.iSongsView = iSongsView;
        iSongsInteractor = new SongsInteractorImp();
    }

    @Override
    public void updateUI(String path, Activity activity) {
        if (iSongsView != null) {
            iSongsView.onShowProgress();
            iSongsInteractor.getSongsList(path, this, activity);
        }
    }

    @Override
    public void onUpdateSongsList(ArrayList<SongDetailsModel> list) {
        if (iSongsView != null) {
            if (list == null || list.isEmpty())
                iSongsView.onEmptyList();
            else
                iSongsView.onUpdateUI(list);
            iSongsView.onHideProgress();
        }
    }

    @Override
    public void onDestroy() {
        iSongsView = null;
    }
}
