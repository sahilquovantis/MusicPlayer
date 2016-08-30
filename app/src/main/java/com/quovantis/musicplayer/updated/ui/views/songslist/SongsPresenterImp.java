package com.quovantis.musicplayer.updated.ui.views.songslist;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.ArrayList;

/**
 * Created by sahil-goel on 24/8/16.
 */
public class SongsPresenterImp implements ISongsPresenter, ISongsInteractor.Listener {
    private ISongsView iSongsView;
    private ISongsInteractor iSongsInteractor;

    public SongsPresenterImp(ISongsView iSongsView) {
        this.iSongsView = iSongsView;
        iSongsInteractor = new SongsInteractorImp();
    }

    @Override
    public void updateUI(long id) {
        if(iSongsView != null) {
            iSongsView.onShowProgress();
            iSongsInteractor.getSongsList(id, this);
        }
    }

    @Override
    public void onUpdateSongsList(ArrayList<SongDetailsModel> list) {
        if (iSongsView != null) {
            iSongsView.onUpdateUI(list);
            iSongsView.onHideProgres();
        }
    }

    @Override
    public void onDestroy() {
        iSongsView = null;
    }
}