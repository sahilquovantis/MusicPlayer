package com.quovantis.musicplayer.updated.ui.views.fullscreenmusiccontrols;

import com.quovantis.musicplayer.updated.constants.AppMusicKeys;
import com.quovantis.musicplayer.updated.helper.MusicHelper;

/**
 * CurrentPlaylist Presenter Implementation
 */
class CurrentPlaylistPresenterImp implements ICurrentPlaylistPresenter {

    private ICurrentPlaylistView mView;

    CurrentPlaylistPresenterImp(ICurrentPlaylistView mView) {
        this.mView = mView;
    }

    @Override
    public void onDestroy() {
        mView = null;
    }

    @Override
    public void songsMoved(int fromPosition, int toPosition) {

    }

    @Override
    public void songRemoved(int position) {
        int size = MusicHelper.getInstance().getCurrentPlaylist().size();
        int currPos = MusicHelper.getInstance().getCurrentPosition();
        if (position == currPos) {
            mView.onCurrentPlayingSongRemoved();
        } else if (position < currPos) {
            MusicHelper.getInstance().setCurrentPosition(currPos - 1);
        }
        if (size == 0) {
            mView.onSuccessfullyRemovedSong();
            mView.onEmptyList();
            return;
        }
        mView.onSuccessfullyRemovedSong();
    }
}
