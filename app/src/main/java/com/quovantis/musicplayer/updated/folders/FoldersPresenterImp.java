package com.quovantis.musicplayer.updated.folders;

import android.content.Context;

import com.quovantis.musicplayer.updated.models.SongPathModel;

import java.util.ArrayList;

/**
 * Created by sahil-goel on 23/8/16.
 */
public class FoldersPresenterImp implements IFoldersPresenter, IFoldersInteractor.Listener{

    private IFolderView mFoldersView;
    private IFoldersInteractor iFoldersInteractor;

    public FoldersPresenterImp(IFolderView mFoldersView) {
        this.mFoldersView = mFoldersView;
        iFoldersInteractor = new FoldersInteractorImp(this);
    }

    @Override
    public void updateUI(Context context) {
        mFoldersView.showProgress();
        iFoldersInteractor.getFoldersList(context);
    }

    @Override
    public void onUpdateFoldersList(ArrayList<SongPathModel> list) {
        mFoldersView.onUpdateFoldersList(list);
        mFoldersView.hideProgress();
    }

    @Override
    public void onRefreshMusicListProgress(int value, int size) {
        mFoldersView.updateRefreshListProgress(size, value);
    }

    @Override
    public void onRefreshMusicListFetchedSongs(int value, int size) {
        mFoldersView.updateRefreshListFetchedFolders(size, value);
    }

    @Override
    public void onCancelRefreshMusicListDialog() {
        mFoldersView.cancelRefreshListDialog();
    }

    @Override
    public void onIntitalzeRefreshMusicListDialog() {
        mFoldersView.initializeRefreshListDialog();
    }
}
