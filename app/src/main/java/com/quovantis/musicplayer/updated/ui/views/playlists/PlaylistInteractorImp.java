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
        RealmResults<UserPlaylistModel> list = realm.where(UserPlaylistModel.class).
                greaterThan("mPlaylistId", 0).findAll();
        ArrayList<UserPlaylistModel> lists = new ArrayList<>();
        lists.addAll(list);
        listener.onGettingPlaylists(lists);
    }

    @Override
    public void renamePlaylist(final UserPlaylistModel model, final String newName, final IPlaylistInteractor.Listener listener) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                model.setPlaylistName(newName);
                realm.copyToRealmOrUpdate(model);
                listener.onSuccessfullyRenaming();
            }
        });
    }
}
