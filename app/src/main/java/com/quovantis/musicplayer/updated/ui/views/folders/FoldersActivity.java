package com.quovantis.musicplayer.updated.ui.views.folders;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.dialogs.RefreshListDialog;
import com.quovantis.musicplayer.updated.dialogs.SongOptionsDialog;
import com.quovantis.musicplayer.updated.interfaces.ICommonKeys;
import com.quovantis.musicplayer.updated.interfaces.IFolderClickListener;
import com.quovantis.musicplayer.updated.models.SongPathModel;
import com.quovantis.musicplayer.updated.services.MusicService;
import com.quovantis.musicplayer.updated.ui.views.music.IMusicPresenter;
import com.quovantis.musicplayer.updated.ui.views.music.IMusicView;
import com.quovantis.musicplayer.updated.ui.views.music.MusicPresenterImp;
import com.quovantis.musicplayer.updated.ui.views.songslist.SongsListActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FoldersActivity extends AppCompatActivity implements IFolderView,
        IFolderClickListener, IMusicView {

    /**
     * Music Layout BindViews
     */
    @BindView(R.id.rl_music_layout)
    RelativeLayout mMusicLayout;
    @BindView(R.id.iv_selected_song_thumbnail)
    ImageView mSelectedSongThumbnailIV;
    @BindView(R.id.tv_selected_song)
    public TextView mSelectedSongTV;
    @BindView(R.id.tv_selected_song_artist)
    public TextView mSelectedSongArtistTV;
    @BindView(R.id.iv_play_pause_button)
    public ImageView mPlayPauseIV;

    /**
     * Folders Activity Views
     */
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
    private IMusicPresenter iMusicPresenter;

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
        getSupportActionBar().setTitle("Folders");
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
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

    @OnClick({R.id.iv_next_song, R.id.iv_previous_song, R.id.iv_play_pause_button})
    public void onMusicButtonsClick(ImageView view) {
        if (view.getId() == R.id.iv_play_pause_button) {
            iMusicPresenter.onPlayPause();
        } else if (view.getId() == R.id.iv_previous_song) {
            iMusicPresenter.onSkipToPrevious();
        } else if (view.getId() == R.id.iv_next_song) {
            iMusicPresenter.onSkipToNext();
        }
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
    public void onFoldersLongPress() {
        SongOptionsDialog dialog = new SongOptionsDialog(FoldersActivity.this);
        dialog.showDialog();
    }

    @Override
    public void onFoldersSinglePress(long id, String directoryName) {
        Bundle bundle = new Bundle();
        bundle.putLong(ICommonKeys.FOLDER_ID_KEY, id);
        bundle.putString(ICommonKeys.DIRECTORY_NAME_KEY, directoryName);
        Intent intent = new Intent(FoldersActivity.this, SongsListActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onUpdateSongUI(String title, String artist, Bitmap bitmap) {
        mSelectedSongTV.setText(title);
        mSelectedSongArtistTV.setText(artist);
        mSelectedSongThumbnailIV.setImageBitmap(bitmap);
    }

    @Override
    public void onUpdateSongState(int state) {
        if (state == PlaybackStateCompat.STATE_PLAYING) {
            mPlayPauseIV.setImageResource(R.drawable.ic_action_pause);
        } else {
            mPlayPauseIV.setImageResource(R.drawable.ic_action_play);
        }
    }

    @Override
    public void onHideMusicLayout() {
        mMusicLayout.setVisibility(View.GONE);
    }

    @Override
    public void onShowMusicLayout() {
        mMusicLayout.setVisibility(View.VISIBLE);
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
}
