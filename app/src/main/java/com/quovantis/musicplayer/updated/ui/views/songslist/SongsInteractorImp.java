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

import com.quovantis.musicplayer.updated.interfaces.ICommonKeys;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.models.UserPlaylistModel;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by sahil-goel on 24/8/16.
 */
public class SongsInteractorImp implements ISongsInteractor,
        LoaderManager.LoaderCallbacks<Cursor> {

    private Context mContext;
    private ISongsInteractor.Listener listener;

    public SongsInteractorImp(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void getSongsList(long id, String action, Listener listener) {
        Realm realm = Realm.getDefaultInstance();
        ArrayList<SongDetailsModel> lists = new ArrayList<>();
        RealmResults<UserPlaylistModel> list = realm.where(UserPlaylistModel.class).equalTo("mPlaylistId", id).findAll();
        lists.addAll(list.get(0).getPlaylist());
        listener.onUpdateSongsList(lists);
    }

    @Override
    public void getSongsList(String path, String action, Listener listener, Activity activity) {
        this.listener = listener;
        Bundle bundle = new Bundle();
        bundle.putString("path", path);
        activity.getLoaderManager().initLoader(7, bundle, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String path = bundle.getString("path");
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] columns = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID};
        return new CursorLoader(mContext, uri, columns, MediaStore.Audio.Media.DATA + " LIKE ?", new String[]{path + "%"}, null);
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
