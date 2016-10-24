package com.quovantis.musicplayer.updated.ui.views.music;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.quovantis.musicplayer.updated.helper.LoggerHelper;
import com.quovantis.musicplayer.updated.helper.MusicHelper;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.services.MusicService;

/**
 * Created by sahil-goel on 26/8/16.
 */
public class MusicPresenterImp implements IMusicPresenter, ServiceConnection {

    private MediaControllerCompat mMediaController;
    private Context mContext;
    private IMusicView iMusicView;
    private boolean mIsBounded;

    public MusicPresenterImp(IMusicView iMusicView, Context mContext) {
        this.iMusicView = iMusicView;
        this.mContext = mContext;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (iBinder instanceof MusicService.ServiceBinder) {
            LoggerHelper.debug("Music Service Binded");
            MediaMetadataCompat mCurrentMetadata = null;
            PlaybackStateCompat mCurrentState = null;
            try {
                mMediaController = new MediaControllerCompat(mContext, ((MusicService.ServiceBinder) iBinder).getService().getMediaSessionToken());
                mMediaController.registerCallback(mMediaCallback);
                mCurrentMetadata = mMediaController.getMetadata();
                mCurrentState = mMediaController.getPlaybackState();
            } catch (RemoteException e) {
                mCurrentMetadata = null;
                mCurrentState = null;
            } finally {
                updateUI(mCurrentMetadata);
                updateState(mCurrentState);
            }
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
    }

    @Override
    public void updateUI(MediaMetadataCompat mediaMetadata) {
        if (iMusicView != null) {
            boolean canHide = true;
            MediaDescriptionCompat mediaDescription = mediaMetadata == null ? null :
                    mediaMetadata.getDescription();
            if (mediaDescription != null) {
                String title = mediaDescription.getTitle() == null ? null : mediaDescription.getTitle().toString();
                String artist = mediaDescription.getSubtitle() == null ? null : mediaDescription.getSubtitle().toString();
                if (title != null && artist != null) {
                    Bitmap bitmap = mediaDescription.getIconBitmap();
                    iMusicView.onUpdateSongUI(title, artist, bitmap);
                    iMusicView.onShowMusicLayout();
                    canHide = false;
                }
            }
            if (canHide)
                iMusicView.onHideMusicLayout();
        }
    }

    @Override
    public void updateState(PlaybackStateCompat playbackState) {
        if (iMusicView != null) {
            if (playbackState != null) {
                int state = playbackState.getState();
                iMusicView.onUpdateSongState(state);
                if (state == PlaybackStateCompat.STATE_PLAYING ||
                        state == PlaybackStateCompat.STATE_PAUSED) {
                    iMusicView.onShowMusicLayout();
                } else {
                    iMusicView.onHideMusicLayout();
                }
            } else {
                iMusicView.onHideMusicLayout();
            }
        }
    }

    @Override
    public MediaControllerCompat getMediaControllerCompat() {
        return mMediaController;
    }

    private MediaControllerCompat.Callback mMediaCallback = new MediaControllerCompat.Callback() {

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            updateState(state);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);
            updateUI(metadata);
        }
    };

    @Override
    public void onDestroy() {
        iMusicView = null;
    }

    @Override
    public void bindService() {
        Intent intent = new Intent(mContext, MusicService.class);
        mContext.startService(intent);
        mContext.bindService(intent, this, Context.BIND_AUTO_CREATE);
        mIsBounded = true;
    }

    @Override
    public void unBindService() {
        if (mContext != null && mIsBounded) {
            mContext.unbindService(this);
            LoggerHelper.debug("Music Service Unbounded");
        }
        mIsBounded = false;
    }

    @Override
    public void onPlayPause() {
        if (mMediaController != null) {
            int state = mMediaController.getPlaybackState().getState();
            if (state == PlaybackStateCompat.STATE_PLAYING) {
                mMediaController.getTransportControls().pause();
            } else if (state == PlaybackStateCompat.STATE_PAUSED) {
                mMediaController.getTransportControls().play();
            }
        }
    }

    @Override
    public void onSkipToPrevious() {
        if (mMediaController != null)
            mMediaController.getTransportControls().skipToPrevious();
    }

    @Override
    public void onSkipToNext() {
        if (mMediaController != null)
            mMediaController.getTransportControls().skipToNext();
    }

    public void stopService() {
        if (mMediaController == null || mMediaController.getPlaybackState() == null || mMediaController.getPlaybackState().getState() == PlaybackStateCompat.STATE_NONE ||
                mMediaController.getPlaybackState().getState() == PlaybackStateCompat.STATE_STOPPED) {
            iMusicView.onStopService();
        }
    }

    @Override
    public void addSongToPlaylist(SongDetailsModel model, boolean isClearQueue, boolean isPlaythisSong) {
        MusicHelper.getInstance().addSongToPlaylist(model, isClearQueue, isPlaythisSong);
        if (isPlaythisSong)
            playSong();
    }

    @Override
    public void playSong() {
        if (mMediaController != null) {
            mMediaController.getTransportControls().playFromMediaId("1", null);
        }
    }


    @Override
    public void seekTo(long pos) {
        if (mMediaController != null)
            mMediaController.getTransportControls().seekTo(pos);
    }
}