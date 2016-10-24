package com.quovantis.musicplayer.updated.ui.views.songslist;

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
import com.quovantis.musicplayer.updated.models.UserPlaylistModel;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Fetches songs from the storage with the help of cursor loader.
 */
class SongsInteractorImp implements ISongsInteractor,
        LoaderManager.LoaderCallbacks<Cursor> {

    private Context mContext;
    private ISongsInteractor.Listener listener;

    @Override
    public void getSongsList(String path, Listener listener, Activity activity) {
        this.listener = listener;
        mContext = activity;
        Bundle bundle = new Bundle();
        bundle.putString("path", path);
        activity.getLoaderManager().restartLoader(7, bundle, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String path = bundle.getString("path");
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] columns = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID};
        String selection = MediaStore.Audio.Media.DATA + " LIKE ? AND " + MediaStore.Audio.Media.DATA + " NOT LIKE ? ";
        String[] selectionArgs = new String[]{
                path + "%",
                path + "/%/%"
        };
        return new CursorLoader(mContext, uri, columns, selection, selectionArgs, columns[2] + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor mCursor) {
        ArrayList<SongDetailsModel> mSongList = new ArrayList<>();
        if (mCursor != null) {
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
                mSongList.add(model);
                mCursor.moveToNext();
            }
        }
        listener.onUpdateSongsList(mSongList);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
