package com.quovantis.musicplayer.updated.interfaces;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

/**
 * Created by sahil-goel on 11/9/16.
 */
public interface IHomeAndMusicCommunicator {
    void onMusicListClick();
    void onOptionsDialogClick(SongDetailsModel model, boolean isClearQueue, boolean isPlaythisSong);
}
