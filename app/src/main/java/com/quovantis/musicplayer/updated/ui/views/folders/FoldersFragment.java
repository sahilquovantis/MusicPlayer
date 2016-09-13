package com.quovantis.musicplayer.updated.ui.views.folders;


import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
        IQueueOptionsDialog.onFolderClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.rv_folders_list)
    RecyclerView mFoldersListRV;
    @BindView(R.id.pb_progress_bar)
    ProgressBar mProgressBar;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<SongPathModel> mFoldersList = new ArrayList<>();
    private IHomeAndFolderCommunicator iHomeAndFolderCommunicator;

    public FoldersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        getLoaderManager().initLoader(1, null, this);
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
        QueueOptionsDialog.showDialog(getActivity(), songPathModel, this);
    }

    @Override
    public void onFoldersSinglePress(String path, String directoryName) {
        Bundle bundle = new Bundle();
        bundle.putString(ICommonKeys.FOLDER_ID_KEY, path);
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
        bundle.putString("Id", model.getPath());
        Intent intent = new Intent(getActivity(), CreatePlaylistActivity.class);
        intent.setAction(Utils.FOLDER_LIST);
        intent.putExtras(bundle);
        startActivityForResult(intent, ICommonKeys.UPDATE_PLAYLIST_RESULT_CODE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        showProgress();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] columns = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID};
        return new CursorLoader(getActivity(), uri, columns, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ArrayList<SongPathModel> list = new ArrayList<>();
        if (data != null) {
            data.moveToFirst();
            while (!data.isAfterLast()) {
                String songPath = data.getString(1);
                String path = songPath.substring(0, songPath.lastIndexOf("/"));
                SongPathModel model = new SongPathModel();
                model.setAlbumId(data.getLong(4));
                model.setPath(path);
                model.setDirectory(path.substring(path.lastIndexOf("/") + 1));
                if (list.isEmpty() || !list.contains(model)) {
                    list.add(model);
                }
                data.moveToNext();
            }
            onUpdateFoldersList(list);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
