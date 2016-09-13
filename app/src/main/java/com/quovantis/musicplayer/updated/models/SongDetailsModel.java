package com.quovantis.musicplayer.updated.models;

import org.parceler.Parcel;

import io.realm.RealmObject;
import io.realm.SongDetailsModelRealmProxy;

/**
 * @author sahil-goel
 *         Model of the Song or Music and Contains Information of the Songs.
 */
public class SongDetailsModel extends RealmObject {
    private String mSongID;
    private String mSongTitle;
    private String mSongArtist;
    private String mSongPath;
    private long mAlbumId;

    public long getAlbumId() {
        return mAlbumId;
    }

    public void setAlbumId(long albumId) {
        mAlbumId = albumId;
    }

    public String getSongID() {
        return mSongID;
    }

    public void setSongID(String mSongID) {
        this.mSongID = mSongID;
    }

    public String getSongTitle() {
        return mSongTitle;
    }

    public void setSongTitle(String mSongTitle) {
        this.mSongTitle = mSongTitle;
    }

    public String getSongArtist() {
        return mSongArtist;
    }

    public void setSongArtist(String mSongArtist) {
        this.mSongArtist = mSongArtist;
    }

    public String getSongPath() {
        return mSongPath;
    }

    public void setSongPath(String mSongPath) {
        this.mSongPath = mSongPath;
    }
}

