package com.quovantis.musicplayer.updated.ui.views.songslist;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by sahil-goel on 24/8/16.
 */
public class SongsInteractor implements ISongsInteractor {

    @Override
    public void getSongsList(long id, ISongsInteractor.Listener listener) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<SongDetailsModel> list = realm.where(SongDetailsModel.class).equalTo("mSongPathID", id).findAll();
        ArrayList<SongDetailsModel> lists = new ArrayList<>();
        lists.addAll(list);
        listener.onUpdateSongsList(lists);
    }
}
