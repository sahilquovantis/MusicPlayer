package com.quovantis.musicplayer.updated.ui.views.songslist;

import com.quovantis.musicplayer.updated.interfaces.ICommonKeys;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.models.UserPlaylistModel;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by sahil-goel on 24/8/16.
 */
public class SongsInteractorImp implements ISongsInteractor {

    @Override
    public void getSongsList(long id, String action, ISongsInteractor.Listener listener) {
        Realm realm = Realm.getDefaultInstance();
        ArrayList<SongDetailsModel> lists = new ArrayList<>();
        if (action.equalsIgnoreCase(ICommonKeys.FOLDERS_ACTION)) {
            RealmResults<SongDetailsModel> list = realm.where(SongDetailsModel.class).equalTo("mSongPathID", id).findAll().sort("mSongTitle", Sort.ASCENDING);
            lists.addAll(list);
        } else if (action.equalsIgnoreCase(ICommonKeys.PLAYLIST_ACTION)) {
            RealmResults<UserPlaylistModel> list = realm.where(UserPlaylistModel.class).equalTo("mPlaylistId", id).findAll();
            lists.addAll(list.get(0).getPlaylist());
        }
        listener.onUpdateSongsList(lists);
    }
}
