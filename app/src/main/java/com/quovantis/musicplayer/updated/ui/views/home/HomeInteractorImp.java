package com.quovantis.musicplayer.updated.ui.views.home;

import android.content.Context;

import com.quovantis.musicplayer.updated.aynctasks.RefreshMusicAsyncTask;
import com.quovantis.musicplayer.updated.interfaces.ICommonKeys;
import com.quovantis.musicplayer.updated.ui.views.folders.FoldersPresenterImp;
import com.quovantis.musicplayer.updated.utility.SharedPrefrence;

import io.realm.Realm;

/**
 * Created by sahil-goel on 11/9/16.
 */
public class HomeInteractorImp implements IHomeInteractor, IHomeInteractor.RefreshSongsListListener {
    private IHomeInteractor.Listener iListener;
    private Realm mRealm;

    public HomeInteractorImp(IHomeInteractor.Listener listener) {
        iListener = listener;
        mRealm = Realm.getDefaultInstance();
    }

    /**
     * Start Async Task , which fetches songs and stores them in Database
     * {@link RefreshMusicAsyncTask}
     *
     * @param context
     */
    @Override
    public void resyncMusic(Context context) {
        new RefreshMusicAsyncTask(context, this).execute();
    }

    @Override
    public void firstTimeSync(Context context) {
        if (SharedPrefrence.getSharedPrefrenceBoolean(context, ICommonKeys.FIRST_TIME_OPEN)) {
            SharedPrefrence.saveSharedPrefrenceBoolean(context, ICommonKeys.FIRST_TIME_OPEN, false);
            resyncMusic(context);
        }
    }

    /**
     * Send Callback To Presenter for updating Progress {@link FoldersPresenterImp}
     *
     * @param total Total Number of Songs
     * @param value Number of Fetched Songs
     */
    @Override
    public void onRefreshMusicListUpdateProgress(int value, int total) {
        iListener.onRefreshMusicListProgress(value, total);
    }

    /**
     * Send Callback To Presenter for updating Progress {@link FoldersPresenterImp}
     *
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
        iListener.onRefreshSuccess();
    }
}
