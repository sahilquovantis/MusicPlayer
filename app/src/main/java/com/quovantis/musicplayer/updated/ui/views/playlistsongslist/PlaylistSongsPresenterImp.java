package com.quovantis.musicplayer.updated.ui.views.playlistsongslist;

import android.content.Context;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.ArrayList;

/**
 * Created by sahil-goel on 14/10/16.
 */

public class PlaylistSongsPresenterImp implements IPlaylistSongsPresenter, IPlaylistSongsInteractor.Listener {
    private IPlaylistSongsView iSongsView;
    private IPlaylistSongsInteractor iSongsInteractor;

    PlaylistSongsPresenterImp(Context context, IPlaylistSongsView iSongsView) {
        this.iSongsView = iSongsView;
        iSongsInteractor = new PlaylistSongsInteractorImp();
    }

    @Override
    public void onUpdateSongsList(ArrayList<SongDetailsModel> list) {
        if (iSongsView != null) {
            if (list == null || list.isEmpty())
                iSongsView.onEmptyList();
            else
                iSongsView.onUpdateUI(list);
            iSongsView.onHideProgress();
        }
    }

    @Override
    public void updateUI(long id) {
        if (iSongsView != null) {
            iSongsView.onShowProgress();
            iSongsInteractor.getSongsList(id, this);
        }
    }

    @Override
    public void onDestroy() {
        iSongsView = null;
    }
}
