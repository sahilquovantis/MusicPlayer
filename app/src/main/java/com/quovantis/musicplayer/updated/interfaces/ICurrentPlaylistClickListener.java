package com.quovantis.musicplayer.updated.interfaces;

/**
 * Created by sahil-goel on 29/8/16.
 */
public interface ICurrentPlaylistClickListener {
    void onClick(int pos);

    void onSongRemove(int pos);

    void onSongsMoved(int from, int to);
}
