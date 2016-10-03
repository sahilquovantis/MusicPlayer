package com.quovantis.musicplayer.updated.ui.views.allsongs;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.ArrayList;

/**
 * Created by sahil-goel on 11/9/16.
 */
public class AllSongsInteractorImp implements IAllSongsInteractor, LoaderManager.LoaderCallbacks<Cursor> {

    private Context mContext;
    private IAllSongsInteractor.Listener mListener;

    @Override
    public void getSongsList(Context context, Activity activity, Listener listener) {
        mContext = context;
        mListener = listener;
        activity.getLoaderManager().restartLoader(2, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] columns = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID};
        return new CursorLoader(mContext, uri, columns, null, null, MediaStore.Audio.Media.TITLE + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor mCursor) {
        ArrayList<SongDetailsModel> mSongsList = new ArrayList<>();
        if (mCursor != null) {
            mSongsList.clear();
            mCursor.moveToFirst();
            while (!mCursor.isAfterLast()) {
                SongDetailsModel model = new SongDetailsModel();
                final String title = mCursor.getString(2);
                final String id = mCursor.getString(0);
                final String artist = mCursor.getString(3);
                final String path = mCursor.getString(1);
                long albumId = mCursor.getLong(4);
                model.setSongTitle(title);
                model.setSongArtist(artist);
                model.setAlbumId(albumId);
                model.setSongPath(path);
                model.setSongID(id);
                mSongsList.add(model);
                mCursor.moveToNext();
            }
        }
        mListener.onGettingSongsList(mSongsList);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
