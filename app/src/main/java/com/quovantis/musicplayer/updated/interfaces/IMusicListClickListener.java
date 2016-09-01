package com.quovantis.musicplayer.updated.interfaces;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

/**
 * Created by sahil-goel on 25/8/16.
 */
public interface IMusicListClickListener {
    void onMusicListClick(int pos);
    void onActionOverFlowClick(SongDetailsModel model);
}
