package com.quovantis.musicplayer.updated.models;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sahil-goel on 29/8/16.
 */
public class UserPlaylistModel extends RealmObject {
    @PrimaryKey
    private long mPlaylistId;
    private String mPlaylistName;
    @Ignore
    private Date mPlaylistCreatedDate;
    private RealmList<SongDetailsModel> mPlaylist = new RealmList<>();

    public long getPlaylistId() {
        return mPlaylistId;
    }

    public void setPlaylistId(long mPlaylistId) {
        this.mPlaylistId = mPlaylistId;
    }

    public String getPlaylistName() {
        return mPlaylistName;
    }

    public void setPlaylistName(String mPlaylistName) {
        this.mPlaylistName = mPlaylistName;
    }

    public Date getPlaylistCreatedDate() {
        return mPlaylistCreatedDate;
    }

    public void setPlaylistCreatedDate(Date mPlaylistCreatedDate) {
        this.mPlaylistCreatedDate = mPlaylistCreatedDate;
    }

    public RealmList<SongDetailsModel> getPlaylist() {
        return mPlaylist;
    }

    public void setPlaylist(RealmList<SongDetailsModel> mPlaylist) {
        this.mPlaylist = mPlaylist;
    }
}
