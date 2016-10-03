package com.quovantis.musicplayer.updated.services;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.helper.MusicHelper;
import com.quovantis.musicplayer.updated.helper.NotificationHelper;
import com.quovantis.musicplayer.updated.helper.PlayBackManager;
import com.quovantis.musicplayer.updated.interfaces.ICommonKeys;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.models.UserPlaylistModel;
import com.quovantis.musicplayer.updated.utility.SharedPrefrence;
import com.quovantis.musicplayer.updated.utility.Utils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by sahil-goel on 25/8/16.
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
        Log.d(ICommonKeys.TAG, "Service Created");
        MusicHelper.getInstance();
        mMediaSession = new MediaSessionCompat(this, ICommonKeys.TAG);
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
            Log.d(ICommonKeys.TAG, "Media Controller Exception : " + e.getMessage());
            mMediaController = null;
        }
        mPlaybackManager = new PlayBackManager(getApplicationContext(), this, this);
        mNotificationHelper = new NotificationHelper(getApplicationContext());
        initCurrentPlaylist();
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
        Log.d(ICommonKeys.TAG, "Service Destroying");
        if (mMediaSession != null) {
            mMediaSession.release();
            mMediaSession = null;
        }
        mMediaController = null;
        if (mNotification != null)
            stopForeground(true);
        super.onDestroy();
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
                    int pos = SharedPrefrence.getCurrentPosition(MusicService.this, ICommonKeys.CURRENT_POSITION);
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
                SharedPrefrence.saveCurrentPosition(MusicService.this, ICommonKeys.CURRENT_POSITION, pos);
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
        intent.putExtra(Utils.CURRENT_PROGRESS, currentProgress);
        intent.putExtra(Utils.TOTAL_PROGRESS, totalProgress);
        intent.setAction(Utils.UPDATE_PROGRESS);
        sendBroadcast(intent);
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
