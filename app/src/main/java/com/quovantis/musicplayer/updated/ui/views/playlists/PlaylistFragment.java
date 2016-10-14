package com.quovantis.musicplayer.updated.ui.views.playlists;


import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.constants.AppKeys;
import com.quovantis.musicplayer.updated.controller.AppActionController;
import com.quovantis.musicplayer.updated.dialogs.CreatePlaylistDialog;
import com.quovantis.musicplayer.updated.dialogs.PlaylistOptionsDialog;
import com.quovantis.musicplayer.updated.dialogs.ProgresDialog;
import com.quovantis.musicplayer.updated.dialogs.QueueOptionsDialog;
import com.quovantis.musicplayer.updated.dialogs.RenamePlaylistDialog;
import com.quovantis.musicplayer.updated.interfaces.IHomeAndPlaylistCommunicator;
import com.quovantis.musicplayer.updated.interfaces.IOnCreatePlaylistDialogListener;
import com.quovantis.musicplayer.updated.interfaces.IPlaylistClickListener;
import com.quovantis.musicplayer.updated.interfaces.IQueueOptionsDialog;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.models.UserPlaylistModel;
import com.quovantis.musicplayer.updated.ui.views.playlistsongslist.PlaylistSongsActivity;
import com.quovantis.musicplayer.updated.ui.views.songslist.SongsListActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

import static android.content.Context.SEARCH_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistFragment extends Fragment implements IPlaylistView,
        IPlaylistClickListener, SearchView.OnQueryTextListener, RenamePlaylistDialog.IRenamePlaylist, IQueueOptionsDialog.onPlaylistClickListener {

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
    private IHomeAndPlaylistCommunicator iHomeAndPlaylistCommunicator;
    private Dialog mProgressDialog;

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
        iHomeAndPlaylistCommunicator = (IHomeAndPlaylistCommunicator) getActivity();
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
        mOriginalList.clear();
        mPlaylistsList.clear();
        mAdapter.notifyDataSetChanged();
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
        new AppActionController.Builder(getActivity())
                .from(getActivity())
                .setBundle(bundle)
                .setIntentAction(AppKeys.PLAYLIST_ACTION)
                .setTargetActivity(PlaylistSongsActivity.class)
                .build()
                .execute();
    }

    @Override
    public void onLongPress(UserPlaylistModel model) {
        PlaylistOptionsDialog dialog = new PlaylistOptionsDialog(getActivity(), model, this);
        dialog.show();
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
        if (!playlistList.isEmpty()) {
            mPlaylistsListRV.setVisibility(View.VISIBLE);
            mEmptyPlaylistTV.setVisibility(View.GONE);
        } else {
            mPlaylistsListRV.setVisibility(View.GONE);
            mEmptyPlaylistTV.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (mOriginalList != null && mOriginalList.size() > 0) {
            iPlaylistPresenter.filterResults(mOriginalList, query);
        } else {
            onNoPlaylist();
        }
        mSearchView.clearFocus();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (mOriginalList != null && mOriginalList.size() > 0) {
            iPlaylistPresenter.filterResults(mOriginalList, newText);
        } else {
            onNoPlaylist();
        }
        return true;
    }

    @Override
    public void onClickFromPlaylistOptionsDialog(UserPlaylistModel model, boolean isClearQueue, boolean isPlaythisSong) {
        iHomeAndPlaylistCommunicator.onOptionsDialogClickFromPlaylist(model, isClearQueue, isPlaythisSong);
    }

    @Override
    public void onDelete(final UserPlaylistModel model) {
        Realm realm = Realm.getDefaultInstance();
        Dialog dialog = ProgresDialog.showProgressDialog(getActivity());
        if (model != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    model.deleteFromRealm();
                    Toast.makeText(getActivity(), "Playlist Deleted", Toast.LENGTH_LONG).show();
                    onNotifyFromHome();
                }
            });
        }
        dialog.cancel();
    }

    @Override
    public void onRename(UserPlaylistModel model) {
        RenamePlaylistDialog dialog = new RenamePlaylistDialog(getActivity(), model, this);
        dialog.show();
        dialog.setPlaylistName(model.getPlaylistName());
        if (dialog.getWindow() != null)
            dialog.getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }

    @Override
    public void onRenamePlaylist(UserPlaylistModel model, String newName) {
        mProgressDialog = ProgresDialog.showProgressDialog(getActivity());
        iPlaylistPresenter.renamePlaylist(model, newName);
    }

    @Override
    public void onSuccessfullyRenaming() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
        onNotifyFromHome();
    }
}
