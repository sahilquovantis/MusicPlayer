package com.quovantis.musicplayer.updated.ui.views.music;

import android.graphics.Bitmap;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quovantis.musicplayer.R;

import butterknife.BindView;
import butterknife.OnClick;

public abstract class MusicBaseActivity extends AppCompatActivity implements IMusicView{

    @BindView(R.id.rl_music_layout)
    RelativeLayout mMusicLayout;
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

    @OnClick({R.id.iv_next_song, R.id.iv_previous_song, R.id.iv_play_pause_button})
    public void onMusicButtonsClick(ImageView view) {
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
    }

    @Override
    public void onUpdateSongState(int state) {
        if (state == PlaybackStateCompat.STATE_PLAYING) {
            mPlayPauseIV.setImageResource(R.drawable.ic_action_pause);
        } else {
            mPlayPauseIV.setImageResource(R.drawable.ic_action_play);
        }
    }

    @Override
    public void onHideMusicLayout() {
        mMusicLayout.setVisibility(View.GONE);
    }

    @Override
    public void onShowMusicLayout() {
        mMusicLayout.setVisibility(View.VISIBLE);
    }

}
