package com.quovantis.musicplayer.updated.ui.views.folders;

import android.content.Context;

import com.quovantis.musicplayer.updated.aynctasks.RefreshMusicAsyncTask;
import com.quovantis.musicplayer.updated.interfaces.ICommonKeys;
import com.quovantis.musicplayer.updated.models.SongPathModel;
import com.quovantis.musicplayer.updated.utility.SharedPrefrence;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by sahil-goel on 23/8/16.
 */
public class FoldersInteractorImp implements IFoldersInteractor,
        IFoldersInteractor.RefreshSongsListListener {

    private IFoldersInteractor.Listener iListener;
    private Realm mRealm;

    public FoldersInteractorImp(IFoldersInteractor.Listener listener) {
        iListener = listener;
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    public void getFoldersList(Context context) {
        if (SharedPrefrence.getSharedPrefrenceBoolean(context, ICommonKeys.FIRST_TIME_OPEN)) {
            SharedPrefrence.saveSharedPrefrenceBoolean(context, ICommonKeys.FIRST_TIME_OPEN, false);
            resyncMusic(context);
        } else {
            onFetchedSongs();
        }
    }

    @Override
    public void resyncMusic(Context context) {
        new RefreshMusicAsyncTask(context, this).execute();
    }

    @Override
    public void onRefreshMusicListUpdateProgress(int value, int total) {
        iListener.onRefreshMusicListProgress(value, total);
    }

    @Override
    public void onRefreshMusicListUpdateFetchedSongs(int value, int total) {
        iListener.onRefreshMusicListFetchedSongs(value, total);
    }

    @Override
    public void onIntitalizeRefreshMusicListDialog() {
        iListener.onIntitalzeRefreshMusicListDialog();
    }

    @Override
    public void onCancelRefreshMusicListDialog() {
        iListener.onCancelRefreshMusicListDialog();
    }

    @Override
    public void onRefreshListSuccessfully() {
        onFetchedSongs();
    }

    @Override
    public void onFetchedSongs() {
        RealmResults<SongPathModel> list = mRealm.where(SongPathModel.class).findAll().sort("mSongDirectory", Sort.ASCENDING);
        ArrayList<SongPathModel> arrayList = new ArrayList<>();
        arrayList.addAll(list);
        iListener.onUpdateFoldersList(arrayList);
    }
}
