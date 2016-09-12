package com.quovantis.musicplayer.updated.ui.views.home;

/**
 * Created by sahil-goel on 11/9/16.
 */
public interface IHomeView {
    void updateRefreshListProgress(int size, int value);

    void updateRefreshListFetchedFolders(int size, int value);

    void cancelRefreshListDialog();

    void initializeRefreshListDialog();

    void onRefreshSuccess();
}
