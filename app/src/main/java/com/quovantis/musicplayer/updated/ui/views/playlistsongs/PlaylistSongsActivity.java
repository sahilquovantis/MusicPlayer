package com.quovantis.musicplayer.updated.ui.views.playlistsongs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.interfaces.ICommonKeys;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.models.UserPlaylistModel;
import com.quovantis.musicplayer.updated.ui.views.music.MusicBaseActivity;
import com.quovantis.musicplayer.updated.ui.views.music.MusicPresenterImp;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaylistSongsActivity extends MusicBaseActivity implements IPlaylistSongsView {

    @BindView(R.id.tv_no_playlist_songs)
    TextView mEmptyPlaylistTV;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rv_playlist_songs_list)
    RecyclerView mPlaylistsListRV;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<SongDetailsModel> mPlaylistsList;
    private IPlaylistSongsPresenter iPlaylistSongsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_songs);
        ButterKnife.bind(this);
        mPlaylistsList = new ArrayList<>();
        initToolbar();
        initRecyclerView();
        Bundle bundle = getIntent().getExtras();
        long id  = -1;
        if (bundle != null) {
            id = bundle.getLong(ICommonKeys.FOLDER_ID_KEY, -1);
            String directory = bundle.getString(ICommonKeys.DIRECTORY_NAME_KEY);
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle(directory);
        }
        initPresenters(id);
    }

    private void initPresenters(long id) {
        iPlaylistSongsPresenter = new PlaylistSongsPresenterImp(this);
        iMusicPresenter = new MusicPresenterImp(this,this);
        iMusicPresenter.bindService();
        iPlaylistSongsPresenter.updateUI(id);
    }

    private void initRecyclerView() {
        mPlaylistsListRV.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PlaylistSongsAdapter(mPlaylistsList, this);
        mPlaylistsListRV.setAdapter(mAdapter);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Playlists");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    public void onStopService() {

    }

    @Override
    public void onUpdateUI(List<SongDetailsModel> list) {
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
    public void onHideProgress() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onEmptyList() {
        mEmptyPlaylistTV.setVisibility(View.VISIBLE);
        mPlaylistsListRV.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iMusicPresenter.onDestroy();
        iMusicPresenter = null;
    }
}
