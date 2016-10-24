package com.quovantis.musicplayer.updated.ui.views.music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.constants.AppKeys;
import com.quovantis.musicplayer.updated.controller.AppActionController;
import com.quovantis.musicplayer.updated.helper.LoggerHelper;
import com.quovantis.musicplayer.updated.services.MusicService;
import com.quovantis.musicplayer.updated.ui.views.fullscreenmusiccontrols.FullScreenMusic;

import butterknife.BindView;
import butterknife.OnClick;

public abstract class MusicBaseActivity extends AppCompatActivity implements IMusicView {

    @BindView(R.id.rl_music_layout)
    public RelativeLayout mMusicLayout;
    @BindView(R.id.iv_selected_song_thumbnail)
    ImageView mSelectedSongThumbnailIV;
    @BindView(R.id.tv_selected_song)
    public TextView mSelectedSongTV;
    @BindView(R.id.tv_selected_song_artist)
    public TextView mSelectedSongArtistTV;
    @BindView(R.id.iv_play_pause_button)
    public ImageView mPlayPauseIV;
    public IMusicPresenter iMusicPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!MusicService.mIsServiceDestroyed) {
            if (iMusicPresenter != null)
                iMusicPresenter.bindService();
        } else {
            onBackPressed();
        }
        registerReceiver(mCloseMusicReceiver, new IntentFilter(AppKeys.CLOSE_MUSIC_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (iMusicPresenter != null)
            iMusicPresenter.unBindService();
        unregisterReceiver(mCloseMusicReceiver);
    }

    @OnClick({R.id.iv_next_song, R.id.iv_previous_song, R.id.iv_play_pause_button})
    protected void onMusicButtonsClick(ImageView view) {
        if (view.getId() == R.id.iv_play_pause_button) {
            iMusicPresenter.onPlayPause();
        } else if (view.getId() == R.id.iv_previous_song) {
            iMusicPresenter.onSkipToPrevious();
        } else if (view.getId() == R.id.iv_next_song) {
            iMusicPresenter.onSkipToNext();
        }
    }

    @Override
    public void onUpdateSongUI(String title, String artist, Bitmap bitmap) {
        mSelectedSongTV.setText(title);
        mSelectedSongArtistTV.setText(artist);
        mSelectedSongThumbnailIV.setImageBitmap(bitmap);
        mSelectedSongTV.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        mSelectedSongTV.setSelected(true);
        mSelectedSongTV.setSingleLine(true);
        mSelectedSongArtistTV.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        mSelectedSongArtistTV.setSelected(true);
        mSelectedSongArtistTV.setSingleLine(true);
        updateCurrentSongPlayingStatus(1);
    }

    @Override
    public void onUpdateSongState(int state) {
        if (state == PlaybackStateCompat.STATE_PLAYING) {
            mPlayPauseIV.setImageResource(R.drawable.ic_action_pause);
        } else {
            mPlayPauseIV.setImageResource(R.drawable.ic_action_play);
        }
        updateCurrentSongPlayingStatus(0);
    }

    protected abstract void updateCurrentSongPlayingStatus(int val);

    @Override
    public void onHideMusicLayout() {
        mMusicLayout.setVisibility(View.GONE);
    }

    @Override
    public void onShowMusicLayout() {
        mMusicLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStopService() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @OnClick(R.id.rl_music_layout)
    public void onClick() {
        if (this instanceof FullScreenMusic) {
            return;
        }
        new AppActionController.Builder(this)
                .from(this)
                .setTargetActivity(FullScreenMusic.class)
                .build()
                .execute();
    }

    @Override
    public void cancelDialog() {

    }

    private BroadcastReceiver mCloseMusicReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equalsIgnoreCase(AppKeys.CLOSE_MUSIC_ACTION)) {
                if (iMusicPresenter != null)
                    iMusicPresenter.unBindService();
                context.stopService(new Intent(context, MusicService.class));
                onHideMusicLayout();
                onBackPressed();
                LoggerHelper.debug("Close Music Receiver Called");
            }
        }
    };
}