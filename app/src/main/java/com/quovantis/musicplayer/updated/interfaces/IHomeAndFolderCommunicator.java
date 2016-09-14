package com.quovantis.musicplayer.updated.interfaces;

import com.quovantis.musicplayer.updated.models.SongPathModel;

/**
 * Created by sahil-goel on 11/9/16.
 */
public interface IHomeAndFolderCommunicator {
    void onOptionsDialogClickFromFolders(SongPathModel model, boolean isClearQueue, boolean isPlaythisSong);
}
