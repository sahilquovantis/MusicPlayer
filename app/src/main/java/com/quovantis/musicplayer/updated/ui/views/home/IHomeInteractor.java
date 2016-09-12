package com.quovantis.musicplayer.updated.ui.views.home;

import android.content.Context;

import com.quovantis.musicplayer.updated.models.SongPathModel;

import java.util.ArrayList;

/**
 * Created by sahil-goel on 11/9/16.
 */
public interface IHomeInteractor {
    void firstTimeSync(Context context);
    void resyncMusic(Context context);

    interface RefreshSongsListListener {
        void onRefreshMusicListUpdateProgress(int value, int total);

        void onRefreshMusicListUpdateFetchedSongs(int value, int total);

        void onIntitalizeRefreshMusicListDialog();

        void onCancelRefreshMusicListDialog();

        void onRefreshListSuccessfully();
    }

    interface Listener {
        void onRefreshMusicListProgress(int value, int size);

        void onRefreshMusicListFetchedSongs(int value, int size);

        void onCancelRefreshMusicListDialog();

        void onIntitalzeRefreshMusicListDialog();

        void onRefreshSuccess();
    }
}
