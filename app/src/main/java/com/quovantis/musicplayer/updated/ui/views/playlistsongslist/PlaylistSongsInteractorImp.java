package com.quovantis.musicplayer.updated.ui.views.playlistsongslist;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.models.UserPlaylistModel;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by sahil-goel on 14/10/16.
 */

public class PlaylistSongsInteractorImp implements IPlaylistSongsInteractor {

    @Override
    public void getSongsList(long id, Listener listener) {
        Realm realm = Realm.getDefaultInstance();
        ArrayList<SongDetailsModel> lists = new ArrayList<>();
        RealmResults<UserPlaylistModel> list = realm.where(UserPlaylistModel.class).equalTo("mPlaylistId", id).findAll();
        lists.addAll(list.get(0).getPlaylist());
        listener.onUpdateSongsList(lists);
    }
}
