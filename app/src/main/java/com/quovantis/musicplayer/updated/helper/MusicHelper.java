package com.quovantis.musicplayer.updated.helper;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.v4.media.MediaMetadataCompat;
import android.widget.Toast;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Maintains Current Songs Queue.
 */
public class MusicHelper {
    private boolean mIsSongStartedPlaying;
    private CurrentPositionHelper mCurrentPositionHelper;
    private static MusicHelper sInstance;
    private ArrayList<SongDetailsModel> mCurrentPlaylist;

    private MusicHelper() {
        mIsSongStartedPlaying = true;
        mCurrentPlaylist = new ArrayList<>();
        mCurrentPositionHelper = new CurrentPositionHelper();
    }

    public static synchronized MusicHelper getInstance() {
        if (sInstance == null) {
            sInstance = new MusicHelper();
        }
        return sInstance;
    }

    public void addSongToPlaylist(SongDetailsModel model, boolean isClearQueue, boolean isPlaythisSong) {
        if (mCurrentPlaylist == null) {
            mCurrentPlaylist = new ArrayList<>();
        }

        if (isClearQueue) {
            mCurrentPositionHelper.setCurrentPosition(0);
            mCurrentPlaylist.clear();
            mCurrentPlaylist.add(0, model);
            return;
        }

        int position = mCurrentPlaylist.isEmpty() ? 0 : mCurrentPlaylist.size();
        mCurrentPlaylist.add(position, model);
        if (isPlaythisSong) {
            mCurrentPositionHelper.setCurrentPosition(position);
        }
    }

    public void addSongToPlaylist(List<SongDetailsModel> list, boolean isClearQueue) {
        if (mCurrentPlaylist == null) {
            mCurrentPlaylist = new ArrayList<>();
        }
        if (isClearQueue) {
            mCurrentPositionHelper.setCurrentPosition(0);
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
        mCurrentPositionHelper.setCurrentPosition(playingPos);
        return true;
    }

    /**
     * Used To create MediaMetaData for Selected Song or Current Song.
     *
     * @param context Context
     * @return Returns MediaMetaData.
     */
    public MediaMetadataCompat getMetadata(Context context) {
        int pos = mCurrentPositionHelper.getCurrentPosition();
        if (isValidIndex(pos)) {
            SongDetailsModel songDetailsModel = mCurrentPlaylist.get(pos);
            try {
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(songDetailsModel.getSongPath());
                String duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                if (duration != null) {
                    MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
                    builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, songDetailsModel.getSongID());
                    builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, songDetailsModel.getSongArtist());
                    builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, songDetailsModel.getSongTitle());
                    builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, Long.parseLong(duration));
                    builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, getAlbumBitmap(context,
                            mediaMetadataRetriever.getEmbeddedPicture()));
                    return builder.build();
                }
            } catch (IllegalArgumentException e) {
                LoggerHelper.debug("MetaData Retriever Exception : " + e.getMessage());
                return null;
            }
        }
        return null;
    }

    /**
     * This Method is Used to Get The Song Uri.
     *
     * @param id Current Song Id whose URI is needed for Playing.
     * @return Returns SongUri
     */
    Uri getSongURI(String id) {
        return ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(id));
    }

    /**
     * Used To Get the Previous Song on the Basis of Current Media Id.
     *
     * @return Returns the ID of the Previous Song.
     */
    public String getPreviousSong() {
        String prevMediaId = null;
        int pos = mCurrentPositionHelper.getPreviousSong(mCurrentPlaylist.size());
        if (isValidIndex(pos))
            prevMediaId = mCurrentPlaylist.get(pos).getSongID();
        else
            mCurrentPositionHelper.setCurrentPosition(0);
        return prevMediaId;
    }

    /**
     * Used To Get the Next Song on the Basis of Current Media Id.
     *
     * @return Returns the ID of the Next Song.
     */
    public String getNextSong() {
        String nextMediaId = null;
        int pos = mCurrentPositionHelper.getNextSong(mCurrentPlaylist.size());
        if (isValidIndex(pos))
            nextMediaId = mCurrentPlaylist.get(pos).getSongID();
        else
            mCurrentPositionHelper.setCurrentPosition(0);
        return nextMediaId;
    }

    /**
     * This Method used to Get the Thumbnail of Current Song.
     *
     * @param context Context
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

    public void clearCurrentPlaylist() {
        mCurrentPlaylist.clear();
        mCurrentPositionHelper.setCurrentPosition(0);
    }

    private boolean isValidIndex(int index) {
        return 0 <= index && index < mCurrentPlaylist.size();
    }

    public void setCurrentPosition(int pos) {
        mCurrentPositionHelper.setCurrentPosition(pos);
    }

    public int getCurrentPosition() {
        return mCurrentPositionHelper.getCurrentPosition();
    }

    public boolean isSongStartedPlaying() {
        return mIsSongStartedPlaying;
    }

    public void setIsSongStartedPlaying(boolean mIsSongStartedPlaying) {
        this.mIsSongStartedPlaying = mIsSongStartedPlaying;
    }
}

