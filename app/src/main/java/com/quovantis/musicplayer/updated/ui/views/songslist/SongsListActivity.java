package com.quovantis.musicplayer.updated.ui.views.songslist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.dialogs.QueueOptionsDialog;
import com.quovantis.musicplayer.updated.helper.MusicHelper;
import com.quovantis.musicplayer.updated.interfaces.ICommonKeys;
import com.quovantis.musicplayer.updated.interfaces.IMusicListClickListener;
import com.quovantis.musicplayer.updated.interfaces.IQueueOptionsDialog;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.ui.views.createplaylist.CreatePlaylistActivity;
import com.quovantis.musicplayer.updated.ui.views.music.MusicBaseActivity;
import com.quovantis.musicplayer.updated.ui.views.music.MusicPresenterImp;
import com.quovantis.musicplayer.updated.utility.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SongsListActivity extends MusicBaseActivity implements ISongsView,
        IMusicListClickListener, IQueueOptionsDialog.onSongClickListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rv_songs_list)
    RecyclerView mSongsListRV;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    private RecyclerView.Adapter mAdapter;
    private String mAction;
    private ArrayList<SongDetailsModel> mSongList;
    private ISongsPresenter iSongsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs_list);
        mSongList = new ArrayList<>();
        ButterKnife.bind(this);
        initToolbar();
        initRecyclerView();
        initPresenters();
        initBundle();
    }

    private void initBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String directory = bundle.getString(ICommonKeys.DIRECTORY_NAME_KEY);
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle(directory);
            if (getIntent().getAction() != null) {
                mAction = getIntent().getAction();
            }
            if (mAction.equalsIgnoreCase(ICommonKeys.PLAYLIST_ACTION)) {
                long id = bundle.getLong(ICommonKeys.FOLDER_ID_KEY, 1);
                iSongsPresenter.updateUI(id, mAction, null, this);
            } else {
                String mPath = bundle.getString(ICommonKeys.FOLDER_ID_KEY, null);
                iSongsPresenter.updateUI(0, mAction, mPath, this);
            }
        }
    }

    private void initPresenters() {
        iSongsPresenter = new SongsPresenterImp(this, this);
        iMusicPresenter = new MusicPresenterImp(this, SongsListActivity.this);
        iMusicPresenter.bindService();
    }

    private void initRecyclerView() {
        mSongsListRV.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SongsListAdapter(this, this, mSongList);
        mSongsListRV.setAdapter(mAdapter);
    }


    private void initToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }


    @Override
    public void onMusicListClick(int pos) {
        if (mSongList != null) {
            boolean isListAdded = MusicHelper.getInstance().setCurrentPlaylist(mSongList, pos);
            if (isListAdded) {
                if (iMusicPresenter != null)
                    iMusicPresenter.playSong();
            }
        }
    }

    @Override
    public void onActionOverFlowClick(SongDetailsModel model) {
        QueueOptionsDialog.showDialog(SongsListActivity.this, model, this);
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
        bundle.putString("Id", model.getSongPath());
        Intent intent = new Intent(this, CreatePlaylistActivity.class);
        intent.setAction(Utils.SONG_LIST);
        intent.putExtras(bundle);
        startActivity(intent);
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
    public void onHideProgres() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onEmptyList() {

    }
}
