package com.quovantis.musicplayer.updated.ui.views.folders;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.dialogs.ProgresDialog;
import com.quovantis.musicplayer.updated.dialogs.QueueOptionsDialog;
import com.quovantis.musicplayer.updated.dialogs.RefreshListDialog;
import com.quovantis.musicplayer.updated.interfaces.ICommonKeys;
import com.quovantis.musicplayer.updated.interfaces.IFolderClickListener;
import com.quovantis.musicplayer.updated.interfaces.IQueueOptionsDialog;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.models.SongPathModel;
import com.quovantis.musicplayer.updated.services.MusicService;
import com.quovantis.musicplayer.updated.ui.views.createplaylist.CreatePlaylistActivity;
import com.quovantis.musicplayer.updated.ui.views.search.SearchActivity;
import com.quovantis.musicplayer.updated.ui.views.currentplaylist.CurrentPlaylistActivity;
import com.quovantis.musicplayer.updated.ui.views.music.MusicBaseActivity;
import com.quovantis.musicplayer.updated.ui.views.music.MusicPresenterImp;
import com.quovantis.musicplayer.updated.ui.views.playlists.PlaylistsActivity;
import com.quovantis.musicplayer.updated.ui.views.songslist.SongsListActivity;
import com.quovantis.musicplayer.updated.utility.Utils;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

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

    /**
     * Initialize Toolbar
     */
    private void initiToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(" ");
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Initialize Presenters
     */
    private void initPresenters() {
        iFoldersPresenter = new FoldersPresenterImp(this);
        iMusicPresenter = new MusicPresenterImp(this, FoldersActivity.this);
        iFoldersPresenter.updateUI(this);
        iMusicPresenter.bindService();
    }

    /**
     * Initialize RecyclerView
     */
    private void initRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(FoldersActivity.this, 2);
        mFoldersListRV.setLayoutManager(layoutManager);
        mAdapter = new FoldersAdapter(FoldersActivity.this, mFoldersList, this);
        mFoldersListRV.setAdapter(mAdapter);
    }

    /**
     * Update UI and show Folders List
     *
     * @param foldersList List of Folders
     */
    @Override
    public void onUpdateFoldersList(List<SongPathModel> foldersList) {
        mFoldersList.clear();
        mFoldersList.addAll(foldersList);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Show Progress Bar while Getting list of Folders
     */
    @Override
    public void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Hide Progress Bar after Getting list of Folders
     */
    @Override
    public void hideProgress() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showEmptyMessage() {

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
        mRefreshListDialog.cancelProgressDialog();
        mRefreshListDialog = null;
    }

    /**
     * Initialize the Dialog {@link RefreshListDialog}
     */
    @Override
    public void initializeRefreshListDialog() {
        mRefreshListDialog = new RefreshListDialog(FoldersActivity.this);
        mRefreshListDialog.showProgressDialog();
    }

    /**
     * Shows the options Dialog on Long Press {@link QueueOptionsDialog}
     *
     * @param songPathModel Song Model on which event happened
     */
    @Override
    public void onFoldersLongPress(SongPathModel songPathModel) {
        QueueOptionsDialog.showDialog(FoldersActivity.this, songPathModel, this);
    }

    /**
     * On Clicking Folder , it starts activity {@link SongsListActivity} and shows
     * all the songs list in that folder
     *
     * @param id            Id of the folder
     * @param directoryName Folder Name
     */
    @Override
    public void onFoldersSinglePress(long id, String directoryName) {
        Bundle bundle = new Bundle();
        bundle.putLong(ICommonKeys.FOLDER_ID_KEY, id);
        bundle.putString(ICommonKeys.DIRECTORY_NAME_KEY, directoryName);
        Intent intent = new Intent(FoldersActivity.this, SongsListActivity.class);
        intent.setAction(ICommonKeys.FOLDERS_ACTION);
        intent.putExtras(bundle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            startActivity(intent);
        }
    }

    /**
     * On Destroy activity Remove all the presenters
     */
    @Override
    protected void onDestroy() {
        iMusicPresenter.stopService();
        iFoldersPresenter.onDestroy();
        iMusicPresenter.onDestroy();
        iFoldersPresenter = null;
        iMusicPresenter = null;
        super.onDestroy();
    }

    /**
     * Stop the Service
     */
    @Override
    public void onStopService() {
        stopService(new Intent(this, MusicService.class));
    }

    /**
     * Navigation View Item Selection
     *
     * @param item Item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        mDrawerLayout.closeDrawer(mNavigationView);
        int id = item.getItemId();
        if (id == R.id.queue) {
            Intent intent = new Intent(this, CurrentPlaylistActivity.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            } else {
                startActivity(intent);
            }
            return true;
        } else if (id == R.id.playlists) {
            Intent intent = new Intent(this, PlaylistsActivity.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            } else {
                startActivity(intent);
            }
            return true;
        } else if (id == R.id.resync_device) {
            iMusicPresenter.hideMusicLayoutDuringResyncMusic();
            iFoldersPresenter.syncMusic(this);
            return true;
        }
        return false;
    }

    /**
     * On Click Event on Options Dialog {@link QueueOptionsDialog}
     *
     * @param model          Folders Model
     * @param isClearQueue   Whether clear current queue or not. If not all songs are added at the end of the queue.
     * @param isPlaythisSong Whether Play this song or not.
     */
    @Override
    public void onClick(SongPathModel model, boolean isClearQueue, boolean isPlaythisSong) {
        mDialog = ProgresDialog.showProgressDialog(this);
        iMusicPresenter.addSongToPlaylist(model.getId(), isClearQueue, isPlaythisSong);
    }

    /**
     * Cancels the Dialog {@link QueueOptionsDialog}
     */
    @Override
    public void cancelDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    @OnClick(R.id.iv_search_view)
    public void onSearchCick() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onAddToPlaylist(SongPathModel model) {
        Bundle bundle = new Bundle();
        bundle.putString("Id", String.valueOf(model.getId()));
        Intent intent = new Intent(FoldersActivity.this, CreatePlaylistActivity.class);
        intent.setAction(Utils.FOLDER_LIST);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
