package com.quovantis.musicplayer.updated.ui.views.createplaylist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Parcelable;
import android.provider.UserDictionary;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.dialogs.CreatePlaylistDialog;
import com.quovantis.musicplayer.updated.dialogs.ProgresDialog;
import com.quovantis.musicplayer.updated.interfaces.IAddToExistingPlaylistClickListener;
import com.quovantis.musicplayer.updated.interfaces.IOnCreatePlaylistDialog;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.models.UserPlaylistModel;
import com.quovantis.musicplayer.updated.ui.views.music.MusicBaseActivity;
import com.quovantis.musicplayer.updated.ui.views.music.MusicPresenterImp;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreatePlaylistActivity extends MusicBaseActivity implements ICreatePlaylistView,
        IAddToExistingPlaylistClickListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rv_playlist)
    RecyclerView mPlaylistRV;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    private CreatePlaylistAdapter mAdapter;
    private ArrayList<UserPlaylistModel> mList;
    private ProgressDialog mCreatePlaylistProgressDialog;
    private ICreatePlaylistPresenter iCreatePlaylistPresenter;
    private ArrayList<SongDetailsModel> mSongsList;
    private String id = null;
    private String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_playlist);
        ButterKnife.bind(this);
        mList = new ArrayList<>();
        mSongsList = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        if (getIntent() != null && getIntent().getAction() != null) {
            action = getIntent().getAction();
        }
        if (bundle != null)
            id = bundle.getString("Id", null);
        initToolbar();
        initRecyclerView();
        initPresenters();
    }

    private void initRecyclerView() {
        mPlaylistRV.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CreatePlaylistAdapter(this, mList, this);
        mPlaylistRV.setAdapter(mAdapter);
    }

    private void initPresenters() {
        iCreatePlaylistPresenter = new CreatePlaylistPresenterImp(this);
        iMusicPresenter = new MusicPresenterImp(this, this);
        iMusicPresenter.bindService();
        iCreatePlaylistPresenter.updateUI();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add to playlist");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    private IOnCreatePlaylistDialog iOnCreatePlaylistDialog = new IOnCreatePlaylistDialog() {
        @Override
        public void onCreatePlaylist(String playlistName) {
            String message = "Creating playlist ...";
            mCreatePlaylistProgressDialog = ProgresDialog.
                    showProgressDialog(CreatePlaylistActivity.this, message);
            iCreatePlaylistPresenter.createPlaylist(playlistName, id, action);
        }
    };

    @OnClick(R.id.rl_create_playlist)
    public void createNewPlaylist() {
        CreatePlaylistDialog.showDialog(CreatePlaylistActivity.this, iOnCreatePlaylistDialog);
    }

    @Override
    public void onCancelCreatePlaylistProgressDialog(boolean isCreated) {
        if (mCreatePlaylistProgressDialog != null)
            mCreatePlaylistProgressDialog.cancel();
        if (isCreated)
            Toast.makeText(this, "Playlist Updated", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        onBackPressed();
    }

    @Override
    public void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hidePRogress() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iMusicPresenter.onDestroy();
        iMusicPresenter = null;
        iCreatePlaylistPresenter.onDestory();
        iCreatePlaylistPresenter = null;
    }

    @Override
    public void updateUI(List<UserPlaylistModel> list) {
        if (list != null && !list.isEmpty()) {
            mList.clear();
            mList.addAll(list);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onAddToExistingPlaylist(UserPlaylistModel model) {
        String message = "Updating ...";
        mCreatePlaylistProgressDialog = ProgresDialog.
                showProgressDialog(CreatePlaylistActivity.this, message);
        iCreatePlaylistPresenter.addToExistingPlaylist(model, id, action);
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
