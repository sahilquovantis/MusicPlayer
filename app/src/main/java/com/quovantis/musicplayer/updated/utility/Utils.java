package com.quovantis.musicplayer.updated.utility;

import android.content.Context;
import android.support.v4.media.MediaMetadataCompat;

import com.quovantis.musicplayer.updated.constants.AppPreferenceKeys;
import com.quovantis.musicplayer.updated.helper.MusicHelper;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.models.UserPlaylistModel;
import com.quovantis.musicplayer.updated.services.MusicService;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by sahil-goel on 24/10/16.
 */

public class Utils {
    public static void updateDefaultPlaylist(final Context context) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                List<SongDetailsModel> list = MusicHelper.getInstance().getCurrentPlaylist();
                UserPlaylistModel userPlaylistModel = new UserPlaylistModel();
                userPlaylistModel.setPlaylistName("Default");
                userPlaylistModel.setPlaylistId(0);
                userPlaylistModel.getPlaylist().addAll(list);
                realm.copyToRealmOrUpdate(userPlaylistModel);
                int pos = 0;
                if (list != null && !list.isEmpty()) {
                    pos = MusicHelper.getInstance().getCurrentPosition();
                }
                SharedPreference.getInstance().putInt(context, AppPreferenceKeys.CURRENT_POSITION, pos);
            }
        });
    }

    public static void initCurrentPlaylist(final Context context){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<UserPlaylistModel> realmResults = realm.where(UserPlaylistModel.class).
                        equalTo("mPlaylistId", 0).findAll();
                if (realmResults.size() > 0) {
                    ArrayList<SongDetailsModel> list = new ArrayList<>(realmResults.get(0).getPlaylist());
                    int pos = SharedPreference.getInstance().getInt(context, AppPreferenceKeys.CURRENT_POSITION);
                    MusicHelper.getInstance().setCurrentPlaylist(list, pos);
                }
            }
        });
    }
}
