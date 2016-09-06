package com.quovantis.musicplayer.updated.helper;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;
import android.widget.Toast;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.interfaces.ICommonKeys;
import com.quovantis.musicplayer.updated.interfaces.IOnSongRemovedFromQueue;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import io.realm.RealmResults;

/**
 * Created by sahil-goel on 25/8/16.
 */
public class MusicHelper {
    private int mCurrentPosition = 0;
    private SongDetailsModel mCurrentSong;
    private static MusicHelper sInstance;
    private ArrayList<SongDetailsModel> mCurrentPlaylist;

    private MusicHelper() {
        mCurrentPlaylist = new ArrayList<>();
    }

    public void addSongToPlaylist(SongDetailsModel model, boolean isClearQueue, boolean isPlaythisSong) {
        if (mCurrentPlaylist == null) {
            mCurrentPlaylist = new ArrayList<>();
        }

        if (isClearQueue) {
            mCurrentPosition = 0;
            mCurrentPlaylist.clear();
            mCurrentPlaylist.add(0, model);
            return;
        }

        int position = mCurrentPlaylist.isEmpty() ? 0 : mCurrentPlaylist.size();
        mCurrentPlaylist.add(position, model);
        if (isPlaythisSong) {
            mCurrentPosition = position;
        }
    }

    public void addSongToPlaylist(List<SongDetailsModel> list, boolean isClearQueue) {
        if (mCurrentPlaylist == null) {
            mCurrentPlaylist = new ArrayList<>();
        }
        if (isClearQueue) {
            mCurrentPosition = 0;
            mCurrentPlaylist.clear();
        }
        mCurrentPlaylist.addAll(list);
    }

    public ArrayList<SongDetailsModel> getCurrentPlaylist() {
        return mCurrentPlaylist;
    }

    public boolean setCurrentPlaylist(ArrayList<SongDetailsModel> list, int playingPos) {
        if (mCurrentPlaylist == null)
            mCurrentPlaylist = new ArrayList<>();
        mCurrentPlaylist.clear();
        mCurrentPlaylist.addAll(list);
        mCurrentPosition = playingPos;
        return true;
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
     * @return Returns MediaMetaData.
     */
    public MediaMetadataCompat getMetadata(Context context) {
        mCurrentSong = null;
        if (isValidIndex(mCurrentPosition)) {
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
        int size = mCurrentPlaylist.size();
        if (mCurrentPosition == 0 || mCurrentPosition < 0)
            mCurrentPosition = size - 1;
        else
            mCurrentPosition -= 1;
        if (isValidIndex(mCurrentPosition))
            prevMediaId = mCurrentPlaylist.get(mCurrentPosition).getSongID();
        else
            mCurrentPosition = 0;
        return prevMediaId;
    }

    /**
     * Used To Get the Next Song on the Basis of Current Media Id.
     *
     * @return Returns the ID of the Next Song.
     */
    public String getNextSong() {
        String nextMediaId = null;
        int size = mCurrentPlaylist.size();
        if (mCurrentPosition == size - 1 || mCurrentPosition >= size)
            mCurrentPosition = 0;
        else
            mCurrentPosition += 1;
        if (isValidIndex(mCurrentPosition))
            nextMediaId = mCurrentPlaylist.get(mCurrentPosition).getSongID();
        else
            mCurrentPosition = 0;
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
            Log.d(ICommonKeys.TAG, "Current Pos : " + mCurrentPosition);
        }
    }

    public void songRemove(int pos, IOnSongRemovedFromQueue iOnSongRemovedFromQueue) {
        if (mCurrentPlaylist != null && !mCurrentPlaylist.isEmpty()) {
            try {
                mCurrentPlaylist.remove(pos);
                if (mCurrentPlaylist == null || mCurrentPlaylist.isEmpty()) {
                    iOnSongRemovedFromQueue.onQueueListEmptyShowEmptyTV();
                }
                if (mCurrentPosition == pos) {
                    mCurrentPosition = pos - 1;
                    iOnSongRemovedFromQueue.onCurrentPlayingSongRemoved();
                }
                iOnSongRemovedFromQueue.onSongRemovedSuccessfully(mCurrentPosition);
            } catch (IndexOutOfBoundsException e) {
                mCurrentPosition = 0;
            }
        }
    }

    public void clearCurrentPlaylist() {
        mCurrentPlaylist.clear();
        mCurrentPosition = 0;
    }

    private boolean isValidIndex(int index) {
        return 0 <= index && index < mCurrentPlaylist.size();
    }
}

