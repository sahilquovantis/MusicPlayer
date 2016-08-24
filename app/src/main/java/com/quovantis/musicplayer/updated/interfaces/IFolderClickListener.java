package com.quovantis.musicplayer.updated.interfaces;

/**
 * Created by sahil-goel on 24/8/16.
 */
public interface IFolderClickListener {
    void onFoldersLongPress();
    void onFoldersSinglePress(long id, String directoryName);
}
