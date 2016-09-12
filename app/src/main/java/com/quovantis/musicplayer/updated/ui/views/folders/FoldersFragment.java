package com.quovantis.musicplayer.updated.ui.views.folders;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.dialogs.ProgresDialog;
import com.quovantis.musicplayer.updated.dialogs.QueueOptionsDialog;
import com.quovantis.musicplayer.updated.dialogs.RefreshListDialog;
import com.quovantis.musicplayer.updated.interfaces.ICommonKeys;
import com.quovantis.musicplayer.updated.interfaces.IFolderClickListener;
import com.quovantis.musicplayer.updated.interfaces.IHomeAndFolderCommunicator;
import com.quovantis.musicplayer.updated.interfaces.IQueueOptionsDialog;
import com.quovantis.musicplayer.updated.models.SongPathModel;
import com.quovantis.musicplayer.updated.ui.views.createplaylist.CreatePlaylistActivity;
import com.quovantis.musicplayer.updated.ui.views.folders.FoldersAdapter;
import com.quovantis.musicplayer.updated.ui.views.folders.FoldersPresenterImp;
import com.quovantis.musicplayer.updated.ui.views.folders.IFolderView;
import com.quovantis.musicplayer.updated.ui.views.folders.IFoldersPresenter;
import com.quovantis.musicplayer.updated.ui.views.music.MusicPresenterImp;
import com.quovantis.musicplayer.updated.ui.views.songslist.SongsListActivity;
import com.quovantis.musicplayer.updated.utility.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class FoldersFragment extends Fragment implements IFolderView, IFolderClickListener,
        IQueueOptionsDialog.onFolderClickListener {

    @BindView(R.id.rv_folders_list)
    RecyclerView mFoldersListRV;
    @BindView(R.id.pb_progress_bar)
    ProgressBar mProgressBar;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<SongPathModel> mFoldersList = new ArrayList<>();
    private IFoldersPresenter iFoldersPresenter;
    private IHomeAndFolderCommunicator iHomeAndFolderCommunicator;
    private RefreshListDialog mRefreshListDialog;

    public FoldersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_folders, container, false);
        ButterKnife.bind(this, view);
        Log.d("Training", "On Create View Folders");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iHomeAndFolderCommunicator = (IHomeAndFolderCommunicator) getActivity();
        initRecyclerView();
        initPresenters();
    }

    private void initPresenters() {
        iFoldersPresenter = new FoldersPresenterImp(this);
        iFoldersPresenter.updateUI(getActivity());
    }

    private void initRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        mFoldersListRV.setLayoutManager(layoutManager);
        mAdapter = new FoldersAdapter(getActivity(), mFoldersList, this);
        mFoldersListRV.setAdapter(mAdapter);
    }

    /**
     * Update UI and show Folders List
     *
     * @param foldersList List of Folders
     */
    @Override
    public void onUpdateFoldersList(List<SongPathModel> foldersList) {
        Log.d("Training", "Folders Update UI");
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

    @Override
    public void onDestroyView() {
        Log.d("Training", "On Destroy View Called Folders");
        iFoldersPresenter.onDestroy();
        iFoldersPresenter = null;
        super.onDestroyView();
    }

    @Override
    public void onFoldersLongPress(SongPathModel songPathModel) {
        QueueOptionsDialog.showDialog(getActivity(), songPathModel, this);
    }

    @Override
    public void onFoldersSinglePress(long id, String directoryName) {
        Bundle bundle = new Bundle();
        bundle.putLong(ICommonKeys.FOLDER_ID_KEY, id);
        bundle.putString(ICommonKeys.DIRECTORY_NAME_KEY, directoryName);
        Intent intent = new Intent(getActivity(), SongsListActivity.class);
        intent.setAction(ICommonKeys.FOLDERS_ACTION);
        intent.putExtras(bundle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void onClick(SongPathModel model, boolean isClearQueue, boolean isPlaythisSong) {
        iHomeAndFolderCommunicator.onOptionsDialogClick(model, isClearQueue, isPlaythisSong);
    }

    @Override
    public void onAddToPlaylist(SongPathModel model) {
        Bundle bundle = new Bundle();
        bundle.putString("Id", String.valueOf(model.getId()));
        Intent intent = new Intent(getActivity(), CreatePlaylistActivity.class);
        intent.setAction(Utils.FOLDER_LIST);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void getListOnNotifyFromHome() {
        Log.d("Training", "Folders Notify Called");
        if (iFoldersPresenter != null)
            iFoldersPresenter.updateUI(getActivity());
    }
}
