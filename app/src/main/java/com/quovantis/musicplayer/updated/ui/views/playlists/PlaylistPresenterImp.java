package com.quovantis.musicplayer.updated.ui.views.playlists;

import com.quovantis.musicplayer.updated.models.UserPlaylistModel;

import java.util.ArrayList;
import java.util.List;

/**
 * User Playlist Presenter.
 */
class PlaylistPresenterImp implements IPlaylistPresenter, IPlaylistInteractor.Listener {

    private IPlaylistView iPlaylistView;
    private IPlaylistInteractor iPlaylistInteractor;

    PlaylistPresenterImp(IPlaylistView iPlaylistView) {
        this.iPlaylistView = iPlaylistView;
        iPlaylistInteractor = new PlaylistInteractorImp();
    }

    @Override
    public void updateUI() {
        if (iPlaylistView != null)
            iPlaylistView.onShowProgress();
        iPlaylistInteractor.getPlaylists(this);
    }

    @Override
    public void onDestroy() {
        iPlaylistView = null;
    }

    @Override
    public void onGettingPlaylists(List<UserPlaylistModel> list) {
        if (iPlaylistView != null) {
            iPlaylistView.onHideProgres();
            if (list == null || list.isEmpty()) {
                iPlaylistView.onNoPlaylist();
            } else {
                iPlaylistView.onFetchingPlaylist(list);
            }
        }
    }

    @Override
    public void filterResults(List<UserPlaylistModel> list1, String query) {
        if (iPlaylistView != null) {
            iPlaylistView.onShowProgress();
            ArrayList<UserPlaylistModel> list = new ArrayList<>();
            if (!list1.isEmpty()) {
                for (UserPlaylistModel model : list1) {
                    String title = model.getPlaylistName().toLowerCase();
                    query = query.toLowerCase();
                    if (title.contains(query)) {
                        list.add(model);
                    }
                }
            }
            iPlaylistView.onUpdateUI(list);
            iPlaylistView.onHideProgres();
        }
    }

    @Override
    public void renamePlaylist(UserPlaylistModel model, String newName) {
        iPlaylistInteractor.renamePlaylist(model, newName, this);
    }

    @Override
    public void onSuccessfullyRenaming() {
        if (iPlaylistView != null) {
            iPlaylistView.onSuccessfullyRenaming();
        }
    }
}
