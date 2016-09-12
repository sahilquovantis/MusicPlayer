package com.quovantis.musicplayer.updated.ui.views.home;

import android.app.Dialog;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.dialogs.ProgresDialog;
import com.quovantis.musicplayer.updated.dialogs.RefreshListDialog;
import com.quovantis.musicplayer.updated.interfaces.IHomeAndFolderCommunicator;
import com.quovantis.musicplayer.updated.interfaces.IHomeAndMusicCommunicator;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.models.SongPathModel;
import com.quovantis.musicplayer.updated.services.MusicService;
import com.quovantis.musicplayer.updated.ui.views.allsongs.AllSongsFragment;
import com.quovantis.musicplayer.updated.ui.views.folders.FoldersFragment;
import com.quovantis.musicplayer.updated.ui.views.music.MusicBaseActivity;
import com.quovantis.musicplayer.updated.ui.views.music.MusicPresenterImp;
import com.quovantis.musicplayer.updated.ui.views.playlists.PlayistFragment;
import com.quovantis.musicplayer.updated.ui.views.search.SearchActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends MusicBaseActivity implements IHomeAndFolderCommunicator,
        IHomeAndMusicCommunicator, IHomeView {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.tabs)
    TabLayout mTabs;
    private Dialog mDialog;
    private HomeAdapter homeAdapter;
    private RefreshListDialog mRefreshListDialog;
    private IHomePresenter iHomePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        initToolbar();
        initPresenter();
        initViewPager();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Music");
    }

    private void initPresenter() {
        iHomePresenter = new HomePresenterImp(this);
        iMusicPresenter = new MusicPresenterImp(this, this);
        iMusicPresenter.bindService();
        iHomePresenter.firstTimeSync(this);
    }

    private void initViewPager() {
        homeAdapter = new HomeAdapter(getSupportFragmentManager());
        homeAdapter.addFragments(new FoldersFragment(), "Folders");
        homeAdapter.addFragments(new AllSongsFragment(), "Music");
        homeAdapter.addFragments(new PlayistFragment(), "Playlists");
        mViewPager.setAdapter(homeAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mTabs.setupWithViewPager(mViewPager);
    }

    @Override
    public void onOptionsDialogClick(SongPathModel model, boolean isClearQueue, boolean isPlaythisSong) {
        mDialog = ProgresDialog.showProgressDialog(this);
        iMusicPresenter.addSongToPlaylist(model.getId(), isClearQueue, isPlaythisSong);
    }

    @Override
    public void cancelDialog() {
        super.cancelDialog();
        if (mDialog != null)
            mDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        iHomePresenter.onDestroy();
        iHomePresenter = null;
        iMusicPresenter.stopService();
        iMusicPresenter.onDestroy();
        iMusicPresenter = null;
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_home_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            return true;
        } else if (item.getItemId() == R.id.resync) {
            iMusicPresenter.hideMusicLayoutDuringResyncMusic();
            iHomePresenter.syncMusic(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStopService() {
        stopService(new Intent(this, MusicService.class));
    }

    @Override
    public void onMusicListClick() {
        if (iMusicPresenter != null)
            iMusicPresenter.playSong();
    }

    @Override
    public void onOptionsDialogClick(SongDetailsModel model, boolean isClearQueue, boolean isPlaythisSong) {
        if (iMusicPresenter != null)
            iMusicPresenter.addSongToPlaylist(model, isClearQueue, isPlaythisSong);
    }

    /**
     * Update Progress Bar in Dialog {@link RefreshListDialog} while Fetching Songs from the Storage.
     *
     * @param size  Total Number of Songs
     * @param value Number of Fetched Songs
     */
    @Override
    public void updateRefreshListProgress(int size, int value) {
        mRefreshListDialog.updateProgress(value, size);
    }

    /**
     * Update Dialog {@link RefreshListDialog} and Shows Number of fetched songs.
     *
     * @param size  Total Number of Songs
     * @param value Number of Fetched Songs
     */
    @Override
    public void updateRefreshListFetchedFolders(int size, int value) {
        mRefreshListDialog.updateFetchedSongs(value, size);
    }

    /**
     * Cancel the Dialog {@link RefreshListDialog} after getting songs.
     */
    @Override
    public void cancelRefreshListDialog() {
        if (mRefreshListDialog != null) {
            mRefreshListDialog.cancelProgressDialog();
            mRefreshListDialog = null;
        }
    }

    /**
     * Initialize the Dialog {@link RefreshListDialog}
     */
    @Override
    public void initializeRefreshListDialog() {
        mRefreshListDialog = new RefreshListDialog(this);
        mRefreshListDialog.showProgressDialog();
    }

    @Override
    public void onRefreshSuccess() {
        FoldersFragment foldersFragment = (FoldersFragment) homeAdapter.getFirstItem(0);
        if (foldersFragment != null) {
            foldersFragment.getListOnNotifyFromHome();
        }
        AllSongsFragment fragment = (AllSongsFragment) homeAdapter.getFirstItem(1);
        if (fragment != null) {
            fragment.getListOnNotifyFromHome();
        }
    }
}
