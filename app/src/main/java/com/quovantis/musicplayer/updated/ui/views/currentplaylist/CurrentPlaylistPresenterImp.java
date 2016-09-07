package com.quovantis.musicplayer.updated.ui.views.currentplaylist;

import com.quovantis.musicplayer.updated.helper.MusicHelper;
import com.quovantis.musicplayer.updated.interfaces.IOnSongRemovedFromQueue;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.ArrayList;

/**
 * Created by sahil-goel on 29/8/16.
 */
public class CurrentPlaylistPresenterImp implements ICurrentPlaylistPresenter, IOnSongRemovedFromQueue {

    private ICurrentPlaylistView mView;

    public CurrentPlaylistPresenterImp(ICurrentPlaylistView mView) {
        this.mView = mView;
    }

    @Override
    public void onDestroy() {
        mView = null;
    }

    @Override
    public void songsMoved(int fromPosition, int toPosition) {
        MusicHelper.getInstance().songsMoved(fromPosition, toPosition);
    }

    @Override
    public void onSongRemovedSuccessfully(int curPos) {
        if (mView != null)
            mView.onSuccessfullyRemovedSong(curPos);
    }

    @Override
    public void songRemoved(int position) {
        MusicHelper.getInstance().songRemove(position, this);
    }

    @Override
    public void onCurrentPlayingSongRemoved() {
        if (mView != null)
            mView.onCurrentPlayingSongRemoved();
    }

    @Override
    public void onQueueListEmptyShowEmptyTV() {
        if (mView != null)
            mView.onEmptyList();
    }
}
