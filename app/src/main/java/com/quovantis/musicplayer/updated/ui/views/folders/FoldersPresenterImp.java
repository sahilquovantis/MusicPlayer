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
        //iFoldersInteractor = new FoldersInteractorImp(this);
    }

    /**
     * Call Interactor for Folders List {@link FoldersInteractorImp}
     * @param context
     */
    @Override
    public void updateUI(Context context) {
        if (mFoldersView != null)
            mFoldersView.showProgress();
       // iFoldersInteractor.getFoldersList(context);
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
}
