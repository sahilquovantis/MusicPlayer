package com.quovantis.musicplayer.updated.ui.views.allsongs;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.dialogs.QueueOptionsDialog;
import com.quovantis.musicplayer.updated.helper.MusicHelper;
import com.quovantis.musicplayer.updated.interfaces.ICommonKeys;
import com.quovantis.musicplayer.updated.interfaces.IHomeAndMusicCommunicator;
import com.quovantis.musicplayer.updated.interfaces.IMusicListClickListener;
import com.quovantis.musicplayer.updated.interfaces.IQueueOptionsDialog;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.models.SongPathModel;
import com.quovantis.musicplayer.updated.ui.views.createplaylist.CreatePlaylistActivity;
import com.quovantis.musicplayer.updated.ui.views.songslist.SongsListAdapter;
import com.quovantis.musicplayer.updated.utility.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllSongsFragment extends Fragment implements IMusicListClickListener,
        IQueueOptionsDialog.onSongClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.rv_songs_list)
    RecyclerView mSongsListRV;
    @BindView(R.id.pb_progress_bar)
    ProgressBar mProgressBar;
    private AllSongsAdapter mAdapter;
    private IHomeAndMusicCommunicator iHomeAndMusicCommunicator;
    private ArrayList<SongDetailsModel> mSongsList = new ArrayList<>();
    private IAllSongsPresenter iAllSongsPresenter;

    public AllSongsFragment() {
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
        getLoaderManager().initLoader(2, null, this);
    }


    private void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }


    private void hideProgress() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onMusicListClick(int pos) {
        if (mSongsList != null) {
            boolean isListAdded = MusicHelper.getInstance().setCurrentPlaylist(mSongsList, pos);
            if (isListAdded) {
                iHomeAndMusicCommunicator.onMusicListClick();
            }
        }
    }

    @Override
    public void onActionOverFlowClick(SongDetailsModel model) {
        QueueOptionsDialog.showDialog(getActivity(), model, this);
    }

    @Override
    public void onClick(SongDetailsModel model, boolean isClearQueue, boolean isPlaythisSong) {
        iHomeAndMusicCommunicator.onOptionsDialogClick(model, isClearQueue, isPlaythisSong);
    }

    @Override
    public void onAddToPlaylist(SongDetailsModel model) {
        Bundle bundle = new Bundle();
        bundle.putString("Id", model.getSongPath());
        Intent intent = new Intent(getActivity(), CreatePlaylistActivity.class);
        intent.setAction(Utils.SONG_LIST);
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
        if (data != null) {
            mSongsListRV.setLayoutManager(new LinearLayoutManager(getActivity()));
            mAdapter = new AllSongsAdapter(getActivity(), this, data);
            mSongsListRV.setAdapter(mAdapter);
        }
        hideProgress();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
