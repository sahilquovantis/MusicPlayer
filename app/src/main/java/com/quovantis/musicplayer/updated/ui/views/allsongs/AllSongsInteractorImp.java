package com.quovantis.musicplayer.updated.ui.views.allsongs;

import android.util.Log;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by sahil-goel on 11/9/16.
 */
public class AllSongsInteractorImp implements IAllSongsInteractor {

    @Override
    public void getSongsList(final Listener listener) {
        Log.d("Training", "Interactor Called");
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<SongDetailsModel> list = realm.where(SongDetailsModel.class).findAllSorted("mSongTitle", Sort.ASCENDING);
        ArrayList<SongDetailsModel> arrayList = new ArrayList<>();
        arrayList.addAll(list);
        Log.d("Training", "Song Size : " + arrayList.size());
        listener.onGettingSongsList(arrayList);
    }
}
