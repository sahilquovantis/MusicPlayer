package com.quovantis.musicplayer.updated.ui.views.current_playlist;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.helper.MusicHelper;
import com.quovantis.musicplayer.updated.helper.QueueItemTouchHelper;
import com.quovantis.musicplayer.updated.interfaces.ICurrentPlaylistClickListener;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.ui.views.music.MusicBaseActivity;
import com.quovantis.musicplayer.updated.ui.views.music.MusicPresenterImp;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CurrentPlaylistActivity extends MusicBaseActivity implements ICurrentPlaylistView,
        ICurrentPlaylistClickListener {


    @BindView(R.id.tv_empty_list)
    TextView mEmptyTV;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rv_current_playlist)
    RecyclerView mCurrentPlaylistRV;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    private CurrentPlaylistAdapter mAdapter;
    private ArrayList<SongDetailsModel> mQueueList;
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
        iMusicPresenter = new MusicPresenterImp(this, CurrentPlaylistActivity.this);
        iMusicPresenter.bindService();
        iCurrentPlaylistPresenter.updateUI();
    }

    private void initRecyclerView() {
        mCurrentPlaylistRV.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CurrentPlaylistAdapter(mQueueList, this, this);
        mCurrentPlaylistRV.setAdapter(mAdapter);
        ItemTouchHelper.Callback callback = new QueueItemTouchHelper(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mCurrentPlaylistRV);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Current Playlist");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
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

    @Override
    public void onStopService() {

    }

    @Override
    public void onClick(SongDetailsModel model) {
        iMusicPresenter.addSongToPlaylist(model, false, true);
    }

    @Override
    public void onSongRemove(int pos) {
        iMusicPresenter.songRemoved(pos);
    }

    @Override
    public void onSongsMoved(int from, int to) {
        iMusicPresenter.songsMoved(from, to);
    }
}
