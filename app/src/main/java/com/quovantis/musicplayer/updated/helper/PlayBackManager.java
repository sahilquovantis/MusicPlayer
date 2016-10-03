package com.quovantis.musicplayer.updated.helper;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.session.PlaybackState;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.Toast;

import com.quovantis.musicplayer.updated.interfaces.ICommonKeys;

import java.io.IOException;

/**
 *  This class handle the media player controls like playing and pausing of music.
 */
public class PlayBackManager implements AudioManager.OnAudioFocusChangeListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private Context mContext;
    private Handler mHandler;
    private boolean mPlayOnFocusGain;
    private ICallback mCallback;
    private ISongProgressCallback iProgressCallback;
    private MediaMetadataCompat mCurrentMedia;
    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
    private int mCurrentState;
    private boolean isPauseWithMetaDataCalled = false;

    public PlayBackManager(Context context, ICallback callback, ISongProgressCallback iCallback) {
        mContext = context;
        iProgressCallback = iCallback;
        mHandler = new Handler();
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mCallback = callback;
    }

    public MediaMetadataCompat getMediaMetaData() {
        return mCurrentMedia;
    }

    public void setMediaData(MediaMetadataCompat mediaData) {
        mCurrentMedia = mediaData;
        if (mMediaPlayer != null) {
            mMediaPlayer.setOnCompletionListener(null);
            mMediaPlayer.reset();
        }
        mCurrentState = PlaybackStateCompat.STATE_NONE;
        updatePlaybackState(PlaybackStateCompat.STATE_NONE);
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mAudioManager != null) {
            mAudioManager.abandonAudioFocus(this);
            mAudioManager = null;
        }
        mCurrentMedia = null;
        updatePlaybackState(PlaybackStateCompat.STATE_STOPPED);
    }

    public void none() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mCurrentMedia = null;
        updatePlaybackState(PlaybackStateCompat.STATE_NONE);
    }

    public void pause() {
        if (isPlaying() && mCurrentState == PlaybackStateCompat.STATE_PLAYING) {
            mMediaPlayer.pause();
            updatePlaybackState(PlaybackStateCompat.STATE_PAUSED);
        }
    }

    public void play() {
        if (mMediaPlayer != null) {
            if (mCurrentState == PlaybackStateCompat.STATE_PAUSED) {
                if (isPauseWithMetaDataCalled) {
                    playFromMetaData(mCurrentMedia);
                    isPauseWithMetaDataCalled = false;
                    return;
                }
                if (tryToGetAudioFocus()) {
                    mMediaPlayer.start();
                    updatePlaybackState(PlaybackStateCompat.STATE_PLAYING);
                } else {
                    mPlayOnFocusGain = true;
                }
                isPauseWithMetaDataCalled = false;
            }
        }
    }

    /**
     * Create and Reset Media Player if Needed
     */
    private void createMediaPlayerIfNeeded() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setWakeMode(mContext, PowerManager.PARTIAL_WAKE_LOCK);
            Log.d(ICommonKeys.TAG, "New Media Player Created");
        } else {
            mMediaPlayer.setOnCompletionListener(null);
            mMediaPlayer.reset();
            Log.d(ICommonKeys.TAG, "Media Player reset");
        }
    }

    public void playFromMetaData(MediaMetadataCompat metadata) {
        String mediaId = metadata.getDescription().getMediaId();
        createMediaPlayerIfNeeded();
        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(mContext, MusicHelper.getInstance().getSongURI(mediaId));
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mCurrentMedia = metadata;
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            mCurrentMedia = null;
            Toast.makeText(mContext, "File not found", Toast.LENGTH_LONG).show();
            updatePlaybackState(PlaybackStateCompat.STATE_NONE);
        }
    }

    public void pauseWithMetaData(MediaMetadataCompat metadata) {
        String mediaId = metadata.getDescription().getMediaId();
        createMediaPlayerIfNeeded();
        try {
            mMediaPlayer.setDataSource(mContext, MusicHelper.getInstance().getSongURI(mediaId));
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mCurrentMedia = metadata;
            mMediaPlayer.pause();
            updatePlaybackState(PlaybackStateCompat.STATE_PAUSED);
            isPauseWithMetaDataCalled = true;
            mHandler.postDelayed(mProgressTimer, 100);
        } catch (IOException e) {
            mCurrentMedia = null;
            Toast.makeText(mContext, "File not found", Toast.LENGTH_LONG).show();
            updatePlaybackState(PlaybackStateCompat.STATE_NONE);
        }
    }

    /**
     * Try to get the system audio focus.
     */
    private boolean tryToGetAudioFocus() {
        if (mAudioManager == null)
            mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        int result = mAudioManager.requestAudioFocus(
                this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    private void updatePlaybackState(int state) {
        if (mCallback == null) {
            mCurrentState = -1;
            return;
        }
        mCurrentState = state;
        PlaybackStateCompat.Builder playbackState = new PlaybackStateCompat.Builder()
                .setState(state, getCurrentStreamPosition(), 1.0f, SystemClock.elapsedRealtime());
        mCallback.onPlaybackStateChanged(playbackState.build());
    }

    private int getCurrentStreamPosition() {
        return mMediaPlayer != null ? mMediaPlayer.getCurrentPosition() : 0;
    }

    /**
     * Used to Check whether Song is Playing or not
     *
     * @return True or False on the basis of Song is Playing or not
     */
    private boolean isPlaying() {
        return (mMediaPlayer != null && mMediaPlayer.isPlaying());
    }

    @Override
    public void onAudioFocusChange(int focus) {
        boolean gotFullFocus = false;
        boolean canDuck = false;
        if (focus == AudioManager.AUDIOFOCUS_GAIN) {
            gotFullFocus = true;
        } else if (focus == AudioManager.AUDIOFOCUS_LOSS || focus == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
            if (mCurrentState == PlaybackState.STATE_PLAYING && isPlaying()) {
                mMediaPlayer.pause();
                updatePlaybackState(PlaybackStateCompat.STATE_PAUSED);
            }
        } else if (focus == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
            canDuck = true;
        }

        if (gotFullFocus || canDuck) {
            if (mMediaPlayer != null) {
                float volume = canDuck ? 0.2f : 1.0f;
                mMediaPlayer.setVolume(volume, volume);
                if (mPlayOnFocusGain) {
                    startPlayer();
                }
            }
        }
    }

    private void startPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
            mMediaPlayer.setOnCompletionListener(this);
            mHandler.postDelayed(mProgressTimer, 100);
            updatePlaybackState(PlaybackStateCompat.STATE_PLAYING);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (mCallback != null) {
            mCallback.onSongCompletion();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d("Training", "OnPrepared");
        if (mediaPlayer != null) {
            if (tryToGetAudioFocus()) {
                startPlayer();
            } else {
                mPlayOnFocusGain = true;
            }
        }
    }

    public void seekTo(long pos) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo((int) pos);
            updatePlaybackState(PlaybackStateCompat.STATE_PLAYING);
        }
    }

    public interface ICallback {
        void onPlaybackStateChanged(PlaybackStateCompat state);

        void onSongCompletion();
    }

    public interface ISongProgressCallback {
        void onProgress(long currentProgress, long totalProgress);
    }

    private Runnable mProgressTimer = new Runnable() {
        @Override
        public void run() {
            if (mMediaPlayer != null) {
                int total = mMediaPlayer.getDuration();
                int current = mMediaPlayer.getCurrentPosition();
                iProgressCallback.onProgress(current, total);
                mHandler.postDelayed(mProgressTimer, 100);
            }
        }
    };
}
