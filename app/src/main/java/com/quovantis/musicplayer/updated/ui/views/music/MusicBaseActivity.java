package com.quovantis.musicplayer.updated.ui.views.music;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.helper.AnimationHelper;
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
    private Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
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
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                if (vibrantSwatch != null) {
                    int rgb = vibrantSwatch.getRgb();
                    mMusicLayout.setBackgroundColor(rgb);
                    changeToolbarColor(rgb);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        window.setStatusBarColor(rgb);
                    }
                }
            }
        });
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
        /*mMusicLayout.post(new Runnable() {
            @Override
            public void run() {
                AnimationHelper.hideView(mMusicLayout);
            }
        });*/
        mMusicLayout.setVisibility(View.GONE);
        changeToolbarColor(R.color.colorPrimary);
        mMusicLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    @Override
    public void onShowMusicLayout() {
      /*  mMusicLayout.post(new Runnable() {
            @Override
            public void run() {
                AnimationHelper.CircularReveal(mMusicLayout);
            }
        });*/
        mMusicLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStopService() {

    }

    @Override
    public void updateMusicProgress(PlaybackStateCompat playbackState) {

    }

    @Override
    public void updateMusicDurationInitial(MediaMetadataCompat mediaMetadata) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @OnClick(R.id.rl_music_layout)
    public void onClick() {
        Intent intent = new Intent(this, FullScreenMusic.class);
        startActivity(intent);
    }

    @Override
    public void cancelDialog() {

    }
}
