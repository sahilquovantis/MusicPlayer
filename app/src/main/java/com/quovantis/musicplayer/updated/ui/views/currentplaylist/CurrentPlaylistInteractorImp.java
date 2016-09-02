package com.quovantis.musicplayer.updated.ui.views.currentplaylist;

import com.quovantis.musicplayer.updated.helper.MusicHelper;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.models.UserPlaylistModel;

import java.util.ArrayList;

import io.realm.Realm;

/**
 * Created by sahil-goel on 29/8/16.
 */
public class CurrentPlaylistInteractorImp implements ICurrentPlaylistInteractor {

    @Override
    public void getCurrentPlaylist(ICurrentPlaylistInteractor.Listener listener) {
        listener.onUpdateUI(MusicHelper.getInstance().getCurrentPlaylist());
    }

    @Override
    public void createPlaylist(final String name, final ICurrentPlaylistInteractor.Listener listener) {
        final ArrayList<SongDetailsModel> list = MusicHelper.getInstance().getCurrentPlaylist();
        if (list.isEmpty()) {
            listener.onPlaylistCreated(false);
            return;
        }
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                UserPlaylistModel userPlaylistModel = realm.createObject(UserPlaylistModel.class);
                userPlaylistModel.setPlaylistName(name);
                userPlaylistModel.setPlaylistId(getKey(realm));
                userPlaylistModel.getPlaylist().addAll(list);
                listener.onPlaylistCreated(true);
            }
        });
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
