package com.quovantis.musicplayer.updated.ui.views.currentplaylist;

import com.quovantis.musicplayer.updated.helper.MusicHelper;
import com.quovantis.musicplayer.updated.interfaces.IOnSongRemovedFromQueue;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.ArrayList;

/**
 * Created by sahil-goel on 29/8/16.
 */
public class CurrentPlaylistPresenterImp implements ICurrentPlaylistPresenter,
        ICurrentPlaylistInteractor.Listener, IOnSongRemovedFromQueue {

    private ICurrentPlaylistView mView;
    private ICurrentPlaylistInteractor iCurrentPlaylistInteractor;

    public CurrentPlaylistPresenterImp(ICurrentPlaylistView mView) {
        this.mView = mView;
        iCurrentPlaylistInteractor = new CurrentPlaylistInteractorImp();
    }

    @Override
    public void updateUI() {
        if (mView != null) {
            mView.onShowProgress();
            iCurrentPlaylistInteractor.getCurrentPlaylist(this);
        }
    }

    @Override
    public void onDestroy() {
        mView = null;
    }

    @Override
    public void onUpdateUI(ArrayList<SongDetailsModel> list) {
        if (mView != null) {
            if (list != null && !list.isEmpty())
                mView.onUpdateUI(list);
            else
                mView.onEmptyList();
            mView.onHideProgress();
        }
    }

    @Override
    public void songsMoved(int fromPosition, int toPosition) {
        MusicHelper.getInstance().songsMoved(fromPosition, toPosition);
    }

    @Override
    public void songRemoved(int position) {
        MusicHelper.getInstance().songRemove(position, this);
    }

    @Override
    public void onQueueListEmptyShowEmptyTV() {
        if (mView != null)
            mView.onEmptyList();
    }

    @Override
    public void createPlaylist(String name) {
        iCurrentPlaylistInteractor.createPlaylist(name, this);
    }

    @Override
    public void onPlaylistCreated(boolean isCreated) {
        if (mView != null)
            mView.onCancelCreatePlaylistProgressDialog(isCreated);
    }
}
