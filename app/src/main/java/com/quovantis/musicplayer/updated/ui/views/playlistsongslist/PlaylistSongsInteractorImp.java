package com.quovantis.musicplayer.updated.ui.views.playlistsongslist;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.models.UserPlaylistModel;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by sahil-goel on 14/10/16.
 */

class PlaylistSongsInteractorImp implements IPlaylistSongsInteractor {

    @Override
    public void getSongsList(long id, Listener listener) {
        Realm realm = Realm.getDefaultInstance();
        ArrayList<SongDetailsModel> lists = new ArrayList<>();
        RealmResults<UserPlaylistModel> list = realm.where(UserPlaylistModel.class).equalTo("mPlaylistId", id).findAll();
        if (!list.isEmpty())
            lists.addAll(list.get(0).getPlaylist());
        listener.onUpdateSongsList(lists);
    }

    @Override
    public void removeSongFromPlaylist(long playlistId, SongDetailsModel model, final Listener listener) {
        final Realm realm = Realm.getDefaultInstance();
        RealmResults<UserPlaylistModel> list = realm.where(UserPlaylistModel.class).equalTo("mPlaylistId", playlistId).findAll();
        if (!list.isEmpty()) {
            final UserPlaylistModel playlistModel = list.get(0);
            final ArrayList<SongDetailsModel> songsList = new ArrayList<>();
            songsList.addAll(playlistModel.getPlaylist());
            if (!songsList.isEmpty())
                songsList.remove(model);
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm1) {
                    playlistModel.getPlaylist().clear();
                    playlistModel.getPlaylist().addAll(songsList);
                    realm.copyToRealmOrUpdate(playlistModel);
                }
            });
            getSongsList(playlistId, listener);
        }
    }

    @Override
    public void removeSongFromPlaylist(long playlistId, int pos, Listener listener) {
        final Realm realm = Realm.getDefaultInstance();
        RealmResults<UserPlaylistModel> list = realm.where(UserPlaylistModel.class).equalTo("mPlaylistId", playlistId).findAll();
        if (!list.isEmpty()) {
            final UserPlaylistModel playlistModel = list.get(0);
            final ArrayList<SongDetailsModel> songsList = new ArrayList<>();
            songsList.addAll(playlistModel.getPlaylist());
            if (!songsList.isEmpty() && pos < songsList.size())
                songsList.remove(pos);
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm1) {
                    playlistModel.getPlaylist().clear();
                    playlistModel.getPlaylist().addAll(songsList);
                    realm.copyToRealmOrUpdate(playlistModel);
                }
            });
            getSongsList(playlistId, listener);
        }
    }

    @Override
    public void saveToPlaylist(final List<SongDetailsModel> newSongsList, long playlistId, Listener listener) {
        final Realm realm = Realm.getDefaultInstance();
        RealmResults<UserPlaylistModel> list = realm.where(UserPlaylistModel.class).equalTo("mPlaylistId", playlistId).findAll();
        if (!list.isEmpty()) {
            final UserPlaylistModel playlistModel = list.get(0);
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm1) {
                    playlistModel.getPlaylist().clear();
                    playlistModel.getPlaylist().addAll(newSongsList);
                    realm.copyToRealmOrUpdate(playlistModel);
                }
            });
            getSongsList(playlistId, listener);
        }
    }
}
