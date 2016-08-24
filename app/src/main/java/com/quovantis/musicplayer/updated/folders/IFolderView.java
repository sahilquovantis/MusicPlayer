package com.quovantis.musicplayer.updated.folders;

import com.quovantis.musicplayer.updated.models.SongPathModel;

import java.util.ArrayList;

/**
 * Created by sahil-goel on 23/8/16.
 */
public interface IFolderView {
    void onUpdateFoldersList(ArrayList<SongPathModel> foldersList);

    void showProgress();

    void hideProgress();

    void showEmptyMessage();

    void updateRefreshListProgress(int size, int value);

    void updateRefreshListFetchedFolders(int size, int value);

    void cancelRefreshListDialog();

    void initializeRefreshListDialog();
}
