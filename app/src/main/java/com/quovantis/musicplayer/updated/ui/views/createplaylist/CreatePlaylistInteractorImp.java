package com.quovantis.musicplayer.updated.ui.views.createplaylist;

import com.quovantis.musicplayer.updated.helper.MusicHelper;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.models.UserPlaylistModel;
import com.quovantis.musicplayer.updated.utility.Utils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by sahil-goel on 7/9/16.
 */
public class CreatePlaylistInteractorImp implements ICreatePlaylistInteractor {

    private Realm realm;

    public CreatePlaylistInteractorImp() {
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void getPlaylists(ICreatePlaylistInteractor.Listener listener) {
        RealmResults<UserPlaylistModel> list = realm.where(UserPlaylistModel.class).greaterThan("mPlaylistId",0).findAll();
        ArrayList<UserPlaylistModel> lists = new ArrayList<>();
        lists.addAll(list);
        listener.onGettingPlaylists(lists);
    }

    @Override
    public void createNewPlaylist(final String name, final String id, String action,
                                  final ICreatePlaylistInteractor.Listener listener) {
        final List<SongDetailsModel> list;
        if (id == null) {
            list = MusicHelper.getInstance().getCurrentPlaylist();
        } else {
            list = getSongs(id, action);

        }
        if (list == null || list.isEmpty()) {
            listener.onPlaylistCreated(false);
            return;
        }
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                UserPlaylistModel userPlaylistModel = new UserPlaylistModel();
                userPlaylistModel.setPlaylistName(name);
                userPlaylistModel.setPlaylistId(getKey(realm));
                userPlaylistModel.getPlaylist().addAll(list);
                realm.copyToRealm(userPlaylistModel);
                listener.onPlaylistCreated(true);
            }
        });
    }

    @Override
    public void addToExistingPlaylist(final UserPlaylistModel model, String id, String action, final Listener listener) {
        final List<SongDetailsModel> list;
        if (id == null) {
            list = MusicHelper.getInstance().getCurrentPlaylist();
        } else {
            list = getSongs(id, action);

        }
        if (list == null || list.isEmpty()) {
            listener.onPlaylistCreated(false);
            return;
        }
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                model.getPlaylist().addAll(list);
                realm.copyToRealmOrUpdate(model);
                listener.onPlaylistCreated(true);
            }
        });

    }

    private List<SongDetailsModel> getSongs(String id, String action) {
        final RealmResults<SongDetailsModel> list;
        if (action.equalsIgnoreCase(Utils.SONG_LIST)) {
            list = realm.where(SongDetailsModel.class).equalTo("mSongID", id).findAll();
        } else {
            list = realm.where(SongDetailsModel.class).equalTo("mSongPathID", Integer.parseInt(id)).findAll().sort("mSongTitle", Sort.ASCENDING);
        }
        return list;
    }

    private int getKey(Realm realm) {
        int key;
        try {
            key = realm.where(UserPlaylistModel.class).max("mPlaylistId").intValue() + 1;
        } catch (ArrayIndexOutOfBoundsException ex) {
            key = 1;
        }
        return key;
    }
}
