package com.quovantis.musicplayer.updated.ui.views.createplaylist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.constants.AppKeys;
import com.quovantis.musicplayer.updated.dialogs.CreatePlaylistDialog;
import com.quovantis.musicplayer.updated.dialogs.CustomProgressDialog;
import com.quovantis.musicplayer.updated.models.UserPlaylistModel;
import com.quovantis.musicplayer.updated.ui.views.music.MusicBaseActivity;
import com.quovantis.musicplayer.updated.ui.views.music.MusicPresenterImp;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreatePlaylistActivity extends MusicBaseActivity implements ICreatePlaylistView,
        CreatePlaylistAdapter.IAddToExistingPlaylistClickListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rv_playlist)
    RecyclerView mPlaylistRV;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    private CreatePlaylistAdapter mAdapter;
    private ArrayList<UserPlaylistModel> mList;
    private CustomProgressDialog mProgressDialog;
    private ICreatePlaylistPresenter iCreatePlaylistPresenter;
    private String id = null;
    private String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_playlist);
        ButterKnife.bind(this);
        mList = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        if (getIntent() != null && getIntent().getAction() != null) {
            action = getIntent().getAction();
        }
        if (bundle != null)
            id = bundle.getString(AppKeys.CREATE_PLAYLIST_INTENT_PATH, null);
        initToolbar();
        initRecyclerView();
        initPresenters();
    }

    @Override
    protected void updateCurrentSongPlayingStatus(int state) {

    }

    private void initRecyclerView() {
        mPlaylistRV.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CreatePlaylistAdapter(this, mList, this);
        mPlaylistRV.setAdapter(mAdapter);
    }

    private void initPresenters() {
        iCreatePlaylistPresenter = new CreatePlaylistPresenterImp(this, this, this);
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

    private CreatePlaylistDialog.IOnCreatePlaylistDialogListener iOnCreatePlaylistDialog = new CreatePlaylistDialog.IOnCreatePlaylistDialogListener() {
        @Override
        public void onCreatePlaylist(String playlistName) {
            String message = "Creating playlist ...";
            mProgressDialog = new CustomProgressDialog(CreatePlaylistActivity.this);
            mProgressDialog.show();
            mProgressDialog.setMessage(message);
            iCreatePlaylistPresenter.createPlaylist(playlistName, id, action);
        }
    };

    @OnClick(R.id.rl_create_playlist)
    public void createNewPlaylist() {
        CreatePlaylistDialog dialog = new CreatePlaylistDialog(CreatePlaylistActivity.this, iOnCreatePlaylistDialog);
        dialog.show();
        if (dialog.getWindow() != null)
            dialog.getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }

    @Override
    public void onCancelCreatePlaylistProgressDialog(boolean isCreated) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        if (isCreated)
            Toast.makeText(this, "Playlist Updated", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
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
        mProgressDialog = new CustomProgressDialog(this);
        mProgressDialog.show();
        mProgressDialog.setMessage(message);
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
