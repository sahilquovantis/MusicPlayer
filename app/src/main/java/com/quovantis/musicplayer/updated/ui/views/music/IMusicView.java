package com.quovantis.musicplayer.updated.ui.views.music;

import android.graphics.Bitmap;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;

/**
 * Created by sahil-goel on 26/8/16.
 */
public interface IMusicView {
    void onUpdateSongUI(String title, String artist, Bitmap bitmap);

    void onUpdateSongState(int state);

    void onHideMusicLayout();

    void onShowMusicLayout();

    void onStopService();

    void updateMusicProgress(PlaybackStateCompat playbackState);

    void updateMusicDurationInitial(MediaMetadataCompat mediaMetadata);

    void cancelDialog();

    void changeToolbarColor(int color);
}
