package com.quovantis.musicplayer.updated.ui.views.playlists;

import com.quovantis.musicplayer.updated.models.UserPlaylistModel;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by sahil-goel on 30/8/16.
 */
public class PlaylistInteractorImp implements IPlaylistInteractor {
    @Override
    public void getPlaylists(IPlaylistInteractor.Listener listener) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<UserPlaylistModel> list = realm.where(UserPlaylistModel.class).findAll();
        ArrayList<UserPlaylistModel> lists = new ArrayList<>();
        lists.addAll(list);
        listener.onGettingPlaylists(lists);
    }
}
