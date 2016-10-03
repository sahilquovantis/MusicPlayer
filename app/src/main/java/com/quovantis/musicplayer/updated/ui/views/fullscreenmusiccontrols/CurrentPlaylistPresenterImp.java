package com.quovantis.musicplayer.updated.ui.views.fullscreenmusiccontrols;

/**
 * Created by sahil-goel on 29/8/16.
 */
public class CurrentPlaylistPresenterImp implements ICurrentPlaylistPresenter {

    private ICurrentPlaylistView mView;

    public CurrentPlaylistPresenterImp(ICurrentPlaylistView mView) {
        this.mView = mView;
    }

    @Override
    public void onDestroy() {
        mView = null;
    }

    @Override
    public void songsMoved(int fromPosition, int toPosition) {

    }

    @Override
    public void songRemoved(int position) {

    }

}
