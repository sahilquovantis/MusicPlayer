package com.quovantis.musicplayer.updated.ui.views.search;

import android.content.Context;
import android.os.AsyncTask;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by sahil-goel on 2/9/16.
 */
public class SearchInteractorImp implements ISearchInteractor {
    private ArrayList<SongDetailsModel> arrayList = new ArrayList<>();
    private RealmResults<SongDetailsModel> list;
    private ISearchInteractor.Listener listener;

    @Override
    public void fetchSongsList(final ISearchInteractor.Listener listener) {
        this.listener = listener;
        Realm realm = Realm.getDefaultInstance();
        list = realm.where(SongDetailsModel.class).findAllSortedAsync("mSongTitle", Sort.ASCENDING);
        list.addChangeListener(new RealmChangeListener<RealmResults<SongDetailsModel>>() {
            @Override
            public void onChange(RealmResults<SongDetailsModel> element) {
                arrayList.addAll(element);
                if (list.isLoaded()) {
                    listener.onFetchingList(arrayList);
                    list.removeChangeListener(this);
                }
            }
        });
    }
}
