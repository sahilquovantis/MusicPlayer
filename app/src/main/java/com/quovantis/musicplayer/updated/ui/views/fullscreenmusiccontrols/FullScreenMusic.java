package com.quovantis.musicplayer.updated.ui.views.fullscreenmusiccontrols;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.constants.AppMusicKeys;
import com.quovantis.musicplayer.updated.dialogs.ProgresDialog;
import com.quovantis.musicplayer.updated.helper.MusicHelper;
import com.quovantis.musicplayer.updated.helper.QueueItemTouchHelper;
import com.quovantis.musicplayer.updated.interfaces.ICurrentPlaylistClickListener;
import com.quovantis.musicplayer.updated.ui.views.createplaylist.CreatePlaylistActivity;
import com.quovantis.musicplayer.updated.ui.views.music.MusicBaseActivity;
import com.quovantis.musicplayer.updated.ui.views.music.MusicPresenterImp;

import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FullScreenMusic extends MusicBaseActivity implements ICurrentPlaylistView,
        ICurrentPlaylistClickListener, SeekBar.OnSeekBarChangeListener {

    @BindView(R.id.iv_repeat_song)
    protected ImageView mRepeatSongIV;
    @BindView(R.id.iv_shuffle_song)
    protected ImageView mShuffleSongIV;
    @BindView(R.id.tv_current_duration)
    protected TextView mCurrentTimeTV;
    @BindView(R.id.tv_final_duration)
    protected TextView mFinalTimeTV;
    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;
    @BindView(R.id.rv_current_playlist)
    protected RecyclerView mCurrentPlaylistRV;
    @BindView(R.id.progress_bar)
    protected ProgressBar mProgressBar;
    @BindView(R.id.seekbar)
    protected SeekBar mSeekbar;
    private CurrentPlaylistAdapter mAdapter;
    private ICurrentPlaylistPresenter iCurrentPlaylistPresenter;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_music);
        ButterKnife.bind(this);
        mSeekbar.setOnSeekBarChangeListener(this);
        registerReceiver(mProgressReceiver, new IntentFilter(AppMusicKeys.UPDATE_PROGRESS));
        initToolbar();
        initRecyclerView();
        initPresenters();
        initViews();
    }

    private void initViews() {
        if (AppMusicKeys.SHUFFLE_STATE == AppMusicKeys.SHUFFLE_ON)
            mShuffleSongIV.setImageResource(R.drawable.shuffle_on);
        if (AppMusicKeys.REPEAT_STATE == AppMusicKeys.REPEAT_ON)
            mRepeatSongIV.setImageResource(R.drawable.repeat_on);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(" ");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initPresenters() {
        iCurrentPlaylistPresenter = new CurrentPlaylistPresenterImp(this);
        iMusicPresenter = new MusicPresenterImp(this, this);
        iMusicPresenter.bindService();
    }

    private void initRecyclerView() {
        mProgressBar.setVisibility(View.GONE);
        mCurrentPlaylistRV.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CurrentPlaylistAdapter(this, this);
        mCurrentPlaylistRV.setAdapter(mAdapter);
        ItemTouchHelper.Callback callback = new QueueItemTouchHelper(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mCurrentPlaylistRV);
    }

    @Override
    public void onClick(int pos) {
        MusicHelper.getInstance().setCurrentPosition(pos);
        if (iMusicPresenter != null)
            iMusicPresenter.playSong();
    }

    @Override
    public void onSongRemove(int position) {
        mDialog = ProgresDialog.showProgressDialog(this);
        MusicHelper.getInstance().getCurrentPlaylist().remove(position);
        mAdapter.notifyItemRemoved(position);
        int size = MusicHelper.getInstance().getCurrentPlaylist().size();
        mAdapter.notifyItemRangeChanged(position, size);
        int currPos = MusicHelper.getInstance().getCurrentPosition();
        if (position == currPos) {
            if (AppMusicKeys.REPEAT_STATE == AppMusicKeys.REPEAT_OFF) {
                MusicHelper.getInstance().setCurrentPosition(position - 1);
            }
            onCurrentPlayingSongRemoved();
        } else if (position < currPos) {
            MusicHelper.getInstance().setCurrentPosition(currPos - 1);
        }
        if (size == 0) {
            onSuccessfullyRemovedSong();
            Toast.makeText(this, "There are no songs to play.", Toast.LENGTH_LONG).show();
            onBackPressed();
            return;
        }
        onSuccessfullyRemovedSong();
    }

    @Override
    public void onSuccessfullyRemovedSong() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    @Override
    public void onCurrentPlayingSongRemoved() {
        if (iMusicPresenter != null)
            iMusicPresenter.onSkipToNext();
    }

    @Override
    public void onSongsMoved(int from, int to) {
        // iCurrentPlaylistPresenter.songsMoved(from, to);
        int pos = MusicHelper.getInstance().getCurrentPosition();
        Collections.swap(MusicHelper.getInstance().getCurrentPlaylist(), from, to);
        if (pos == to)
            pos = from;
        else if (pos == from)
            pos = to;
        MusicHelper.getInstance().setCurrentPosition(pos);
        mAdapter.notifyItemMoved(from, to);
    }

    @OnClick(R.id.iv_shuffle_song)
    public void onShuffle() {
        if (AppMusicKeys.SHUFFLE_STATE == AppMusicKeys.SHUFFLE_OFF) {
            AppMusicKeys.SHUFFLE_STATE = AppMusicKeys.SHUFFLE_ON;
            mShuffleSongIV.setImageResource(R.drawable.shuffle_on);
            Toast.makeText(this, "Shuffle turned on", Toast.LENGTH_LONG).show();
        } else {
            AppMusicKeys.SHUFFLE_STATE = AppMusicKeys.SHUFFLE_OFF;
            mShuffleSongIV.setImageResource(R.drawable.shuffle_off);
            Toast.makeText(this, "Shuffle turned off", Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.iv_repeat_song)
    public void onRepeat() {
        if (AppMusicKeys.REPEAT_STATE == AppMusicKeys.REPEAT_OFF) {
            AppMusicKeys.REPEAT_STATE = AppMusicKeys.REPEAT_ON;
            mRepeatSongIV.setImageResource(R.drawable.repeat_on);
            Toast.makeText(this, "Repeat turned on", Toast.LENGTH_LONG).show();
        } else {
            AppMusicKeys.REPEAT_STATE = AppMusicKeys.REPEAT_OFF;
            mRepeatSongIV.setImageResource(R.drawable.repeat_off);
            Toast.makeText(this, "Repeat turned off", Toast.LENGTH_LONG).show();
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
    protected void onDestroy() {
        iMusicPresenter.onDestroy();
        iCurrentPlaylistPresenter.onDestroy();
        iMusicPresenter = null;
        iCurrentPlaylistPresenter = null;
        unregisterReceiver(mProgressReceiver);
        super.onDestroy();
    }

    @Override
    public void onEmptyList() {
        Toast.makeText(this, "There are no songs in queue", Toast.LENGTH_LONG).show();
        onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.action_menu_now_playlist_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.add_to_playlist) {
            Intent intent = new Intent(this, CreatePlaylistActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        mCurrentTimeTV.setText(DateUtils.formatElapsedTime(progress / 1000));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        if (iMusicPresenter != null) {
            iMusicPresenter.seekTo(progress);
            mCurrentTimeTV.setText(DateUtils.formatElapsedTime(progress / 1000));
        }
    }

    @Override
    public void onHideMusicLayout() {
        super.onHideMusicLayout();
        finish();
    }

    private BroadcastReceiver mProgressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                long current = intent.getLongExtra(AppMusicKeys.CURRENT_PROGRESS, 0);
                long total = intent.getLongExtra(AppMusicKeys.TOTAL_PROGRESS, 0);
                mSeekbar.setProgress((int) current);
                mSeekbar.setMax((int) total);
                mCurrentTimeTV.setText(DateUtils.formatElapsedTime(current / 1000));
                mFinalTimeTV.setText(DateUtils.formatElapsedTime(total / 1000));
            }
        }
    };

    @Override
    public void onUpdateSongState(int state) {
        super.onUpdateSongState(state);
        if (mAdapter != null) {
            if (state == PlaybackStateCompat.STATE_PLAYING) {
                mAdapter.setIsPlaying(true);
            } else {
                mAdapter.setIsPlaying(false);
            }
            mAdapter.notifyDataSetChanged();
        }
    }
}
