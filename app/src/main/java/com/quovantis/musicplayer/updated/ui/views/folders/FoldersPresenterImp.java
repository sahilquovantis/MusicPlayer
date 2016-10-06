package com.quovantis.musicplayer.updated.ui.views.folders;

import android.app.Activity;
import android.content.Context;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.models.SongPathModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sahil-goel on 23/8/16.
 */
public class FoldersPresenterImp implements IFoldersPresenter, IFoldersInteractor.Listener {

    private IFolderView iFolderView;
    private IFoldersInteractor iFoldersInteractor;

    public FoldersPresenterImp(IFolderView mFoldersView) {
        this.iFolderView = mFoldersView;
        iFoldersInteractor = new FoldersInteractorImp();
    }

    /**
     * Call Interactor for Folders List {@link FoldersInteractorImp}
     *
     * @param context
     */
    @Override
    public void updateUI(Context context, Activity activity) {
        if (iFolderView != null)
            iFolderView.showProgress();
        iFoldersInteractor.getFoldersList(context, activity, this);
    }

    @Override
    public void onDestroy() {
        iFolderView = null;
    }

    /**
     * Update the Folders Activity after getting songs {@link FoldersFragment}
     *
     * @param list List of Folders
     */
    @Override
    public void onUpdateFoldersList(ArrayList<SongPathModel> list) {
        if (iFolderView != null) {
            iFolderView.onFetchingAllFoldersList(list);
            iFolderView.hideProgress();
        }
    }

    @Override
    public void filterResults(List<SongPathModel> list, String query) {
        if (iFolderView != null) {
            iFolderView.showProgress();
            ArrayList<SongPathModel> filterList = new ArrayList<>();
            if (!list.isEmpty()) {
                for (SongPathModel model : list) {
                    String title = model.getDirectory().toLowerCase();
                    query = query.toLowerCase();
                    if (title.contains(query)) {
                        filterList.add(model);
                    }
                }
            }
            iFolderView.onUpdateFoldersList(filterList);
            iFolderView.hideProgress();
        }
    }
}
