package com.quovantis.musicplayer.updated.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.KeyEvent;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.constants.AppMusicKeys;
import com.quovantis.musicplayer.updated.helper.LoggerHelper;
import com.quovantis.musicplayer.updated.helper.MusicHelper;
import com.quovantis.musicplayer.updated.helper.NotificationHelper;
import com.quovantis.musicplayer.updated.helper.PlayBackManager;
import com.quovantis.musicplayer.updated.utility.Utils;

/**
 * Music Service which runs in Background.
 */
public class MusicService extends Service implements PlayBackManager.ICallback,
        PlayBackManager.ISongProgressCallback {
    private static final int NOTIFICATION_ID = 1;
    private Binder mBinder;
    private MediaSessionCompat mMediaSession;
    private MediaControllerCompat mMediaController;
    private NotificationHelper mNotificationHelper;
    private PlayBackManager mPlaybackManager;
    private Notification mNotification;
    public static boolean mIsServiceDestroyed;
    private NotificationManager mNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mIsServiceDestroyed = false;
        mBinder = new ServiceBinder();
        registerReceiver(mNoisyReceiver, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
        LoggerHelper.debug("Music Service Created");
        MusicHelper.getInstance();
        mMediaSession = new MediaSessionCompat(this, "Music");
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setCallback(mMediaCallback);
        mMediaSession.setActive(true);
        mMediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
                .setState(PlaybackStateCompat.STATE_NONE, 0, 1, SystemClock.elapsedRealtime())
                .build());
        try {
            mMediaController = new MediaControllerCompat(this, getMediaSessionToken());
        } catch (RemoteException e) {
            mMediaController = null;
        }
        mPlaybackManager = new PlayBackManager(getApplicationContext(), this, this);
        mNotificationHelper = new NotificationHelper(getApplicationContext());
        initCurrentPlaylist();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LoggerHelper.debug("Music Service OnStartCommand");
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            switch (action) {
                case AppMusicKeys.INTENT_ACTION_PAUSE:
                    mMediaController.getTransportControls().pause();
                    break;
                case AppMusicKeys.INTENT_ACTION_STOP:
                    mMediaController.getTransportControls().stop();
                    break;
                case AppMusicKeys.INTENT_ACTION_NEXT:
                    mMediaController.getTransportControls().skipToNext();
                    break;
                case AppMusicKeys.INTENT_ACTION_PREVIOUS:
                    mMediaController.getTransportControls().skipToPrevious();
                    break;
                case AppMusicKeys.INTENT_ACTION_PLAY:
                    mMediaController.getTransportControls().play();
                    break;
            }
        }
        return START_STICKY;
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
        public boolean onMediaButtonEvent(Intent intent) {
            if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
                KeyEvent event = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                if (event != null && event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (event.getKeyCode()) {
                        case KeyEvent.KEYCODE_MEDIA_PLAY: {
                            onPlay();
                            break;
                        }
                        case KeyEvent.KEYCODE_MEDIA_PAUSE: {
                            onPause();
                            break;
                        }

                        case KeyEvent.KEYCODE_HEADSETHOOK: {
                            int state = mMediaController.getPlaybackState().getState();
                            if (state == PlaybackStateCompat.STATE_PAUSED) {
                                onPlay();
                            } else if (state == PlaybackStateCompat.STATE_PLAYING) {
                                onPause();
                            }
                            break;
                        }
                        case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                            onSkipToPrevious();
                            break;
                        case KeyEvent.KEYCODE_MEDIA_NEXT:
                            onSkipToNext();
                            break;
                    }
                }
            }
            return true;
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            super.onPlayFromMediaId(mediaId, extras);
            MediaMetadataCompat mediaMetadata = MusicHelper.getInstance().
                    getMetadata(getApplicationContext());
            if (mediaMetadata == null && mPlaybackManager != null) {
                mPlaybackManager.setMediaData(null);
            } else {
                if (mMediaSession != null && mPlaybackManager != null) {
                    mMediaSession.setActive(true);
                    mMediaSession.setMetadata(mediaMetadata);
                    mPlaybackManager.playFromMetaData(mediaMetadata);
                }
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            if (mPlaybackManager != null)
                mPlaybackManager.pause();
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            String mediaId = MusicHelper.getInstance().getNextSong();
            if (mediaId == null && mPlaybackManager != null) {
                mPlaybackManager.setMediaData(null);
            } else {
                onPlayFromMediaId(mediaId, null);
            }
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            String mediaId = MusicHelper.getInstance().getPreviousSong();
            if (mediaId == null && mPlaybackManager != null) {
                mPlaybackManager.setMediaData(null);
            } else {
                onPlayFromMediaId(mediaId, null);
            }
        }

        @Override
        public void onStop() {
            super.onStop();
            if (mPlaybackManager != null)
                mPlaybackManager.stop();
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
            if (mPlaybackManager != null)
                mPlaybackManager.seekTo(pos);
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {
            super.onCustomAction(action, extras);
            if (action.equals("NONE") && mPlaybackManager != null) {
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
        Utils.updateDefaultPlaylist(MusicService.this);
        if (mMediaSession != null) {
            mMediaSession.release();
            mMediaSession = null;
        }
        mMediaController = null;
        if (mNotificationManager != null) {
            mNotificationManager.cancelAll();
            mNotificationManager = null;
        }
        if (mNotification != null)
            stopForeground(true);
        if (mNoisyReceiver != null)
            unregisterReceiver(mNoisyReceiver);
        if (mPlaybackManager != null) {
            mPlaybackManager.releasePlayer();
            mPlaybackManager = null;
        }
        mNotificationHelper = null;
        mNotification = null;
        mIsServiceDestroyed = true;
        LoggerHelper.debug("Music Service Destroyed");
    }

    private BroadcastReceiver mNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null
                    && intent.getAction().equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
                mMediaController.getTransportControls().pause();
            }
        }
    };

    private void initCurrentPlaylist() {
        Utils.initCurrentPlaylist(MusicService.this);
        MediaMetadataCompat metadata = MusicHelper.getInstance().getMetadata(this);
        if (metadata != null) {
            mMediaSession.setActive(true);
            mMediaSession.setMetadata(metadata);
            mPlaybackManager.pauseWithMetaData(metadata);
        }
    }

    @Override
    public void onPlaybackStateChanged(PlaybackStateCompat state) {
        if (mMediaSession != null)
            mMediaSession.setPlaybackState(state);
        if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
            mNotification = mNotificationHelper.createNotification(mPlaybackManager.getMediaMetaData(),
                    getMediaSessionToken(), R.drawable.ic_action_pause, "Pause", AppMusicKeys.INTENT_ACTION_PAUSE);
            if (mNotification != null)
                startForeground(NOTIFICATION_ID, mNotification);
        } else if (state.getState() == PlaybackStateCompat.STATE_PAUSED) {
            stopForeground(true);
            mNotification = mNotificationHelper.createNotification(mPlaybackManager.getMediaMetaData(),
                    getMediaSessionToken(), R.drawable.ic_action_play, "Play", AppMusicKeys.INTENT_ACTION_PLAY);
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, mNotification);
        } else {
            stopForeground(true);
            if (mNotificationManager != null) {
                mNotificationManager.cancelAll();
                mNotificationManager = null;
            }
            mNotification = null;
            if (state.getState() == PlaybackStateCompat.STATE_STOPPED) {
                stopSelf();
            }
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

    @Override
    public void onProgress(long currentProgress, long totalProgress) {
        Intent intent = new Intent();
        intent.putExtra(AppMusicKeys.CURRENT_PROGRESS, currentProgress);
        intent.putExtra(AppMusicKeys.TOTAL_PROGRESS, totalProgress);
        intent.setAction(AppMusicKeys.UPDATE_PROGRESS);
        sendBroadcast(intent);
    }

    public class ServiceBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}