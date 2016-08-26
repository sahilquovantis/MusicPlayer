package com.quovantis.musicplayer.updated.ui.views.songslist;

import android.graphics.Bitmap;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.dialogs.QueueOptionsDialog;
import com.quovantis.musicplayer.updated.helper.MusicHelper;
import com.quovantis.musicplayer.updated.interfaces.ICommonKeys;
import com.quovantis.musicplayer.updated.interfaces.IMusicListClickListener;
import com.quovantis.musicplayer.updated.interfaces.IQueueOptionsDialog;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.ui.views.folders.FoldersActivity;
import com.quovantis.musicplayer.updated.ui.views.music.IMusicPresenter;
import com.quovantis.musicplayer.updated.ui.views.music.IMusicView;
import com.quovantis.musicplayer.updated.ui.views.music.MusicPresenterImp;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SongsListActivity extends AppCompatActivity implements ISongsView,
        IMusicListClickListener, IMusicView , IQueueOptionsDialog{

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
    @BindView(R.id.rv_songs_list)
    RecyclerView mSongsListRV;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<SongDetailsModel> mSongList;
    private ISongsPresenter iSongsPresenter;
    private IMusicPresenter iMusicPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs_list);
        ButterKnife.bind(this);
        mSongList = new ArrayList<>();
        initToolbar();
        initRecyclerView();
        iSongsPresenter = new SongsPresenter(this);
        iMusicPresenter = new MusicPresenterImp(this, SongsListActivity.this);
        iMusicPresenter.bindService();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            long id = bundle.getLong(ICommonKeys.FOLDER_ID_KEY, -1);
            String directory = bundle.getString(ICommonKeys.DIRECTORY_NAME_KEY);
            getSupportActionBar().setTitle(directory);
            iSongsPresenter.updateUI(id);
        }
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initRecyclerView() {
        mSongsListRV.setLayoutManager(new LinearLayoutManager(SongsListActivity.this));
        mAdapter = new SongsListAdapter(this, this, mSongList);
        mSongsListRV.setAdapter(mAdapter);
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
        MusicHelper.getInstance().addSongToPlaylist(model, false, true);
        iMusicPresenter.playSong(model.getSongID());
    }

    @Override
    public void onActionOverFlowClick(SongDetailsModel model) {
        QueueOptionsDialog.showDialog(SongsListActivity.this, model, this);
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
    public void onStopService() {

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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClearAndPlay(String id) {
        iMusicPresenter.playSong(id);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
