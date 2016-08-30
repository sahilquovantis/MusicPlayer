package com.quovantis.musicplayer.updated.interfaces;

import com.quovantis.musicplayer.updated.models.SongPathModel;

/**
 * Created by sahil-goel on 24/8/16.
 */
public interface IFolderClickListener {
    void onFoldersLongPress(SongPathModel songPathModel);
    void onFoldersSinglePress(long id, String directoryName);
}
