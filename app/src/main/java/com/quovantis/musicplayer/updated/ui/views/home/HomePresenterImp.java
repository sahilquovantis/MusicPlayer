package com.quovantis.musicplayer.updated.ui.views.home;

import android.content.Context;

/**
 * Created by sahil-goel on 11/9/16.
 */
public class HomePresenterImp implements IHomePresenter,IHomeInteractor.Listener{
    private IHomeView mFoldersView;
    private IHomeInteractor iHomeInteractor;

    public HomePresenterImp(IHomeView iHomeView) {
        this.mFoldersView = iHomeView;
        iHomeInteractor = new HomeInteractorImp(this);
    }

    @Override
    public void onDestroy() {
        mFoldersView = null;
    }

    @Override
    public void syncMusic(Context context) {
        iHomeInteractor.resyncMusic(context);
    }

    @Override
    public void firstTimeSync(Context context) {
        iHomeInteractor.firstTimeSync(context);
    }

    @Override
    public void onRefreshMusicListProgress(int value, int size) {
        if (mFoldersView != null)
            mFoldersView.updateRefreshListProgress(size, value);
    }

    @Override
    public void onRefreshMusicListFetchedSongs(int value, int size) {
        if (mFoldersView != null)
            mFoldersView.updateRefreshListFetchedFolders(size, value);
    }

    @Override
    public void onCancelRefreshMusicListDialog() {
        if (mFoldersView != null)
            mFoldersView.cancelRefreshListDialog();
    }

    @Override
    public void onIntitalzeRefreshMusicListDialog() {
        if (mFoldersView != null)
            mFoldersView.initializeRefreshListDialog();
    }

    @Override
    public void onRefreshSuccess() {
        if(mFoldersView != null)
            mFoldersView.onRefreshSuccess();
    }
}
