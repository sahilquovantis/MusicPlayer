package com.quovantis.musicplayer.updated.ui.views.folders;

import android.content.Context;

import com.quovantis.musicplayer.updated.models.SongPathModel;

import java.util.ArrayList;

/**
 * Created by sahil-goel on 23/8/16.
 */
public interface IFoldersInteractor {
    void getFoldersList(Context context);

    void onFetchedSongs();

    interface Listener {
        void onUpdateFoldersList(ArrayList<SongPathModel> list);
    }
}
