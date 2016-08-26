package com.quovantis.musicplayer.updated.ui.views.music;

import android.graphics.Bitmap;

/**
 * Created by sahil-goel on 26/8/16.
 */
public interface IMusicView {
    void onUpdateSongUI(String title, String artist, Bitmap bitmap);
    void onUpdateSongState(int state);
    void onHideMusicLayout();
    void onShowMusicLayout();
    void onStopService();
}
