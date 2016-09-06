package com.quovantis.musicplayer.updated.ui.views.playlists;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.interfaces.ICommonKeys;
import com.quovantis.musicplayer.updated.interfaces.IPlaylistClickListener;
import com.quovantis.musicplayer.updated.models.UserPlaylistModel;
import com.quovantis.musicplayer.updated.ui.views.music.MusicBaseActivity;
import com.quovantis.musicplayer.updated.ui.views.music.MusicPresenterImp;
import com.quovantis.musicplayer.updated.ui.views.songslist.SongsListActivity;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaylistsActivity extends MusicBaseActivity implements IPlaylistView,
        IPlaylistClickListener {

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
        mAdapter = new PlaylistAdapter(mPlaylistsList, this, this);
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
        iPlaylistPresenter.onDestroy();
        iMusicPresenter.onDestroy();
        iMusicPresenter = null;
        iPlaylistPresenter = null;
        super.onDestroy();
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
    public void onClick(long id, String name) {
        Bundle bundle = new Bundle();
        bundle.putLong(ICommonKeys.FOLDER_ID_KEY, id);
        bundle.putString(ICommonKeys.DIRECTORY_NAME_KEY, name);
        Intent intent = new Intent(this, SongsListActivity.class);
        intent.setAction(ICommonKeys.PLAYLIST_ACTION);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_in_animation, R.anim.enter_out_animation);
    }

    @Override
    public void updateMusicProgress(PlaybackStateCompat playbackState) {

    }
    @Override
    public void updateMusicDurationInitial(MediaMetadataCompat mediaMetadata) {

    }
}
