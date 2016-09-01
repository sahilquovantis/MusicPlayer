package com.quovantis.musicplayer.updated.ui.views.folders;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.dialogs.ProgresDialog;
import com.quovantis.musicplayer.updated.dialogs.QueueOptionsDialog;
import com.quovantis.musicplayer.updated.dialogs.RefreshListDialog;
import com.quovantis.musicplayer.updated.interfaces.ICommonKeys;
import com.quovantis.musicplayer.updated.interfaces.IFolderClickListener;
import com.quovantis.musicplayer.updated.interfaces.IQueueOptionsDialog;
import com.quovantis.musicplayer.updated.models.SongPathModel;
import com.quovantis.musicplayer.updated.services.MusicService;
import com.quovantis.musicplayer.updated.ui.views.currentplaylist.CurrentPlaylistActivity;
import com.quovantis.musicplayer.updated.ui.views.music.MusicBaseActivity;
import com.quovantis.musicplayer.updated.ui.views.music.MusicPresenterImp;
import com.quovantis.musicplayer.updated.ui.views.playlists.PlaylistsActivity;
import com.quovantis.musicplayer.updated.ui.views.songslist.SongsListActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FoldersActivity extends MusicBaseActivity implements IFolderView,
        IFolderClickListener, NavigationView.OnNavigationItemSelectedListener,
        IQueueOptionsDialog.onFolderClickListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;
    @BindView(R.id.rv_folders_list)
    RecyclerView mFoldersListRV;
    @BindView(R.id.pb_progress_bar)
    ProgressBar mProgressBar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<SongPathModel> mFoldersList;
    private RefreshListDialog mRefreshListDialog;
    private IFoldersPresenter iFoldersPresenter;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folders);
        ButterKnife.bind(this);
        initiToolbar();
        mFoldersList = new ArrayList<>();
        initRecyclerView();
        initPresenters();
    }

    private void initiToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(" ");
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    private void initPresenters() {
        iFoldersPresenter = new FoldersPresenterImp(this);
        iMusicPresenter = new MusicPresenterImp(this, FoldersActivity.this);
        iFoldersPresenter.updateUI(this);
        iMusicPresenter.bindService();
    }

    private void initRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(FoldersActivity.this, 2);
        mFoldersListRV.setLayoutManager(layoutManager);
        mAdapter = new FoldersAdapter(FoldersActivity.this, mFoldersList, this);
        mFoldersListRV.setAdapter(mAdapter);
    }

    @Override
    public void onUpdateFoldersList(ArrayList<SongPathModel> foldersList) {
        mFoldersList.clear();
        mFoldersList.addAll(foldersList);
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showEmptyMessage() {

    }

    @Override
    public void updateRefreshListProgress(int size, int value) {
        mRefreshListDialog.updateProgress(value, size);
    }

    @Override
    public void updateRefreshListFetchedFolders(int size, int value) {
        mRefreshListDialog.updateFetchedSongs(value, size);
    }

    @Override
    public void cancelRefreshListDialog() {
        mRefreshListDialog.cancelProgressDialog();
        mRefreshListDialog = null;
    }

    @Override
    public void initializeRefreshListDialog() {
        mRefreshListDialog = new RefreshListDialog(FoldersActivity.this);
        mRefreshListDialog.showProgressDialog();
    }

    @Override
    public void onFoldersLongPress(SongPathModel songPathModel) {
        QueueOptionsDialog.showDialog(FoldersActivity.this, songPathModel, this);
    }

    @Override
    public void onFoldersSinglePress(long id, String directoryName) {
        Bundle bundle = new Bundle();
        bundle.putLong(ICommonKeys.FOLDER_ID_KEY, id);
        bundle.putString(ICommonKeys.DIRECTORY_NAME_KEY, directoryName);
        Intent intent = new Intent(FoldersActivity.this, SongsListActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_in_animation, R.anim.enter_out_animation);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iMusicPresenter.stopService();
        iFoldersPresenter.onDestroy();
        iMusicPresenter.onDestroy();
        iFoldersPresenter = null;
        iMusicPresenter = null;
    }

    @Override
    public void onStopService() {
        stopService(new Intent(this, MusicService.class));
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        mDrawerLayout.closeDrawer(mNavigationView);
        int id = item.getItemId();
        if (id == R.id.queue) {
            Intent intent = new Intent(this, CurrentPlaylistActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter_in_animation, R.anim.enter_out_animation);
            return true;
        } else if (id == R.id.playlists) {
            Intent intent = new Intent(this, PlaylistsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter_in_animation, R.anim.enter_out_animation);
            return true;
        } else if (id == R.id.resync_device) {
            iMusicPresenter.hideMusicLayoutDuringResyncMusic();
            iFoldersPresenter.syncMusic(this);
            return true;
        }
        return false;
    }

    @Override
    public void onClick(SongPathModel model, boolean isClearQueue, boolean isPlaythisSong) {
        mDialog = ProgresDialog.showProgressDialog(this);
        iMusicPresenter.addSongToPlaylist(model.getId(), isClearQueue, isPlaythisSong);
    }

    @Override
    public void cancelDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    @Override
    public void updateMusicProgress(PlaybackStateCompat playbackState) {

    }

    @Override
    public void updateMusicDurationInitial(MediaMetadataCompat mediaMetadata) {

    }
}
