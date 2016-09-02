package com.quovantis.musicplayer.updated.ui.views.search;

import android.content.Context;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by sahil-goel on 2/9/16.
 */
public class SearchInteractorImp implements ISearchInteractor {
    @Override
    public void fetchSongsList(Listener listener) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<SongDetailsModel> list = realm.where(SongDetailsModel.class).findAll().sort("mSongTitle", Sort.ASCENDING);
        ArrayList<SongDetailsModel> arrayList = new ArrayList<>();
        arrayList.addAll(list);
        listener.onFetchingList(arrayList);
    }
}
