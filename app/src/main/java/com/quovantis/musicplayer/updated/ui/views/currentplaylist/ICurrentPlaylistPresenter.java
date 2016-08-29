package com.quovantis.musicplayer.updated.ui.views.currentplaylist;

/**
 * Created by sahil-goel on 29/8/16.
 */
public interface ICurrentPlaylistPresenter {
    void updateUI();

    void onDestroy();

    void songsMoved(int fromPosition, int toPosition);

    void songRemoved(int position);

    void createPlaylist(String name);

}
