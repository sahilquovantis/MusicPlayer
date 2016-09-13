package com.quovantis.musicplayer.updated.ui.views.home;

import android.app.Dialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.dialogs.ProgresDialog;
import com.quovantis.musicplayer.updated.dialogs.RefreshListDialog;
import com.quovantis.musicplayer.updated.helper.MusicHelper;
import com.quovantis.musicplayer.updated.interfaces.ICommonKeys;
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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

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
        // iHomePresenter.firstTimeSync(this);
    }

    private void initViewPager() {
        homeAdapter = new HomeAdapter(getSupportFragmentManager());
        homeAdapter.addFragments(new FoldersFragment(), "Folders");
        homeAdapter.addFragments(new AllSongsFragment(), "All Tracks");
        homeAdapter.addFragments(new PlayistFragment(), "Playlists");
        mViewPager.setAdapter(homeAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mTabs.setupWithViewPager(mViewPager);
    }

    @Override
    public void onOptionsDialogClick(SongPathModel model, final boolean isClearQueue, final boolean isPlaythisSong) {
        mDialog = ProgresDialog.showProgressDialog(this);
        final String path = model.getPath();
        getLoaderManager().initLoader(4, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] columns = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM_ID};
                return new CursorLoader(HomeActivity.this, uri, columns, MediaStore.Audio.Media.DATA + " LIKE ?",
                        new String[]{path + "%"}, null);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor mCursor) {
                ArrayList<SongDetailsModel> mSongList = new ArrayList<SongDetailsModel>();
                if (mCursor != null) {
                    mCursor.moveToFirst();
                    mSongList.clear();
                    while (!mCursor.isAfterLast()) {
                        SongDetailsModel model = new SongDetailsModel();
                        final String title = mCursor.getString(2);
                        final String id = mCursor.getString(0);
                        final String artist = mCursor.getString(3);
                        final String path = mCursor.getString(1);
                        long albumId = mCursor.getLong(4);
                        model.setSongTitle(title);
                        model.setSongArtist(artist);
                        model.setAlbumId(albumId);
                        model.setSongPath(path);
                        model.setSongID(id);
                        mSongList.add(model);
                        mCursor.moveToNext();
                    }
                }
                if (!mSongList.isEmpty()) {
                    MusicHelper.getInstance().addSongToPlaylist(mSongList, isClearQueue);
                    if (isPlaythisSong) {
                        iMusicPresenter.playSong();
                    }
                }
                if (mDialog != null)
                    mDialog.dismiss();
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        });
        // iMusicPresenter.addSongToPlaylist(model.getId(), isClearQueue, isPlaythisSong);
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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        PlayistFragment playistFragment = (PlayistFragment) homeAdapter.getFirstItem(2);
        if (playistFragment != null) {
            playistFragment.onNotifyFromHome();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
