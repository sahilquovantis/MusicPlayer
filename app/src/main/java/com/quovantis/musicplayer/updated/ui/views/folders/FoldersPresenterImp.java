package com.quovantis.musicplayer.updated.ui.views.folders;

import android.content.Context;

import com.quovantis.musicplayer.updated.models.SongPathModel;

import java.util.ArrayList;

/**
 * Created by sahil-goel on 23/8/16.
 */
public class FoldersPresenterImp implements IFoldersPresenter, IFoldersInteractor.Listener {

    private IFolderView mFoldersView;
    private IFoldersInteractor iFoldersInteractor;

    public FoldersPresenterImp(IFolderView mFoldersView) {
        this.mFoldersView = mFoldersView;
        iFoldersInteractor = new FoldersInteractorImp(this);
    }

    /**
     * Call Interactor for Folders List {@link FoldersInteractorImp}
     * @param context
     */
    @Override
    public void updateUI(Context context) {
        if (mFoldersView != null)
            mFoldersView.showProgress();
        iFoldersInteractor.getFoldersList(context);
    }

    @Override
    public void onDestroy() {
        mFoldersView = null;
    }

    /**
     * Update the Folders Activity after getting songs {@link FoldersActivity}
     * @param list List of Folders
     */
    @Override
    public void onUpdateFoldersList(ArrayList<SongPathModel> list) {
        if (mFoldersView != null) {
            mFoldersView.onUpdateFoldersList(list);
            mFoldersView.hideProgress();
        }
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

    /**
     * Sync Music From the Storage
     * @param context
     */
    @Override
    public void syncMusic(Context context) {
        if(mFoldersView != null)
            mFoldersView.showProgress();
        iFoldersInteractor.resyncMusic(context);
    }
}
