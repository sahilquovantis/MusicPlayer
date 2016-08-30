package com.quovantis.musicplayer.updated.ui.views.playlistsongs;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.models.UserPlaylistModel;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by sahil-goel on 30/8/16.
 */
public class PlaylistSongsInteractorImp implements IPlaylistSongsInteractor {
    @Override
    public void getSongsList(long id, Listener listener) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<UserPlaylistModel> list = realm.where(UserPlaylistModel.class).equalTo("mPlaylistId", id).findAll();
        ArrayList<SongDetailsModel> lists = new ArrayList<>();
        lists.addAll(list.get(0).getPlaylist());
        listener.onUpdateSongsList(lists);
    }
}
