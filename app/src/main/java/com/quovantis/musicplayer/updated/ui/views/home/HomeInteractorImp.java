package com.quovantis.musicplayer.updated.ui.views.home;

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
public class HomeInteractorImp implements IHomeInteractor, LoaderManager.LoaderCallbacks<Cursor> {

    private Context mContext;
    private IHomeInteractor.Listener iListener;
    private boolean mIsClearQueue;
    private boolean mIsPlayThisSong;

    @Override
    public void getSongsListFromFolder(String path, boolean isClearQueue, boolean isPlaythisSong, Context context, Activity activity,
                                       IHomeInteractor.Listener listener) {
        iListener = listener;
        mContext = context;
        mIsClearQueue = isClearQueue;
        mIsPlayThisSong = isPlaythisSong;
        Bundle bundle = new Bundle();
        bundle.putString("path", path);
        activity.getLoaderManager().initLoader(10, bundle, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String path = bundle.getString("path");
        String selection = MediaStore.Audio.Media.DATA + " LIKE ? AND " + MediaStore.Audio.Media.DATA + " NOT LIKE ? ";
        String[] selectionArgs = new String[]{
                path + "%",
                path + "/%/%"
        };
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] columns = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID};
        return new CursorLoader(mContext, uri, columns, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor mCursor) {
        ArrayList<SongDetailsModel> mSongList = new ArrayList<SongDetailsModel>();
        if (mCursor != null) {
            mCursor.moveToFirst();
            mSongList.clear();
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
        iListener.onSuccess(mSongList, mIsClearQueue, mIsPlayThisSong);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
