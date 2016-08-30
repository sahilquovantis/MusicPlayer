package com.quovantis.musicplayer.updated.ui.views.playlistsongs;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.List;

/**
 * Created by sahil-goel on 30/8/16.
 */
public class PlaylistSongsPresenterImp implements IPlaylistSongsPresenter, IPlaylistSongsInteractor.Listener {
    private IPlaylistSongsView iView;
    private IPlaylistSongsInteractor iPlaylistSongsInteractor;

    public PlaylistSongsPresenterImp(IPlaylistSongsView iView) {
        this.iView = iView;
        iPlaylistSongsInteractor = new PlaylistSongsInteractorImp();
    }

    @Override
    public void updateUI(long id) {
        if (iView != null)
            iView.onShowProgress();
        iPlaylistSongsInteractor.getSongsList(id, this);
    }

    @Override
    public void onDestroy() {
        iView = null;
    }

    @Override
    public void onUpdateSongsList(List<SongDetailsModel> list) {
        if (iView != null) {
            iView.onHideProgress();
            if (list == null || list.isEmpty()) {
                iView.onEmptyList();
            } else {
                iView.onUpdateUI(list);
            }
        }
    }
}
