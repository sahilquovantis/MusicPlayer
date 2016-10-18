package com.quovantis.musicplayer.updated.ui.views.playlistsongslist;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.constants.AppKeys;
import com.quovantis.musicplayer.updated.controller.AppActionController;
import com.quovantis.musicplayer.updated.dialogs.CustomProgressDialog;
import com.quovantis.musicplayer.updated.dialogs.PlaylistSongsOptionsDialog;
import com.quovantis.musicplayer.updated.helper.MusicHelper;
import com.quovantis.musicplayer.updated.helper.QueueItemTouchHelper;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.ui.views.createplaylist.CreatePlaylistActivity;
import com.quovantis.musicplayer.updated.ui.views.music.MusicBaseActivity;
import com.quovantis.musicplayer.updated.ui.views.music.MusicPresenterImp;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaylistSongsActivity extends MusicBaseActivity implements PlaylistSongsOptionsDialog.OnPlaylistSongsDialogClickListener,
        PlaylistSongsAdapter.IMusicListClickListener, IPlaylistSongsView {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rv_songs_list)
    RecyclerView mSongsListRV;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    private PlaylistSongsAdapter mAdapter;
    private ArrayList<SongDetailsModel> mSongList;
    private IPlaylistSongsPresenter iPresenter;
    private CustomProgressDialog mProgressDialog;
    private long mPlaylistID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_songs);
        mSongList = new ArrayList<>();
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        long id = 1;
        String title = null;
        if (bundle != null) {
            id = bundle.getLong(AppKeys.FOLDER_ID_KEY, 1);
            mPlaylistID = id;
            title = bundle.getString(AppKeys.DIRECTORY_NAME_KEY);
        }
        initToolbar(title);
        initRecyclerView();
        initPresenters(id);
    }

    @Override
    protected void updateCurrentSongPlayingStatus(int val) {

    }

    private void initPresenters(long id) {
        iPresenter = new PlaylistSongsPresenterImp(this, this);
        iMusicPresenter = new MusicPresenterImp(this, PlaylistSongsActivity.this);
        iMusicPresenter.bindService();
        iPresenter.updateUI(id);
    }

    private void initRecyclerView() {
        mSongsListRV.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PlaylistSongsAdapter(this, this, mSongList);
        mSongsListRV.setAdapter(mAdapter);
        ItemTouchHelper.Callback callback = new QueueItemTouchHelper(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mSongsListRV);
    }


    private void initToolbar(String title) {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            if (title != null)
                getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onUpdateUI(ArrayList<SongDetailsModel> list) {
        mSongList.clear();
        mSongList.addAll(list);
        mAdapter.notifyDataSetChanged();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @Override
    public void onShowProgress() {
        mProgressBar.setProgress(View.VISIBLE);
    }

    @Override
    public void onHideProgress() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onEmptyList() {
        mSongList.clear();
        mAdapter.notifyDataSetChanged();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @Override
    public void onMusicListClick(int pos) {
        if (mSongList != null && MusicHelper.getInstance().isSongStartedPlaying()) {
            MusicHelper.getInstance().setIsSongStartedPlaying(false);
            boolean isListAdded = MusicHelper.getInstance().setCurrentPlaylist(mSongList, pos);
            if (isListAdded) {
                if (iMusicPresenter != null)
                    iMusicPresenter.playSong();
            }
        }
    }

    @Override
    public void onItemMovedCompleted() {
        mProgressDialog = new CustomProgressDialog(this);
        mProgressDialog.show();
        iPresenter.saveToPlaylist(mSongList, mPlaylistID);
    }

    @Override
    public void onActionOverFlowClick(SongDetailsModel model) {
        PlaylistSongsOptionsDialog dialog = new PlaylistSongsOptionsDialog(this, model, this);
        dialog.show();
    }

    @Override
    public void onClickFromSpecificSongOptionsDialog(SongDetailsModel model, boolean isClearQueue, boolean isPlaythisSong) {
        if (iMusicPresenter != null)
            iMusicPresenter.addSongToPlaylist(model, isClearQueue, isPlaythisSong);
    }

    @Override
    public void onRemove(SongDetailsModel model) {
        mProgressDialog = new CustomProgressDialog(this);
        mProgressDialog.show();
        iPresenter.removeFromPlaylist(mPlaylistID, model);
    }

    @Override
    public void onAddToPlaylist(SongDetailsModel model) {
        Bundle bundle = new Bundle();
        bundle.putString(AppKeys.CREATE_PLAYLIST_INTENT_PATH, model.getSongPath());
        new AppActionController.Builder(this)
                .from(this)
                .setTargetActivity(CreatePlaylistActivity.class)
                .setBundle(bundle)
                .setIntentAction(AppKeys.SONG_LIST)
                .build()
                .execute();
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
    protected void onDestroy() {
        iMusicPresenter.onDestroy();
        iMusicPresenter = null;
        iPresenter.onDestroy();
        iPresenter = null;
        super.onDestroy();
    }
}