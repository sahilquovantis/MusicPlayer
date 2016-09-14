package com.quovantis.musicplayer.updated.ui.views.createplaylist;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;

import com.quovantis.musicplayer.updated.helper.MusicHelper;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.models.UserPlaylistModel;
import com.quovantis.musicplayer.updated.utility.Utils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by sahil-goel on 7/9/16.
 */
public class CreatePlaylistInteractorImp implements ICreatePlaylistInteractor, LoaderManager.LoaderCallbacks<Cursor> {

    private Realm realm;
    private Context mContext;
    private Activity mActivity;
    private ICreatePlaylistInteractor.Listener listener;
    private String mName;
    private UserPlaylistModel mUserPlaylistModel;

    public CreatePlaylistInteractorImp(Context context, Activity activity) {
        realm = Realm.getDefaultInstance();
        mContext = context;
        mActivity = activity;
    }

    @Override
    public void getPlaylists(ICreatePlaylistInteractor.Listener listener) {
        RealmResults<UserPlaylistModel> list = realm.where(UserPlaylistModel.class).greaterThan("mPlaylistId", 0).findAll();
        ArrayList<UserPlaylistModel> lists = new ArrayList<>();
        lists.addAll(list);
        listener.onGettingPlaylists(lists);
    }

    @Override
    public void createNewPlaylist(final String name, final String id, String action,
                                  final ICreatePlaylistInteractor.Listener listener) {
        mUserPlaylistModel = null;
        this.listener = listener;
        mName = name;
        final ArrayList<SongDetailsModel> list;
        if (id == null) {
            list = MusicHelper.getInstance().getCurrentPlaylist();
            addSongs(list, name);
        } else {
            getSongs(id, action);
        }
    }

    @Override
    public void addToExistingPlaylist(final UserPlaylistModel model, String id, String action, final ICreatePlaylistInteractor.Listener listener) {
        mName = null;
        mUserPlaylistModel = model;
        this.listener = listener;
        List<SongDetailsModel> list = null;
        if (id == null) {
            list = MusicHelper.getInstance().getCurrentPlaylist();
            addSongs(model, list);
        } else {
            getSongs(id, action);
        }
    }

    private void addSongs(final UserPlaylistModel model, final List<SongDetailsModel> list) {
        if (list == null || list.isEmpty()) {
            listener.onPlaylistCreated(false);
            return;
        }
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                model.getPlaylist().addAll(list);
                realm.copyToRealmOrUpdate(model);
                listener.onPlaylistCreated(true);
            }
        });
    }

    private void getSongs(String path, String action) {
        Bundle bundle = new Bundle();
        bundle.putString("path", path);
        bundle.putString("action", action);
        mActivity.getLoaderManager().initLoader(5, bundle, this);
    }

    private int getKey(Realm realm) {
        int key;
        try {
            key = realm.where(UserPlaylistModel.class).max("mPlaylistId").intValue() + 1;
        } catch (ArrayIndexOutOfBoundsException ex) {
            key = 1;
        }
        return key;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String path = bundle.getString("path", null);
        String action = bundle.getString("action", null);
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] columns = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID};
        if (action.equalsIgnoreCase(Utils.SONG_LIST)) {
            return new CursorLoader(mContext, uri, columns, MediaStore.Audio.Media.DATA + " LIKE ?", new String[]{path}, null);
        } else if (action.equalsIgnoreCase(Utils.FOLDER_LIST)) {
            String selection = MediaStore.Audio.Media.DATA + " LIKE ? AND " + MediaStore.Audio.Media.DATA + " NOT LIKE ? ";
            String[] selectionArgs = new String[]{
                    path + "%",
                    path + "/%/%"
            };
            return new CursorLoader(mContext, uri, columns, selection, selectionArgs, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor mCursor) {
        ArrayList<SongDetailsModel> mSongList = new ArrayList<>();
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
        final ArrayList<SongDetailsModel> list = new ArrayList<>(mSongList);
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mName != null) {
                    addSongs(list, mName);
                    mName = null;
                } else {
                    addSongs(mUserPlaylistModel, list);
                    mUserPlaylistModel = null;
                }
            }
        });

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void addSongs(final ArrayList<SongDetailsModel> list, final String name) {
        if (list == null || list.isEmpty()) {
            listener.onPlaylistCreated(false);
            return;
        }
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                UserPlaylistModel userPlaylistModel = new UserPlaylistModel();
                userPlaylistModel.setPlaylistName(name);
                userPlaylistModel.setPlaylistId(getKey(realm));
                userPlaylistModel.getPlaylist().addAll(list);
                realm.copyToRealm(userPlaylistModel);
                listener.onPlaylistCreated(true);
            }
        });
    }
}
