package com.quovantis.musicplayer.updated.songslist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.interfaces.ICommonKeys;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SongsListActivity extends AppCompatActivity implements ISongsView {

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
        iSongsPresenter = new SongsPresenter(this);
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
        mAdapter = new SongsListAdapter(this, mSongList);
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
}
