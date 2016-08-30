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

    void resyncMusic(Context context);

    interface RefreshSongsListListener {
        void onRefreshMusicListUpdateProgress(int value, int total);

        void onRefreshMusicListUpdateFetchedSongs(int value, int total);

        void onIntitalizeRefreshMusicListDialog();

        void onCancelRefreshMusicListDialog();

        void onRefreshListSuccessfully();
    }

    interface Listener {
        void onUpdateFoldersList(ArrayList<SongPathModel> list);

        void onRefreshMusicListProgress(int value, int size);

        void onRefreshMusicListFetchedSongs(int value, int size);

        void onCancelRefreshMusicListDialog();

        void onIntitalzeRefreshMusicListDialog();
    }
}
