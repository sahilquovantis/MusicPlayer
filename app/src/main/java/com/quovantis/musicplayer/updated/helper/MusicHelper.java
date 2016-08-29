package com.quovantis.musicplayer.updated.helper;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.interfaces.ICommonKeys;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;

import io.realm.RealmResults;

/**
 * Created by sahil-goel on 25/8/16.
 */
public class MusicHelper {
    private int mCurrentPosition = 0;
    private SongDetailsModel mCurrentSong;
    private static MusicHelper sInstance;
    private ArrayList<SongDetailsModel> mCurrentPlaylist;
    private LinkedHashSet<SongDetailsModel> mSongsSet;

    private MusicHelper() {
        mSongsSet = new LinkedHashSet<>();
        mCurrentPlaylist = new ArrayList<>(mSongsSet);
    }

   /* public void addSongToPlaylist(SongDetailsModel songDetailsModel) {
        try {
            if (mCurrentPlaylist != null &&
                    !mCurrentPlaylist.isEmpty() &&
                    mCurrentPlaylist.contains(songDetailsModel)) {
                mCurrentPosition = mCurrentPlaylist.indexOf(songDetailsModel);
            } else {
                mCurrentPosition += 1;
                mCurrentPlaylist.add(mCurrentPosition, songDetailsModel);
            }
        } catch (NullPointerException e) {
        }
    }*/

    public void addSongToPlaylist(SongDetailsModel model, boolean isClearQueue, boolean isPlaythisSong) {
        try {

            if (isClearQueue) {
                mCurrentPosition = 0;
                mCurrentPlaylist.clear();
                mCurrentPlaylist.add(0, model);
                return;
            }

            if (!mCurrentPlaylist.isEmpty() && mCurrentPlaylist.contains(model)) {
                mCurrentPosition = mCurrentPlaylist.indexOf(model);
                return;
            }
            int position = mCurrentPlaylist.isEmpty() ? 0 : mCurrentPlaylist.size();
            mCurrentPlaylist.add(position, model);
            if (isPlaythisSong) {
                mCurrentPosition = position;
            }
        } catch (NullPointerException | IndexOutOfBoundsException e1) {
            mCurrentPosition = 0;
        }
    }

    public boolean addSongToPlaylist(RealmResults<SongDetailsModel> list) {
        boolean isAdded;
        mSongsSet.addAll(list);
        mCurrentPlaylist.clear();
        isAdded = mCurrentPlaylist.addAll(mSongsSet);
        return isAdded;
    }

    public boolean addSongToPlaylist(ArrayList<SongDetailsModel> list, int pos) {
        mCurrentPosition = pos;
        boolean isAdded;
        mSongsSet.clear();
        mSongsSet.addAll(list);
        mCurrentPlaylist.clear();
        isAdded = mCurrentPlaylist.addAll(mSongsSet);
        return isAdded;
    }

    public ArrayList<SongDetailsModel> getCurrentPlaylist() {
        return mCurrentPlaylist;
    }

    public static synchronized MusicHelper getInstance() {
        if (sInstance == null) {
            sInstance = new MusicHelper();
        }
        return sInstance;
    }

    /**
     * Used To create MediaMetaData for Selected Song or Current Song.
     *
     * @param context
     * @param mediaId ID of the Current Song whose MetaData is Needed.
     * @return Returns MediaMetaData.
     */
    public MediaMetadataCompat getMetadata(Context context, String mediaId) {
        try {
            if (!mCurrentPlaylist.isEmpty()) {
                Log.d("Training", "Current Position : " + mCurrentPosition);
                SongDetailsModel songDetailsModel = mCurrentPlaylist.get(mCurrentPosition);
                mCurrentSong = songDetailsModel;
                MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
                builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, songDetailsModel.getSongID());
                builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, songDetailsModel.getSongArtist());
                builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, songDetailsModel.getSongTitle());
                builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, songDetailsModel.getSongDuration());
                builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, getAlbumBitmap(context,
                        songDetailsModel.getSongThumbnailData()));
                return builder.build();
            }
        } catch (IllegalArgumentException | IndexOutOfBoundsException | NullPointerException e) {
            mCurrentSong = null;
            return null;
        }
        return null;
    }

    /**
     * This Method is Used to Get The Song Uri.
     *
     * @param id Current Song Id whose URI is needed for Playing.
     * @return Returns SongUri
     */
    public Uri getSongURI(String id) {
        return ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(id));
    }

    /**
     * Used To Get the Previous Song on the Basis of Current Media Id.
     *
     * @return Returns the ID of the Previous Song.
     */
    public String getPreviousSong() {
        String prevMediaId = null;
        try {
            if (mCurrentPlaylist != null && !mCurrentPlaylist.isEmpty()) {
                if (mCurrentPosition == 0) {
                    mCurrentPosition = mCurrentPlaylist.size() - 1;
                } else {
                    mCurrentPosition -= 1;
                }
                prevMediaId = mCurrentPlaylist.get(mCurrentPosition).getSongID();
            }
        } catch (IllegalArgumentException | IndexOutOfBoundsException | NullPointerException e) {
            mCurrentPosition = 0;
        }
        return prevMediaId;
    }

    /**
     * Used To Get the Next Song on the Basis of Current Media Id.
     *
     * @return Returns the ID of the Next Song.
     */
    public String getNextSong() {
        String nextMediaId = null;
        try {
            if (mCurrentPlaylist != null && !mCurrentPlaylist.isEmpty()) {
                if (mCurrentPosition == mCurrentPlaylist.size() - 1) {
                    mCurrentPosition = 0;
                } else {
                    mCurrentPosition += 1;
                }
                nextMediaId = mCurrentPlaylist.get(mCurrentPosition).getSongID();
            }
        } catch (IllegalArgumentException | IndexOutOfBoundsException | NullPointerException e) {
            mCurrentPosition = 0;
        }
        return nextMediaId;
    }

    /**
     * This Method used to Get the Thumbnail of Current Song.
     *
     * @param context
     * @param data    Byte Data Contains the Picture's Blob.
     * @return Returns Bitmap of Current Song.
     */
    private Bitmap getAlbumBitmap(Context context, byte[] data) {
        Bitmap bitmap;
        if (data == null) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.music);
        } else {
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        }
        return bitmap;
    }

    public void songsMoved(int from, int to) {
        if (mCurrentPlaylist != null && !mCurrentPlaylist.isEmpty()) {
            Collections.swap(mCurrentPlaylist, from, to);
            mCurrentPosition = mCurrentSong == null ? 0 : mCurrentPlaylist.indexOf(mCurrentSong);
            Log.d(ICommonKeys.TAG,"Current Pos : " + mCurrentPosition);
        }
    }

    public void songRemove(int pos) {
        if (mCurrentPlaylist != null && !mCurrentPlaylist.isEmpty()) {
            try {
                mCurrentPlaylist.remove(pos);
                if (mCurrentPosition == pos)
                    mCurrentPosition = pos - 1;
            } catch (IndexOutOfBoundsException e) {
                mCurrentPosition = 0;
            }
        }
    }
}

