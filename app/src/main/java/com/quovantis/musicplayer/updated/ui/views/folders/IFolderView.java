package com.quovantis.musicplayer.updated.ui.views.folders;

import com.quovantis.musicplayer.updated.models.SongPathModel;

import java.util.List;

/**
 * Created by sahil-goel on 23/8/16.
 */
public interface IFolderView {
    void onUpdateFoldersList(List<SongPathModel> foldersList);

    void onFetchingAllFoldersList(List<SongPathModel> foldersList);

    void showProgress();

    void hideProgress();

    void showEmptyMessage();
}
