package com.quovantis.musicplayer.updated.ui.views.current_playlist;

import android.graphics.Bitmap;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.interfaces.ICommonKeys;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.ui.views.music.IMusicPresenter;
import com.quovantis.musicplayer.updated.ui.views.music.IMusicView;
import com.quovantis.musicplayer.updated.ui.views.music.MusicPresenterImp;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CurrentPlaylistActivity extends AppCompatActivity implements ICurrentPlaylistView,
        IMusicView {

    /**
     * Music Layout BindViews
     */
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

    /**
     * Folders Activity Views
     */
    @BindView(R.id.tv_empty_list)
    TextView mEmptyTV;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rv_current_playlist)
    RecyclerView mCurrentPlaylistRV;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<SongDetailsModel> mQueueList;
    private IMusicPresenter iMusicPresenter;
    private ICurrentPlaylistPresenter iCurrentPlaylistPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_playlist);
        ButterKnife.bind(this);
        mQueueList = new ArrayList<>();
        initToolbar();
        initRecyclerView();
        initPresenters();
    }

    private void initPresenters() {
        iCurrentPlaylistPresenter = new CurrentPlaylistPresenterImp(this);
        iMusicPresenter = new MusicPresenterImp(this, this);
        iMusicPresenter.bindService();
        iCurrentPlaylistPresenter.updateUI();
    }

    private void initRecyclerView() {
        mCurrentPlaylistRV.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CurrentPlaylistAdapter(mQueueList, this);
        mCurrentPlaylistRV.setAdapter(mAdapter);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Current Playlist");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
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
    public void onShowProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHideProgress() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onUpdateUI(ArrayList<SongDetailsModel> currentPlaylistList) {
        mEmptyTV.setVisibility(View.GONE);
        mQueueList.clear();
        mQueueList.addAll(currentPlaylistList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onEmptyList() {
        mEmptyTV.setVisibility(View.VISIBLE);
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

    @Override
    public void onStopService() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iCurrentPlaylistPresenter.onDestroy();
        iMusicPresenter.onDestroy();
        iCurrentPlaylistPresenter = null;
        iMusicPresenter = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
