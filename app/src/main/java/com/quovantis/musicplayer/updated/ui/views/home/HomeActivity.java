package com.quovantis.musicplayer.updated.ui.views.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.controller.AppActionController;
import com.quovantis.musicplayer.updated.dialogs.CustomProgressDialog;
import com.quovantis.musicplayer.updated.helper.DepthPageTransformerHelper;
import com.quovantis.musicplayer.updated.helper.MusicHelper;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.models.SongPathModel;
import com.quovantis.musicplayer.updated.models.UserPlaylistModel;
import com.quovantis.musicplayer.updated.services.MusicService;
import com.quovantis.musicplayer.updated.ui.views.allsongs.AllSongsFragment;
import com.quovantis.musicplayer.updated.ui.views.folders.FoldersFragment;
import com.quovantis.musicplayer.updated.ui.views.music.MusicBaseActivity;
import com.quovantis.musicplayer.updated.ui.views.music.MusicPresenterImp;
import com.quovantis.musicplayer.updated.ui.views.playlists.PlaylistFragment;
import com.quovantis.musicplayer.updated.ui.views.settings.SettingsActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends MusicBaseActivity implements FoldersFragment.IHomeAndFolderCommunicator,
        AllSongsFragment.IHomeAndMusicCommunicator, PlaylistFragment.IHomeAndPlaylistCommunicator, IHomeView {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.tabs)
    TabLayout mTabs;
    private CustomProgressDialog mProgressDialog;
    private HomeAdapter mHomeAdapter;
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

    @Override
    protected void updateCurrentSongPlayingStatus(int state) {

    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Music");
    }

    private void initPresenter() {
        iHomePresenter = new HomePresenterImp(this);
        iMusicPresenter = new MusicPresenterImp(this, this);
    }

    private void initViewPager() {
        mViewPager.setPageTransformer(true, new DepthPageTransformerHelper());
        mHomeAdapter = new HomeAdapter(getSupportFragmentManager());
        mHomeAdapter.addFragments(new FoldersFragment(), "Folders");
        mHomeAdapter.addFragments(new AllSongsFragment(), "All Tracks");
        mHomeAdapter.addFragments(new PlaylistFragment(), "Playlist");
        mViewPager.setAdapter(mHomeAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mTabs.setupWithViewPager(mViewPager);
    }

    @Override
    public void onOptionsDialogClickFromFolders(SongPathModel model, final boolean isClearQueue, final boolean isPlaythisSong) {
        iHomePresenter.getSongsListFromFolder(model.getPath(), isClearQueue, isPlaythisSong, this, this);
    }

    @Override
    public void cancelDialog() {
        super.cancelDialog();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
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
        int id = item.getItemId();
        if (id == R.id.settings) {
            new AppActionController.Builder(this)
                    .from(this)
                    .setTargetActivity(SettingsActivity.class)
                    .build()
                    .execute();
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
    public void onOptionsDialogClickFromAllTracks(SongDetailsModel model, boolean isClearQueue, boolean isPlaythisSong) {
        if (iMusicPresenter != null)
            iMusicPresenter.addSongToPlaylist(model, isClearQueue, isPlaythisSong);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        PlaylistFragment playistFragment = (PlaylistFragment) mHomeAdapter.getFirstItem(2);
        if (playistFragment != null) {
            playistFragment.onNotifyFromHome();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showProgressDialog() {
        mProgressDialog = new CustomProgressDialog(this);
        mProgressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @Override
    public void onSuccessGettingSongsListForAddingToQueue(List<SongDetailsModel> list,
                                                          boolean isClearQueue, final boolean isPlaythisSong) {
        if (!list.isEmpty()) {
            MusicHelper.getInstance().addSongToPlaylist(list, isClearQueue);
            if (isPlaythisSong) {
                iMusicPresenter.playSong();
            }
            Toast.makeText(this, "Queue Updated", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_MENU) {
            return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    @Override
    public void onOptionsDialogClickFromPlaylist(UserPlaylistModel model, boolean isClearQueue, boolean isPlaythisSong) {
        if (model != null) {
            List<SongDetailsModel> list = model.getPlaylist();
            if (list != null && !list.isEmpty() && list.size() > 0) {
                MusicHelper.getInstance().addSongToPlaylist(list, isClearQueue);
                if (isPlaythisSong) {
                    iMusicPresenter.playSong();
                }
                Toast.makeText(this, "Successfully Added", Toast.LENGTH_LONG).show();
            }
        }
    }
}