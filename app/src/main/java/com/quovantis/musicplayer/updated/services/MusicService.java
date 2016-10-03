package com.quovantis.musicplayer.updated.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.KeyEvent;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.constants.AppMusicKeys;
import com.quovantis.musicplayer.updated.constants.AppPreferenceKeys;
import com.quovantis.musicplayer.updated.helper.LoggerHelper;
import com.quovantis.musicplayer.updated.helper.MusicHelper;
import com.quovantis.musicplayer.updated.helper.NotificationHelper;
import com.quovantis.musicplayer.updated.helper.PlayBackManager;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.models.UserPlaylistModel;
import com.quovantis.musicplayer.updated.utility.SharedPreference;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Music Service which runs in Background.
 */
public class MusicService extends Service implements PlayBackManager.ICallback,
        PlayBackManager.ISongProgressCallback {
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
        LoggerHelper.debug("Music Service Created");
        MusicHelper.getInstance();
        mMediaSession = new MediaSessionCompat(this, "Music");
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setCallback(mMediaCallback);
        mMediaSession.setActive(true);
        mMediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_PAUSE |
                                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID | PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
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
        return super.onStartCommand(intent, flags, startId);
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
        updateDefaultPlaylist();
        if (mMediaSession != null) {
            mMediaSession.release();
            mMediaSession = null;
        }
        mMediaController = null;
        if (mNotification != null)
            stopForeground(true);
        super.onDestroy();
        LoggerHelper.debug("Music Service Destroyed");
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void initCurrentPlaylist() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<UserPlaylistModel> realmResults = realm.where(UserPlaylistModel.class).
                        equalTo("mPlaylistId", 0).findAll();
                if (realmResults.size() > 0) {
                    ArrayList<SongDetailsModel> list = new ArrayList<>(realmResults.get(0).getPlaylist());
                    int pos = SharedPreference.getInstance().getInt(MusicService.this,
                            AppPreferenceKeys.CURRENT_POSITION);
                    MusicHelper.getInstance().setCurrentPlaylist(list, pos);
                }
            }
        });
        MediaMetadataCompat metadata = MusicHelper.getInstance().getMetadata(this);
        if (metadata != null) {
            mMediaSession.setActive(true);
            mMediaSession.setMetadata(metadata);
            mPlaybackManager.pauseWithMetaData(metadata);
        }
    }

    private void updateDefaultPlaylist() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                List<SongDetailsModel> list = MusicHelper.getInstance().getCurrentPlaylist();
                UserPlaylistModel userPlaylistModel = new UserPlaylistModel();
                userPlaylistModel.setPlaylistName("Default");
                userPlaylistModel.setPlaylistId(0);
                userPlaylistModel.getPlaylist().addAll(list);
                realm.copyToRealmOrUpdate(userPlaylistModel);
                int pos = 0;
                if (list != null && !list.isEmpty()) {
                    pos = MusicHelper.getInstance().getCurrentPosition();
                }
                SharedPreference.getInstance().putInt(MusicService.this,
                        AppPreferenceKeys.CURRENT_POSITION, pos);
            }
        });
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
                    AppMusicKeys.INTENT_ACTION_PAUSE);
            if (mNotification != null)
                startForeground(NOTIFICATION_ID, mNotification);
        } else if (state.getState() == PlaybackStateCompat.STATE_PAUSED) {
            mNotification = mNotificationHelper.createNotification(
                    mPlaybackManager.getMediaMetaData(),
                    getMediaSessionToken(),
                    R.drawable.ic_action_play, "Play",
                    AppMusicKeys.INTENT_ACTION_PLAY);
            if (mNotification != null)
                startForeground(NOTIFICATION_ID, mNotification);
        } else {
            stopForeground(true);
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
