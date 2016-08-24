package com.quovantis.musicplayer.updated.folders;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.helper.DialogHelper;
import com.quovantis.musicplayer.updated.helper.SongOptionsDialog;
import com.quovantis.musicplayer.updated.interfaces.ICommonKeys;
import com.quovantis.musicplayer.updated.interfaces.IFolderClickListener;
import com.quovantis.musicplayer.updated.models.SongPathModel;
import com.quovantis.musicplayer.updated.songslist.SongsListActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FoldersActivity extends AppCompatActivity implements IFolderView, IFolderClickListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rv_folders_list)
    RecyclerView mFoldersListRV;
    @BindView(R.id.pb_progress_bar)
    ProgressBar mProgressBar;
    private RecyclerView.Adapter mAdapter;
    private IFoldersPresenter iFoldersPresenter;
    private ArrayList<SongPathModel> mFoldersList;
    private DialogHelper mDialogHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folders);
        ButterKnife.bind(this);
        mFoldersList = new ArrayList<>();
        initRecyclerView();
        iFoldersPresenter = new FoldersPresenterImp(this);
        iFoldersPresenter.updateUI(this);
    }

    private void initRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(FoldersActivity.this, 2);
        mFoldersListRV.setLayoutManager(layoutManager);
        mAdapter = new FoldersAdapter(FoldersActivity.this, mFoldersList, this);
        mFoldersListRV.setAdapter(mAdapter);
    }

    @Override
    public void onUpdateFoldersList(ArrayList<SongPathModel> foldersList) {
        mFoldersList.clear();
        mFoldersList.addAll(foldersList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showEmptyMessage() {

    }

    @Override
    public void updateRefreshListProgress(int size, int value) {
        mDialogHelper.updateProgress(value, size);
    }

    @Override
    public void updateRefreshListFetchedFolders(int size, int value) {
        mDialogHelper.updateFetchedSongs(value, size);
    }

    @Override
    public void cancelRefreshListDialog() {
        mDialogHelper.cancelProgressDialog();
        mDialogHelper = null;
    }

    @Override
    public void initializeRefreshListDialog() {
        mDialogHelper = new DialogHelper(FoldersActivity.this);
        mDialogHelper.showProgressDialog();
    }

    @Override
    public void onFoldersLongPress() {
        SongOptionsDialog dialog = new SongOptionsDialog(FoldersActivity.this);
        dialog.showDialog();
    }

    @Override
    public void onFoldersSinglePress(long id, String directoryName) {
        Bundle bundle = new Bundle();
        bundle.putLong(ICommonKeys.FOLDER_ID_KEY,id);
        bundle.putString(ICommonKeys.DIRECTORY_NAME_KEY,directoryName);
        Intent intent = new Intent(FoldersActivity.this, SongsListActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
