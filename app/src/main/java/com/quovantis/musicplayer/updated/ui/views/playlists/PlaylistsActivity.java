package com.quovantis.musicplayer.updated.ui.views.playlists;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.models.UserPlaylistModel;
import com.quovantis.musicplayer.updated.ui.views.music.MusicBaseActivity;
import com.quovantis.musicplayer.updated.ui.views.music.MusicPresenterImp;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaylistsActivity extends MusicBaseActivity implements IPlaylistView {

    @BindView(R.id.tv_no_playlist)
    TextView mEmptyPlaylistTV;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rv_playlists_list)
    RecyclerView mPlaylistsListRV;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<UserPlaylistModel> mPlaylistsList;
    private IPlaylistPresenter iPlaylistPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlists);
        ButterKnife.bind(this);
        mPlaylistsList = new ArrayList<>();
        initToolbar();
        initRecyclerView();
        initPresenters();
    }

    private void initPresenters() {
        iPlaylistPresenter = new PlaylistPresenterImp(this);
        iMusicPresenter = new MusicPresenterImp(this, this);
        iMusicPresenter.bindService();
        iPlaylistPresenter.updateUI();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Playlists");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    private void initRecyclerView() {
        mPlaylistsListRV.setLayoutManager(new LinearLayoutManager(PlaylistsActivity.this));
        mAdapter = new PlaylistAdapter(mPlaylistsList, this);
        mPlaylistsListRV.setAdapter(mAdapter);
    }

    @Override
    public void onStopService() {

    }

    @Override
    public void onUpdateUI(List<UserPlaylistModel> list) {
        mPlaylistsListRV.setVisibility(View.VISIBLE);
        mPlaylistsList.clear();
        mPlaylistsList.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onShowProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHideProgres() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onNoPlaylist() {
        mPlaylistsListRV.setVisibility(View.GONE);
        mEmptyPlaylistTV.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iPlaylistPresenter.onDestroy();
        iMusicPresenter.onDestroy();
        iMusicPresenter = null;
        iPlaylistPresenter = null;
    }
}
