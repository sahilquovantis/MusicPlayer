package com.quovantis.musicplayer.updated.ui.views.allsongs;


import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.constants.AppKeys;
import com.quovantis.musicplayer.updated.controller.AppActionController;
import com.quovantis.musicplayer.updated.dialogs.QueueOptionsDialog;
import com.quovantis.musicplayer.updated.helper.MusicHelper;
import com.quovantis.musicplayer.updated.interfaces.IHomeAndMusicCommunicator;
import com.quovantis.musicplayer.updated.interfaces.IMusicListClickListener;
import com.quovantis.musicplayer.updated.interfaces.IQueueOptionsDialog;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.ui.views.createplaylist.CreatePlaylistActivity;
import com.quovantis.musicplayer.updated.ui.views.songslist.SongsListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.SEARCH_SERVICE;

public class AllSongsFragment extends Fragment implements IMusicListClickListener,
        IQueueOptionsDialog.onSongClickListener, IAllSongsView, SearchView.OnQueryTextListener {

    @BindView(R.id.rv_songs_list)
    RecyclerView mSongsListRV;
    @BindView(R.id.pb_progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.fast_scroll)
    FastScroller mFastScroll;
    private SongsListAdapter mAdapter;
    private IHomeAndMusicCommunicator iHomeAndMusicCommunicator;
    private ArrayList<SongDetailsModel> mSongsList = new ArrayList<>();
    private ArrayList<SongDetailsModel> mOriginalList = new ArrayList<>();
    private IAllSongsPresenter iAllSongsPresenter;
    private SearchView mSearchView;

    public AllSongsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all_songs, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iHomeAndMusicCommunicator = (IHomeAndMusicCommunicator) getActivity();
        initRecyclerView();
        initPresenter();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.folders_fragments_menu, menu);
        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setQueryHint("Search songs ...");

    }

    private void initPresenter() {
        iAllSongsPresenter = new AllSongsPresenterImp(this);
        iAllSongsPresenter.getSongsList(getActivity(), getActivity());
    }

    private void initRecyclerView() {
        mSongsListRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new SongsListAdapter(getActivity(), this, mSongsList);
        mSongsListRV.setAdapter(mAdapter);
        mFastScroll.setRecyclerView(mSongsListRV);
    }


    @Override
    public void updateUi(List<SongDetailsModel> list) {
        mSongsList.clear();
        mSongsList.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onMusicListClick(int pos) {
        if (mSongsList != null && MusicHelper.getInstance().isSongStartedPlaying()) {
            MusicHelper.getInstance().setIsSongStartedPlaying(false);
            boolean isListAdded = MusicHelper.getInstance().setCurrentPlaylist(mSongsList, pos);
            if (isListAdded) {
                iHomeAndMusicCommunicator.onMusicListClick();
            }
        }
    }

    @Override
    public void onActionOverFlowClick(SongDetailsModel model) {
        QueueOptionsDialog dialog = new QueueOptionsDialog(getActivity(), model, this);
        dialog.show();
    }

    @Override
    public void onClickFromSpecificSongOptionsDialog(SongDetailsModel model, boolean isClearQueue, boolean isPlaythisSong) {
        iHomeAndMusicCommunicator.onOptionsDialogClickFromAllTracks(model, isClearQueue, isPlaythisSong);
    }

    @Override
    public void onAddToPlaylist(SongDetailsModel model) {
        Bundle bundle = new Bundle();
        bundle.putString(AppKeys.CREATE_PLAYLIST_INTENT_PATH, model.getSongPath());
        new AppActionController.Builder(getActivity())
                .from(getActivity())
                .setBundle(bundle)
                .setIntentAction(AppKeys.SONG_LIST)
                .setTargetActivityForResult(CreatePlaylistActivity.class, AppKeys.UPDATE_PLAYLIST_RESULT_CODE)
                .build()
                .execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        iAllSongsPresenter.onDestroy();
        iAllSongsPresenter = null;
    }

    @Override
    public void onFetchingAllSongsList(List<SongDetailsModel> songsList) {
        mOriginalList.clear();
        mOriginalList.addAll(songsList);
        mSongsList.clear();
        mSongsList.addAll(mOriginalList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        iAllSongsPresenter.filterResults(mOriginalList, query);
        mSearchView.clearFocus();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        iAllSongsPresenter.filterResults(mOriginalList, newText);
        return true;
    }
}
