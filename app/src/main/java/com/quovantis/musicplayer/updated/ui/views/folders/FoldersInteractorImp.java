package com.quovantis.musicplayer.updated.ui.views.folders;

import android.content.Context;

import com.quovantis.musicplayer.updated.aynctasks.RefreshMusicAsyncTask;
import com.quovantis.musicplayer.updated.dialogs.RefreshListDialog;
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

    /**
     * Get the Folders list. It checks whether app is opened first time or not.
     * If this is first time. Scans the storage.
     * If this is not first time, load the music from Database.
     * @param context
     */
    @Override
    public void getFoldersList(Context context) {
        if (SharedPrefrence.getSharedPrefrenceBoolean(context, ICommonKeys.FIRST_TIME_OPEN)) {
            SharedPrefrence.saveSharedPrefrenceBoolean(context, ICommonKeys.FIRST_TIME_OPEN, false);
            resyncMusic(context);
        } else {
            onFetchedSongs();
        }
    }

    /**
     * Start Async Task , which fetches songs and stores them in Database
     * {@link RefreshMusicAsyncTask}
     * @param context
     */
    @Override
    public void resyncMusic(Context context) {
        new RefreshMusicAsyncTask(context, this).execute();
    }

    /**
     * Send Callback To Presenter for updating Progress {@link FoldersPresenterImp}
     * @param total Total Number of Songs
     * @param value Number of Fetched Songs
     */
    @Override
    public void onRefreshMusicListUpdateProgress(int value, int total) {
        iListener.onRefreshMusicListProgress(value, total);
    }

    /**
     * Send Callback To Presenter for updating Progress {@link FoldersPresenterImp}
     * @param total Total Number of Songs
     * @param value Number of Fetched Songs
     */
    @Override
    public void onRefreshMusicListUpdateFetchedSongs(int value, int total) {
        iListener.onRefreshMusicListFetchedSongs(value, total);
    }

    /**
     * Send Callback To Presenter for Initializing Refresh List Dialog {@link FoldersPresenterImp}
     */
    @Override
    public void onIntitalizeRefreshMusicListDialog() {
        iListener.onIntitalzeRefreshMusicListDialog();
    }

    /**
     * Cancel the Dialog {@link FoldersPresenterImp}
     */
    @Override
    public void onCancelRefreshMusicListDialog() {
        iListener.onCancelRefreshMusicListDialog();
    }

    /**
     * Send callback to Presenter {@link FoldersPresenterImp} after Successfully scanning storage
     */
    @Override
    public void onRefreshListSuccessfully() {
        onFetchedSongs();
    }

    /**
     * Fetched the Songs From the DataBase
     */
    @Override
    public void onFetchedSongs() {
        RealmResults<SongPathModel> list = mRealm.where(SongPathModel.class).findAll().sort("mSongDirectory", Sort.ASCENDING);
        ArrayList<SongPathModel> arrayList = new ArrayList<>();
        arrayList.addAll(list);
        iListener.onUpdateFoldersList(arrayList);
    }
}
