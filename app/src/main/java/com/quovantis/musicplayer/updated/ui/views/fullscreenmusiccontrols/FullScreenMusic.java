package com.quovantis.musicplayer.updated.ui.views.fullscreenmusiccontrols;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.helper.MusicHelper;
import com.quovantis.musicplayer.updated.helper.QueueItemTouchHelper;
import com.quovantis.musicplayer.updated.interfaces.ICommonKeys;
import com.quovantis.musicplayer.updated.interfaces.ICurrentPlaylistClickListener;
import com.quovantis.musicplayer.updated.interfaces.IOnCreatePlaylistDialog;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.ui.views.currentplaylist.CurrentPlaylistAdapter;
import com.quovantis.musicplayer.updated.ui.views.currentplaylist.CurrentPlaylistPresenterImp;
import com.quovantis.musicplayer.updated.ui.views.currentplaylist.ICurrentPlaylistPresenter;
import com.quovantis.musicplayer.updated.ui.views.currentplaylist.ICurrentPlaylistView;
import com.quovantis.musicplayer.updated.ui.views.music.MusicBaseActivity;
import com.quovantis.musicplayer.updated.ui.views.music.MusicPresenterImp;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FullScreenMusic extends MusicBaseActivity implements ICurrentPlaylistView,
        ICurrentPlaylistClickListener, SeekBar.OnSeekBarChangeListener {

    @BindView(R.id.tv_current_duration)
    TextView mCurrentTimeTV;
    @BindView(R.id.tv_final_duration)
    TextView mFinalTimeTV;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rv_current_playlist)
    RecyclerView mCurrentPlaylistRV;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.seekbar)
    SeekBar mSeekbar;
    private CurrentPlaylistAdapter mAdapter;
    private ArrayList<SongDetailsModel> mQueueList;
    private ICurrentPlaylistPresenter iCurrentPlaylistPresenter;
    private PlaybackStateCompat mPlaybackState;
    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_music);
        ButterKnife.bind(this);
        mQueueList = new ArrayList<>();
        mSeekbar.setOnSeekBarChangeListener(this);
        initToolbar();
        initRecyclerView();
        initPresenters();
        initTimer();
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
        iCurrentPlaylistPresenter.updateUI();
        iMusicPresenter.bindService();
    }

    private void initRecyclerView() {
        mCurrentPlaylistRV.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CurrentPlaylistAdapter(mQueueList, this, this);
        mCurrentPlaylistRV.setAdapter(mAdapter);
        ItemTouchHelper.Callback callback = new QueueItemTouchHelper(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mCurrentPlaylistRV);
    }

    @Override
    public void onClick(SongDetailsModel model) {
        iMusicPresenter.addSongToPlaylist(model, false, true);
    }

    @Override
    public void onSongRemove(int pos) {
        iCurrentPlaylistPresenter.songRemoved(pos);
    }

    @Override
    public void onCurrentPlayingSongRemoved() {
        iMusicPresenter.onSkipToNext();
    }

    @Override
    public void onSongsMoved(int from, int to) {
        iCurrentPlaylistPresenter.songsMoved(from, to);
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
        mQueueList.clear();
        mQueueList.addAll(currentPlaylistList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iMusicPresenter.onDestroy();
        iCurrentPlaylistPresenter.onDestroy();
        iMusicPresenter = null;
        iCurrentPlaylistPresenter = null;
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
        mPlaybackState = null;
    }

    @Override
    public void onEmptyList() {
        Toast.makeText(this, "There are no songs in queue", Toast.LENGTH_LONG).show();
        onBackPressed();
    }

    @Override
    public void onCancelCreatePlaylistProgressDialog(boolean isCreated) {

    }

    @Override
    public void onStopService() {

    }

    @Override
    public void updateMusicProgress(PlaybackStateCompat playbackState) {
        mPlaybackState = playbackState;
        List<SongDetailsModel> list = MusicHelper.getInstance().getCurrentPlaylist();
        if (list == null || list.isEmpty()) {
            onBackPressed();
            return;
        }
        updateProgress();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateProgress() {
        if (mPlaybackState == null) {
            return;
        }
        long currentPosition = mPlaybackState.getPosition();
        int state = mPlaybackState.getState();
        if (state == PlaybackStateCompat.STATE_PLAYING) {
            long timeDelta = SystemClock.elapsedRealtime() -
                    mPlaybackState.getLastPositionUpdateTime();
            currentPosition += (int) timeDelta * mPlaybackState.getPlaybackSpeed();
        }
        mSeekbar.setProgress((int) currentPosition);
    }

    @Override
    public void updateMusicDurationInitial(MediaMetadataCompat metadata) {
        if (metadata == null) {
            return;
        }
        int duration = (int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
        mSeekbar.setProgress(0);
        mSeekbar.setMax(duration);
        mFinalTimeTV.setText(DateUtils.formatElapsedTime(duration / 1000));
    }

    private void initTimer() {
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateProgress();
                    }
                });
            }
        }, 0, 1000);
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
        iMusicPresenter.seekTo(progress);
        mCurrentTimeTV.setText(DateUtils.formatElapsedTime(progress / 1000));
    }
}
