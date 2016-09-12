package com.quovantis.musicplayer.updated.ui.views.search;

import android.content.Intent;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.dialogs.QueueOptionsDialog;
import com.quovantis.musicplayer.updated.helper.MusicHelper;
import com.quovantis.musicplayer.updated.interfaces.IMusicListClickListener;
import com.quovantis.musicplayer.updated.interfaces.IQueueOptionsDialog;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.ui.views.createplaylist.CreatePlaylistActivity;
import com.quovantis.musicplayer.updated.ui.views.music.IMusicPresenter;
import com.quovantis.musicplayer.updated.ui.views.music.MusicBaseActivity;
import com.quovantis.musicplayer.updated.ui.views.music.MusicPresenterImp;
import com.quovantis.musicplayer.updated.ui.views.songslist.SongsListAdapter;
import com.quovantis.musicplayer.updated.utility.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends MusicBaseActivity implements TextWatcher,
        ISearchView, IQueueOptionsDialog.onSongClickListener {


    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rv_search_list)
    RecyclerView mSearchListRV;
    @BindView(R.id.pb_progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.et_search_query)
    EditText mSearchQueryET;
    private IMusicPresenter iMusicPresenter;
    private ISearchPresenter iSearchPresenter;
    private ArrayList<SongDetailsModel> mOriginalList;
    private ArrayList<SongDetailsModel> mFilteredList;
    private SongsListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        mOriginalList = new ArrayList<>();
        mFilteredList = new ArrayList<>();
        initToolbar();
        initRecyclerView();
        initPresenters();
        mSearchQueryET.addTextChangedListener(this);
    }

    private void initRecyclerView() {
        mSearchListRV.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SongsListAdapter(this, iMusicListClickListener, mFilteredList);
        mSearchListRV.setAdapter(mAdapter);
    }

    private void initPresenters() {
        iSearchPresenter = new SearchPresenterImp(this);
        iMusicPresenter = new MusicPresenterImp(this, SearchActivity.this);
        iMusicPresenter.bindService();
        iSearchPresenter.fetchSongsList();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(" ");
        }
    }

    private IMusicListClickListener iMusicListClickListener = new IMusicListClickListener() {
        @Override
        public void onMusicListClick(int pos) {
            if (mFilteredList != null) {
                boolean isListAdded = MusicHelper.getInstance().setCurrentPlaylist(mFilteredList, pos);
                if (isListAdded) {
                    if(iMusicPresenter != null)
                    iMusicPresenter.playSong();
                }
            }
        }

        @Override
        public void onActionOverFlowClick(SongDetailsModel model) {
            QueueOptionsDialog.showDialog(SearchActivity.this, model, SearchActivity.this);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iMusicPresenter.onDestroy();
        iMusicPresenter = null;
    }

    @OnClick(R.id.iv_back)
    public void onBack() {
        onBackPressed();
        finish();
    }

    @OnClick(R.id.iv_remove_search)
    public void clearSearchQuery() {
        mSearchQueryET.setText("");
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        iSearchPresenter.filterResults(mOriginalList, charSequence.toString());
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onShowProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHideProgress() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onUpdateUI(List<SongDetailsModel> list, String query) {
        mFilteredList.clear();
        mFilteredList.addAll(list);
        mAdapter.setQuery(query);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFetchingAllSongList(List<SongDetailsModel> list) {
        mOriginalList.clear();
        mOriginalList.addAll(list);
        mFilteredList.clear();
        mFilteredList.addAll(list);
        mAdapter.notifyDataSetChanged();
        mSearchQueryET.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(SongDetailsModel model, boolean isClearQueue, boolean isPlaythisSong) {
        if(iMusicPresenter != null)
        iMusicPresenter.addSongToPlaylist(model, isClearQueue, isPlaythisSong);
    }

    @Override
    public void onAddToPlaylist(SongDetailsModel model) {
        Bundle bundle = new Bundle();
        bundle.putString("Id", model.getSongID());
        Intent intent = new Intent(this, CreatePlaylistActivity.class);
        intent.setAction(Utils.SONG_LIST);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
