package com.quovantis.musicplayer.updated.interfaces;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

/**
 * Created by sahil-goel on 29/8/16.
 */
public interface ICurrentPlaylistClickListener {
    void onClick(SongDetailsModel model);
    void onSongRemove(int pos);
    void onSongsMoved(int from, int to);
}
