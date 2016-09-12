package com.quovantis.musicplayer.updated.ui.views.folders;

import android.content.Context;

import com.quovantis.musicplayer.updated.aynctasks.RefreshMusicAsyncTask;
import com.quovantis.musicplayer.updated.dialogs.RefreshListDialog;
import com.quovantis.musicplayer.updated.interfaces.ICommonKeys;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.models.SongPathModel;
import com.quovantis.musicplayer.updated.utility.SharedPrefrence;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by sahil-goel on 23/8/16.
 */
public class FoldersInteractorImp implements IFoldersInteractor {

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
     *
     * @param context
     */
    @Override
    public void getFoldersList(Context context) {
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
