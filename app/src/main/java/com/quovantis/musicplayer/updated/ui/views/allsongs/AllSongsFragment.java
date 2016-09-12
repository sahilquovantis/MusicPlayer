package com.quovantis.musicplayer.updated.ui.views.allsongs;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.quovantis.musicplayer.updated.interfaces.IHomeAndMusicCommunicator;
import com.quovantis.musicplayer.updated.interfaces.IMusicListClickListener;
import com.quovantis.musicplayer.updated.interfaces.IQueueOptionsDialog;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
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
public class AllSongsFragment extends Fragment implements IAllSongsView, IMusicListClickListener,
        IQueueOptionsDialog.onSongClickListener {

    @BindView(R.id.rv_songs_list)
    RecyclerView mSongsListRV;
    @BindView(R.id.pb_progress_bar)
    ProgressBar mProgressBar;
    private SongsListAdapter mAdapter;
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

    private void initRecyclerView() {
        mSongsListRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new SongsListAdapter(getActivity(), this, mSongsList);
        mSongsListRV.setAdapter(mAdapter);
    }

    private void initPresenter() {
        iAllSongsPresenter = new AllSongsPresenterImp(this);
        Log.d("Training","Presenter Called");
        iAllSongsPresenter.getSongsList();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iHomeAndMusicCommunicator = (IHomeAndMusicCommunicator) getActivity();
        initRecyclerView();
        initPresenter();
    }

    @Override
    public void updateUi(List<SongDetailsModel> list) {
        mSongsList.clear();
        mAdapter.setList(list);
        mAdapter.notifyDataSetChanged();
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

    public void onDestroyView() {
        iAllSongsPresenter.onDestroy();
        iAllSongsPresenter = null;
        super.onDestroyView();
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
        bundle.putString("Id", model.getSongID());
        Intent intent = new Intent(getActivity(), CreatePlaylistActivity.class);
        intent.setAction(Utils.SONG_LIST);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void getListOnNotifyFromHome() {
        if (iAllSongsPresenter != null)
            iAllSongsPresenter.getSongsList();
    }
}
