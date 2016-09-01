package com.quovantis.musicplayer.updated.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.MediaMetadata;
import android.media.session.PlaybackState;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.helper.MusicHelper;
import com.quovantis.musicplayer.updated.helper.NotificationHelper;
import com.quovantis.musicplayer.updated.helper.PlayBackManager;
import com.quovantis.musicplayer.updated.interfaces.ICommonKeys;
import com.quovantis.musicplayer.updated.utility.Utils;

/**
 * Created by sahil-goel on 25/8/16.
 */
public class MusicService extends Service implements PlayBackManager.ICallback {
    private static final int NOTIFICATION_ID = 1;
    private Binder mBinder = new ServiceBinder();
    private MediaSessionCompat mMediaSession;
    private MediaControllerCompat mMediaController;
    private NotificationHelper mNotificationHelper;
    private PlayBackManager mPlaybackManager;
    private Notification mNotification;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(ICommonKeys.TAG, "Service Created");
        mMediaSession = new MediaSessionCompat(this, ICommonKeys.TAG);
        mMediaSession.setCallback(mMediaCallback);
        mMediaSession.setActive(true);
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_NONE, 0, 1.0f)
                .build());
        try {
            mMediaController = new MediaControllerCompat(this, getMediaSessionToken());
        } catch (RemoteException e) {
            Log.d(ICommonKeys.TAG, "Media Controller Exception : " + e.getMessage());
            mMediaController = null;
        }
        mPlaybackManager = new PlayBackManager(getApplicationContext(), this);
        mNotificationHelper = new NotificationHelper(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(ICommonKeys.TAG, "Service Start Command");
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            if (action.equals(Utils.INTENT_ACTION_PAUSE)) {
                mMediaController.getTransportControls().pause();
            } else if (action.equals(Utils.INTENT_ACTION_STOP)) {
                mMediaController.getTransportControls().stop();
            } else if (action.equals(Utils.INTENT_ACTION_NEXT)) {
                mMediaController.getTransportControls().skipToNext();
            } else if (action.equals(Utils.INTENT_ACTION_PREVIOUS)) {
                mMediaController.getTransportControls().skipToPrevious();
            } else if (action.equals(Utils.INTENT_ACTION_PLAY)) {
                mMediaController.getTransportControls().play();
            }
        }
        return START_NOT_STICKY;
    }

    private MediaSessionCompat.Callback mMediaCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onPlay() {
            super.onPlay();
            if (mPlaybackManager != null) {
                mPlaybackManager.play();
            }
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            super.onPlayFromMediaId(mediaId, extras);
            MediaMetadataCompat mediaMetadata = MusicHelper.getInstance().
                    getMetadata(getApplicationContext());
            if (mediaMetadata == null) {
                mPlaybackManager.setMediaData(null);
            } else {
                mMediaSession.setActive(true);
                mMediaSession.setMetadata(mediaMetadata);
                mPlaybackManager.playFromMetaData(mediaMetadata);
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            mPlaybackManager.pause();
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            String mediaId = MusicHelper.getInstance().getNextSong();
            if (mediaId == null) {
                mPlaybackManager.setMediaData(null);
            } else {
                onPlayFromMediaId(mediaId, null);
            }
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            String mediaId = MusicHelper.getInstance().getPreviousSong();
            if (mediaId == null) {
                mPlaybackManager.setMediaData(null);
            } else {
                onPlayFromMediaId(mediaId, null);
            }
        }

        @Override
        public void onStop() {
            super.onStop();
            mPlaybackManager.stop();
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
            mPlaybackManager.seekTo(pos);
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {
            super.onCustomAction(action, extras);
            if (action.equals("NONE")) {
                mPlaybackManager.none();
            }
        }
    };

    /**
     * @return Returns the Media Session Token
     */
    public MediaSessionCompat.Token getMediaSessionToken() {
        return mMediaSession == null ? null : mMediaSession.getSessionToken();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(ICommonKeys.TAG, "Service Destroying");
        if (mMediaSession != null) {
            mMediaSession.release();
            mMediaSession = null;
        }
        mMediaController = null;
        if (mNotification != null)
            stopForeground(true);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onPlaybackStateChanged(PlaybackStateCompat state) {
        if (mMediaSession != null)
            mMediaSession.setPlaybackState(state);
        if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
            mNotification = mNotificationHelper.createNotification(
                    mPlaybackManager.getMediaMetaData(),
                    getMediaSessionToken(),
                    R.drawable.ic_action_pause, "Pause",
                    Utils.INTENT_ACTION_PAUSE);
            if (mNotification != null)
                startForeground(NOTIFICATION_ID, mNotification);
        } else if (state.getState() == PlaybackStateCompat.STATE_PAUSED) {
            mNotification = mNotificationHelper.createNotification(
                    mPlaybackManager.getMediaMetaData(),
                    getMediaSessionToken(),
                    R.drawable.ic_action_play, "Play",
                    Utils.INTENT_ACTION_PLAY);
            if (mNotification != null)
                startForeground(NOTIFICATION_ID, mNotification);
        } else {
            if (state.getState() == PlaybackStateCompat.STATE_STOPPED) {
                stopSelf();
            }
            stopForeground(true);
            mNotification = null;
            //mNotificationHelper.cancelNotification();
        }
    }

    @Override
    public void onSongCompletion() {
        String mediaId = MusicHelper.getInstance().getNextSong();
        if (mediaId == null) {
            mPlaybackManager.setMediaData(null);
        } else {
            mMediaController.getTransportControls().playFromMediaId(mediaId, null);
        }
    }

    public class ServiceBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(ICommonKeys.TAG, "OnBind Called");
        return mBinder;
    }
}
