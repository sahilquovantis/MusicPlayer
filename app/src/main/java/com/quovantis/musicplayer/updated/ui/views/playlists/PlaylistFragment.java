package com.quovantis.musicplayer.updated.ui.views.playlists;


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
import android.widget.TextView;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.constants.AppKeys;
import com.quovantis.musicplayer.updated.interfaces.IPlaylistClickListener;
import com.quovantis.musicplayer.updated.models.UserPlaylistModel;
import com.quovantis.musicplayer.updated.ui.views.songslist.SongsListActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.SEARCH_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistFragment extends Fragment implements IPlaylistView,
        IPlaylistClickListener, SearchView.OnQueryTextListener {

    @BindView(R.id.tv_no_playlist)
    TextView mEmptyPlaylistTV;
    @BindView(R.id.rv_playlists_list)
    RecyclerView mPlaylistsListRV;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<UserPlaylistModel> mPlaylistsList;
    private ArrayList<UserPlaylistModel> mOriginalList = new ArrayList<>();
    private IPlaylistPresenter iPlaylistPresenter;
    private SearchView mSearchView;

    public PlaylistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playist, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPlaylistsList = new ArrayList<>();
        initRecyclerView();
        initPresenters();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.folders_fragments_menu, menu);
        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setQueryHint("Search playlist ...");
    }

    private void initRecyclerView() {
        mPlaylistsListRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new PlaylistAdapter(mPlaylistsList, getActivity(), this);
        mPlaylistsListRV.setAdapter(mAdapter);
    }

    private void initPresenters() {
        iPlaylistPresenter = new PlaylistPresenterImp(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (iPlaylistPresenter != null) {
            iPlaylistPresenter.updateUI();
        }
    }

    @Override
    public void onUpdateUI(List<UserPlaylistModel> list) {
        if (mOriginalList.isEmpty()) {
            onNoPlaylist();
            return;
        }
        mPlaylistsListRV.setVisibility(View.VISIBLE);
        mPlaylistsList.clear();
        mPlaylistsList.addAll(list);
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
    public void onNoPlaylist() {
        mPlaylistsListRV.setVisibility(View.GONE);
        mEmptyPlaylistTV.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        iPlaylistPresenter.onDestroy();
        iPlaylistPresenter = null;
        super.onDestroyView();
    }

    @Override
    public void onClick(long id, String name) {
        Bundle bundle = new Bundle();
        bundle.putLong(AppKeys.FOLDER_ID_KEY, id);
        bundle.putString(AppKeys.DIRECTORY_NAME_KEY, name);
        Intent intent = new Intent(getActivity(), SongsListActivity.class);
        intent.setAction(AppKeys.PLAYLIST_ACTION);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void onNotifyFromHome() {
        if (iPlaylistPresenter != null)
            iPlaylistPresenter.updateUI();
    }

    @Override
    public void onFetchingPlaylist(List<UserPlaylistModel> playlistList) {
        mOriginalList.clear();
        mOriginalList.addAll(playlistList);
        mPlaylistsList.clear();
        mPlaylistsList.addAll(mOriginalList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        iPlaylistPresenter.filterResults(mOriginalList, query);
        mSearchView.clearFocus();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        iPlaylistPresenter.filterResults(mOriginalList, newText);
        return true;
    }
}
