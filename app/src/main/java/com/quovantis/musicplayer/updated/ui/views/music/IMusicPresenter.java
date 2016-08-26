package com.quovantis.musicplayer.updated.ui.views.music;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;

/**
 * Created by sahil-goel on 26/8/16.
 */
public interface IMusicPresenter {
    void updateUI(MediaMetadataCompat mediaMetadata);
    void updateState(PlaybackStateCompat playbackState);
    void onDestroy();
    void bindService();
}
