package com.quovantis.musicplayer.updated.ui.views.music;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.MediaMetadata;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.quovantis.musicplayer.updated.helper.MusicHelper;
import com.quovantis.musicplayer.updated.interfaces.ICommonKeys;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.services.MusicService;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by sahil-goel on 26/8/16.
 */
public class MusicPresenterImp implements IMusicPresenter, ServiceConnection {

    private MediaControllerCompat mMediaController;
    private Context mContext;
    private IMusicView iMusicView;

    public MusicPresenterImp(IMusicView iMusicView, Context mContext) {
        this.iMusicView = iMusicView;
        this.mContext = mContext;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (iBinder instanceof MusicService.ServiceBinder) {
            MediaMetadataCompat mCurrentMetadata = null;
            PlaybackStateCompat mCurrentState = null;
            Log.d(ICommonKeys.TAG, "Service Binded");
            try {
                mMediaController = new MediaControllerCompat(mContext,
                        ((MusicService.ServiceBinder) iBinder).getService().getMediaSessionToken());

                mMediaController.registerCallback(mMediaCallback);
                Log.d("Training", "Activity : Media Callback registered");
                mCurrentMetadata = mMediaController.getMetadata();
                mCurrentState = mMediaController.getPlaybackState();
            } catch (RemoteException e) {
                mCurrentMetadata = null;
                mCurrentState = null;
                Log.d(ICommonKeys.TAG, "Media Controller Exception in Activities : " + e.getMessage());
            } finally {
                updateUI(mCurrentMetadata);
                updateState(mCurrentState);
            }
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        Log.d(ICommonKeys.TAG, "Service Unbinded");
    }

    @Override
    public void updateUI(MediaMetadataCompat mediaMetadata) {
        if (iMusicView != null) {
            iMusicView.updateMusicDurationInitial(mediaMetadata);
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
                if (state == PlaybackStateCompat.STATE_PLAYING ||
                        state == PlaybackStateCompat.STATE_PAUSED) {
                    iMusicView.onUpdateSongState(state);
                    iMusicView.onShowMusicLayout();
                } else {
                    iMusicView.onHideMusicLayout();
                }
            } else {
                iMusicView.onHideMusicLayout();
            }
            iMusicView.updateMusicProgress(playbackState);
        }
    }

    private MediaControllerCompat.Callback mMediaCallback = new MediaControllerCompat.Callback() {

        @Override
        public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
            Log.d(ICommonKeys.TAG, "Activity Playback State Changed : " + state.getState());
            updateState(state);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);
            Log.d(ICommonKeys.TAG, "Activity Metadata Changed");
            updateUI(metadata);
        }
    };

    @Override
    public void onDestroy() {
        mContext.unbindService(this);
        iMusicView = null;
    }

    @Override
    public void bindService() {
        Intent intent = new Intent(mContext, MusicService.class);
        mContext.startService(intent);
        mContext.bindService(intent, this, Context.BIND_AUTO_CREATE);
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
            playSong(model.getSongID());
    }

    @Override
    public void addSongToPlaylist(long id, boolean isClearQueue, boolean isPlaythisSong) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<SongDetailsModel> list = realm.where(SongDetailsModel.class).equalTo("mSongPathID", id).findAll().sort("mSongTitle", Sort.ASCENDING);
        if (!list.isEmpty()) {
            MusicHelper.getInstance().addSongToPlaylist(list, isClearQueue, isPlaythisSong);
            if (isPlaythisSong)
                playSong(list.get(0).getSongID());
            if(iMusicView != null)
                iMusicView.cancelDialog();
        }
    }

    private void playSong(String id) {
        if (mMediaController != null) {
            Log.d(ICommonKeys.TAG, "Selected Song id : " + id);
            mMediaController.getTransportControls().playFromMediaId(id, null);
        }
    }

    @Override
    public void hideMusicLayoutDuringResyncMusic() {
        if (mMediaController != null)
            mMediaController.getTransportControls().sendCustomAction("NONE", null);
        MusicHelper.getInstance().clearCurrentPlaylist();
    }

    @Override
    public void seekTo(long pos) {
        if (mMediaController != null)
            mMediaController.getTransportControls().seekTo(pos);
    }
}
