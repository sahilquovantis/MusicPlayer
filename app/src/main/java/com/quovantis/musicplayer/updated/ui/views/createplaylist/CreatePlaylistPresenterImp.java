package com.quovantis.musicplayer.updated.ui.views.createplaylist;

import android.app.Activity;
import android.content.Context;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.models.UserPlaylistModel;

import java.util.List;

/**
 * Created by sahil-goel on 7/9/16.
 */
public class CreatePlaylistPresenterImp implements ICreatePlaylistPresenter,
        ICreatePlaylistInteractor.Listener {

    private ICreatePlaylistView iCreatePlaylistView;
    private ICreatePlaylistInteractor iCreatePlaylistInteractor;

    public CreatePlaylistPresenterImp(ICreatePlaylistView iCreatePlaylistView, Context context,
                                      Activity activity) {
        this.iCreatePlaylistView = iCreatePlaylistView;
        iCreatePlaylistInteractor = new CreatePlaylistInteractorImp(context,activity);
    }

    @Override
    public void updateUI() {
        if (iCreatePlaylistView != null)
            iCreatePlaylistView.showProgress();
        iCreatePlaylistInteractor.getPlaylists(this);
    }

    @Override
    public void createPlaylist(String name, String id, String action) {
        iCreatePlaylistInteractor.createNewPlaylist(name, id, action, this);
    }

    @Override
    public void onDestory() {
        iCreatePlaylistView = null;
    }

    @Override
    public void onGettingPlaylists(List<UserPlaylistModel> list) {
        if (iCreatePlaylistView != null) {
            iCreatePlaylistView.updateUI(list);
            iCreatePlaylistView.hidePRogress();
        }
    }

    @Override
    public void onPlaylistCreated(boolean isCreated) {
        if(iCreatePlaylistView != null)
            iCreatePlaylistView.onCancelCreatePlaylistProgressDialog(isCreated);
    }

    @Override
    public void addToExistingPlaylist(UserPlaylistModel model, String id, String action) {
        iCreatePlaylistInteractor.addToExistingPlaylist(model, id, action, this);
    }
}
