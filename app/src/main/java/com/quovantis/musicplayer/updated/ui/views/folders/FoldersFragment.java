package com.quovantis.musicplayer.updated.ui.views.folders;


import android.app.SearchManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.constants.AppKeys;
import com.quovantis.musicplayer.updated.controller.AppActionController;
import com.quovantis.musicplayer.updated.dialogs.QueueOptionsDialog;
import com.quovantis.musicplayer.updated.interfaces.IQueueOptionsDialog;
import com.quovantis.musicplayer.updated.models.SongPathModel;
import com.quovantis.musicplayer.updated.ui.views.createplaylist.CreatePlaylistActivity;
import com.quovantis.musicplayer.updated.ui.views.songslist.SongsListActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.SEARCH_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FoldersFragment extends Fragment implements IFolderView, FoldersAdapter.IFolderClickListener,
        IQueueOptionsDialog.onFolderClickListener, SearchView.OnQueryTextListener {

    @BindView(R.id.rv_folders_list)
    RecyclerView mFoldersListRV;
    @BindView(R.id.pb_progress_bar)
    ProgressBar mProgressBar;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<SongPathModel> mOriginalList = new ArrayList<>();
    private ArrayList<SongPathModel> mFoldersList = new ArrayList<>();
    private IHomeAndFolderCommunicator iHomeAndFolderCommunicator;
    private IFoldersPresenter iFoldersPresenter;
    private SearchView mSearchView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folders, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iHomeAndFolderCommunicator = (IHomeAndFolderCommunicator) getActivity();
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
        mSearchView.setQueryHint("Search folders ...");
    }

    private void initPresenter() {
        iFoldersPresenter = new FoldersPresenterImp(this);
        iFoldersPresenter.updateUI(getActivity(), getActivity());
    }

    private void initRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        mFoldersListRV.setLayoutManager(layoutManager);
        mAdapter = new FoldersAdapter(getActivity(), mFoldersList, this);
        mFoldersListRV.setAdapter(mAdapter);
    }

    @Override
    public void onFetchingAllFoldersList(List<SongPathModel> foldersList) {
        mOriginalList.clear();
        mOriginalList.addAll(foldersList);
        mFoldersList.clear();
        mFoldersList.addAll(mOriginalList);
        mAdapter.notifyDataSetChanged();
        hideProgress();
    }

    /**
     * Update UI and show Folders List
     * <p/>@param foldersList List of Folders
     */
    @Override
    public void onUpdateFoldersList(List<SongPathModel> foldersList) {
        Log.d("Training", "Folders Update UI");
        mFoldersList.clear();
        mFoldersList.addAll(foldersList);
        mAdapter.notifyDataSetChanged();
        hideProgress();
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

    @Override
    public void onFoldersLongPress(SongPathModel songPathModel) {
        QueueOptionsDialog dialog = new QueueOptionsDialog(getActivity(), songPathModel, this);
        dialog.show();
    }

    @Override
    public void onFoldersSinglePress(String path, String directoryName) {
        Bundle bundle = new Bundle();
        bundle.putString(AppKeys.FOLDER_ID_KEY, path);
        bundle.putString(AppKeys.DIRECTORY_NAME_KEY, directoryName);
        new AppActionController.Builder(getActivity())
                .from(getActivity())
                .setBundle(bundle)
                .setTargetActivity(SongsListActivity.class)
                .setIntentAction(AppKeys.FOLDERS_ACTION)
                .build()
                .execute();
    }

    @Override
    public void onClickFromFolderOptionsDialog(SongPathModel model, boolean isClearQueue, boolean isPlaythisSong) {
        iHomeAndFolderCommunicator.onOptionsDialogClickFromFolders(model, isClearQueue, isPlaythisSong);
    }

    @Override
    public void onAddToPlaylist(SongPathModel model) {
        Bundle bundle = new Bundle();
        bundle.putString(AppKeys.CREATE_PLAYLIST_INTENT_PATH, model.getPath());
        new AppActionController.Builder(getActivity())
                .from(getActivity())
                .setBundle(bundle)
                .setIntentAction(AppKeys.FOLDER_LIST)
                .setTargetActivityForResult(CreatePlaylistActivity.class, AppKeys.UPDATE_PLAYLIST_RESULT_CODE)
                .build()
                .execute();
    }

    @Override
    public void onDestroyView() {
        iFoldersPresenter.onDestroy();
        iFoldersPresenter = null;
        super.onDestroyView();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        iFoldersPresenter.filterResults(mOriginalList, query);
        mSearchView.clearFocus();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        iFoldersPresenter.filterResults(mOriginalList, newText);
        return true;
    }

    public interface IHomeAndFolderCommunicator {
        void onOptionsDialogClickFromFolders(SongPathModel model, boolean isClearQueue, boolean isPlaythisSong);
    }
}