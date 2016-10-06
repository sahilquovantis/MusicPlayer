package com.quovantis.musicplayer.updated.ui.views.music;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.List;

/**
 * Created by sahil-goel on 26/8/16.
 */
public interface IMusicPresenter {

    MediaControllerCompat getMediaControllerCompat();

    void addSongToPlaylist(SongDetailsModel model, boolean isClearQueue, boolean isPlaythisSong);

    void updateUI(MediaMetadataCompat mediaMetadata);

    void updateState(PlaybackStateCompat playbackState);

    void onDestroy();

    void bindService();

    void onPlayPause();

    void onSkipToPrevious();

    void onSkipToNext();

    void stopService();

    void seekTo(long pos);

    void playSong();
}
