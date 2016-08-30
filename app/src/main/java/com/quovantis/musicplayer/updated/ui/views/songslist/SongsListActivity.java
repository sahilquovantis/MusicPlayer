package com.quovantis.musicplayer.updated.ui.views.songslist;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.dialogs.QueueOptionsDialog;
import com.quovantis.musicplayer.updated.interfaces.ICommonKeys;
import com.quovantis.musicplayer.updated.interfaces.IMusicListClickListener;
import com.quovantis.musicplayer.updated.interfaces.IQueueOptionsDialog;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.ui.views.music.MusicBaseActivity;
import com.quovantis.musicplayer.updated.ui.views.music.MusicPresenterImp;

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
    private ArrayList<SongDetailsModel> mSongList;
    private ISongsPresenter iSongsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs_list);
        ButterKnife.bind(this);
        mSongList = new ArrayList<>();
        initToolbar();
        initRecyclerView();
        iSongsPresenter = new SongsPresenterImp(this);
        iMusicPresenter = new MusicPresenterImp(this, SongsListActivity.this);
        iMusicPresenter.bindService();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            long id = bundle.getLong(ICommonKeys.FOLDER_ID_KEY, -1);
            String directory = bundle.getString(ICommonKeys.DIRECTORY_NAME_KEY);
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle(directory);
            iSongsPresenter.updateUI(id);
        }
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    private void initRecyclerView() {
        mSongsListRV.setLayoutManager(new LinearLayoutManager(SongsListActivity.this));
        mAdapter = new SongsListAdapter(this, this, mSongList);
        mSongsListRV.setAdapter(mAdapter);
    }

    @Override
    public void onUpdateUI(ArrayList<SongDetailsModel> list) {
        mSongList.clear();
        mSongList.addAll(list);
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
    public void onMusicListClick(SongDetailsModel model) {
        iMusicPresenter.addSongToPlaylist(model, false, true);
    }

    @Override
    public void onActionOverFlowClick(SongDetailsModel model) {
        QueueOptionsDialog.showDialog(SongsListActivity.this, model, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iSongsPresenter.onDestroy();
        iMusicPresenter.onDestroy();
        iSongsPresenter = null;
        iMusicPresenter = null;
    }

    @Override
    public void onClick(SongDetailsModel model, boolean isClearQueue, boolean isPlaythisSong) {
        iMusicPresenter.addSongToPlaylist(model, isClearQueue, isPlaythisSong);
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
}
