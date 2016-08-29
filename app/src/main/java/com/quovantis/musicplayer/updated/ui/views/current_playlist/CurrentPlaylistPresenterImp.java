package com.quovantis.musicplayer.updated.ui.views.current_playlist;

import android.content.Context;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.ArrayList;

/**
 * Created by sahil-goel on 29/8/16.
 */
public class CurrentPlaylistPresenterImp implements ICurrentPlaylistPresenter,
        ICurrentPlaylistInteractor.Listener {

    private ICurrentPlaylistView mView;
    private ICurrentPlaylistInteractor iCurrentPlaylistInteractor;

    public CurrentPlaylistPresenterImp(ICurrentPlaylistView mView) {
        this.mView = mView;
        iCurrentPlaylistInteractor = new CurrentPlaylistInteractorImp();
    }

    @Override
    public void updateUI() {
        if (mView != null) {
            mView.onShowProgress();
            iCurrentPlaylistInteractor.getCurrentPlaylist(this);
        }
    }

    @Override
    public void onDestroy() {
        mView = null;
    }

    @Override
    public void onUpdateUI(ArrayList<SongDetailsModel> list) {
        if (mView != null) {
            if (list != null && !list.isEmpty())
                mView.onUpdateUI(list);
            else
                mView.onEmptyList();
            mView.onHideProgress();
        }
    }
}
