package com.quovantis.musicplayer.updated.ui.views.songslist;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.constants.AppKeys;
import com.quovantis.musicplayer.updated.controller.AppActionController;
import com.quovantis.musicplayer.updated.dialogs.QueueOptionsDialog;
import com.quovantis.musicplayer.updated.helper.MusicHelper;
import com.quovantis.musicplayer.updated.interfaces.IMusicListClickListener;
import com.quovantis.musicplayer.updated.interfaces.IQueueOptionsDialog;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.ui.views.createplaylist.CreatePlaylistActivity;
import com.quovantis.musicplayer.updated.ui.views.customviews.CustomFastScroller;
import com.quovantis.musicplayer.updated.ui.views.music.MusicBaseActivity;
import com.quovantis.musicplayer.updated.ui.views.music.MusicPresenterImp;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SongsListActivity extends MusicBaseActivity implements ISongsView,
        IMusicListClickListener, IQueueOptionsDialog.onSongClickListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fast_scroller)
    CustomFastScroller mFastScroller;
    @BindView(R.id.rv_songs_list)
    RecyclerView mSongsListRV;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<SongDetailsModel> mSongList;
    private ISongsPresenter iSongsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs_list);
        mSongList = new ArrayList<>();
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        String title = null, path = null;
        if (bundle != null) {
            title = bundle.getString(AppKeys.DIRECTORY_NAME_KEY);
            path = bundle.getString(AppKeys.FOLDER_ID_KEY, null);
        }
        initToolbar(title);
        initRecyclerView();
        initPresenters(path);
    }

    @Override
    protected void updateCurrentSongPlayingStatus(int state) {

    }

    private void initPresenters(String path) {
        iSongsPresenter = new SongsPresenterImp(this);
        iMusicPresenter = new MusicPresenterImp(this, SongsListActivity.this);
        if (path != null)
            iSongsPresenter.updateUI(path, this);
    }

    private void initRecyclerView() {
        mSongsListRV.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SongsListAdapter(this, this, mSongList);
        mSongsListRV.setAdapter(mAdapter);
        mFastScroller.setRecyclerView(mSongsListRV);
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
    public void onActionOverFlowClick(SongDetailsModel model) {
        QueueOptionsDialog dialog = new QueueOptionsDialog(this, model, this);
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        iMusicPresenter.onDestroy();
        iMusicPresenter = null;
        iSongsPresenter.onDestroy();
        iSongsPresenter = null;
        super.onDestroy();
    }

    @Override
    public void onClickFromSpecificSongOptionsDialog(SongDetailsModel model, boolean isClearQueue, boolean isPlaythisSong) {
        if (iMusicPresenter != null)
            iMusicPresenter.addSongToPlaylist(model, isClearQueue, isPlaythisSong);
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
    public void onUpdateUI(ArrayList<SongDetailsModel> list) {
        mSongList.clear();
        mSongList.addAll(list);
        mAdapter.notifyDataSetChanged();
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

    }
}
