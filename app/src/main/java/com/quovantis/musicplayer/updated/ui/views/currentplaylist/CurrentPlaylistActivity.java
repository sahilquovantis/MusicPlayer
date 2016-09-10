package com.quovantis.musicplayer.updated.ui.views.currentplaylist;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.dialogs.CreatePlaylistDialog;
import com.quovantis.musicplayer.updated.dialogs.ProgresDialog;
import com.quovantis.musicplayer.updated.helper.MusicHelper;
import com.quovantis.musicplayer.updated.helper.QueueItemTouchHelper;
import com.quovantis.musicplayer.updated.interfaces.ICommonKeys;
import com.quovantis.musicplayer.updated.interfaces.ICurrentPlaylistClickListener;
import com.quovantis.musicplayer.updated.interfaces.IOnCreatePlaylistDialog;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.ui.views.createplaylist.CreatePlaylistActivity;
import com.quovantis.musicplayer.updated.ui.views.music.MusicBaseActivity;
import com.quovantis.musicplayer.updated.ui.views.music.MusicPresenterImp;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    private ICurrentPlaylistPresenter iCurrentPlaylistPresenter;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_playlist);
        ButterKnife.bind(this);
        initToolbar();
        initRecyclerView();
        initPresenters();
    }

    private void initPresenters() {
        iCurrentPlaylistPresenter = new CurrentPlaylistPresenterImp(this);
        iMusicPresenter = new MusicPresenterImp(this, CurrentPlaylistActivity.this);
        iMusicPresenter.bindService();
    }

    private void initRecyclerView() {
        if (MusicHelper.getInstance().getCurrentPlaylist().isEmpty()) {
            Toast.makeText(this, "Current queue is empty", Toast.LENGTH_LONG).show();
            onBackPressed();
        }
        mCurrentPlaylistRV.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CurrentPlaylistAdapter(MusicHelper.getInstance().getCurrentPlaylist(), this, this);
        mCurrentPlaylistRV.setAdapter(mAdapter);
        ItemTouchHelper.Callback callback = new QueueItemTouchHelper(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mCurrentPlaylistRV);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Now Playing");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
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
    public void onEmptyList() {
        mEmptyTV.setVisibility(View.VISIBLE);
        Toast.makeText(this, "There are no songs in queue", Toast.LENGTH_LONG).show();
        onBackPressed();
    }

    @Override
    protected void onDestroy() {
        iCurrentPlaylistPresenter.onDestroy();
        iMusicPresenter.onDestroy();
        iCurrentPlaylistPresenter = null;
        iMusicPresenter = null;
        super.onDestroy();
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
    public void onClick(int pos) {
        MusicHelper.getInstance().setCurrentPosition(pos);
        if (iMusicPresenter != null)
            iMusicPresenter.playSong();
    }

    @Override
    public void onSongRemove(int pos) {
        mDialog = ProgresDialog.showProgressDialog(this);
        iCurrentPlaylistPresenter.songRemoved(pos);
        mAdapter.notifyItemRemoved(pos);
    }

    @Override
    public void onSuccessfullyRemovedSong(int currentPos) {
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
        iCurrentPlaylistPresenter.songsMoved(from, to);
        mAdapter.notifyItemMoved(from, to);
    }
    @Override
    public void changeToolbarColor(int color) {
        if (mToolbar != null) {
            mToolbar.setBackgroundColor(color);
        }
    }
}
