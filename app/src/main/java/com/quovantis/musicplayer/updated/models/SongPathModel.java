package com.quovantis.musicplayer.updated.models;

/**
 * Created by sahil-goel on 23/8/16.
 */
public class SongPathModel {
    private long mAlbumId;
    private String mPath;
    private String mDirectory;

    public long getAlbumId() {
        return mAlbumId;
    }

    public void setAlbumId(long mAlbumId) {
        this.mAlbumId = mAlbumId;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String mPath) {
        this.mPath = mPath;
    }

    public String getDirectory() {
        return mDirectory;
    }

    public void setDirectory(String mDirectory) {
        this.mDirectory = mDirectory;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof SongPathModel))
            return false;
        return this.getPath().equalsIgnoreCase(((SongPathModel) obj).getPath());
    }
}
