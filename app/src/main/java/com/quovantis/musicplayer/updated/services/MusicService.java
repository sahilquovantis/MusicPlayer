package com.quovantis.musicplayer.updated.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.quovantis.musicplayer.updated.helper.NotificationHelper;
import com.quovantis.musicplayer.updated.interfaces.ICommonKeys;

/**
 * Created by sahil-goel on 25/8/16.
 */
public class MusicService extends Service {
    private Binder mBinder = new ServiceBinder();
    private MediaSessionCompat mMediaSession;
    private MediaControllerCompat mMediaController;
    private NotificationHelper mNotificationHelper;

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
        mNotificationHelper = new NotificationHelper(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(ICommonKeys.TAG, "Service Start Command");
        return START_NOT_STICKY;
    }

    private MediaSessionCompat.Callback mMediaCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onPlay() {
            super.onPlay();
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            super.onPlayFromMediaId(mediaId, extras);
        }

        @Override
        public void onPause() {
            super.onPause();
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
        }

        @Override
        public void onStop() {
            super.onStop();
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {
            super.onCustomAction(action, extras);
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
