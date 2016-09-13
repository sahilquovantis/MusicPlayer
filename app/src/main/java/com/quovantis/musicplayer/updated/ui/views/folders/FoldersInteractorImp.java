package com.quovantis.musicplayer.updated.ui.views.folders;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.quovantis.musicplayer.updated.aynctasks.RefreshMusicAsyncTask;
import com.quovantis.musicplayer.updated.dialogs.RefreshListDialog;
import com.quovantis.musicplayer.updated.interfaces.ICommonKeys;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.models.SongPathModel;
import com.quovantis.musicplayer.updated.utility.SharedPrefrence;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by sahil-goel on 23/8/16.
 */
public class FoldersInteractorImp implements IFoldersInteractor , LoaderManager.LoaderCallbacks<Cursor>{

    private Context mContext;
    private IFoldersInteractor.Listener mListener;
    @Override
    public void getFoldersList(Context context, Activity activity, IFoldersInteractor.Listener listener) {
        mContext = context;
        mListener = listener;
        //activity.getLoaderManager().initLoader(1,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] columns = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID};
        return new CursorLoader(mContext, uri, columns, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ArrayList<SongPathModel> list = new ArrayList<>();
        if (data != null) {
            data.moveToFirst();
            while (!data.isAfterLast()) {
                String songPath = data.getString(1);
                String path = songPath.substring(0, songPath.lastIndexOf("/"));
                SongPathModel model = new SongPathModel();
                model.setAlbumId(data.getLong(4));
                model.setPath(path);
                model.setDirectory(path.substring(path.lastIndexOf("/") + 1));
                if (list.isEmpty() || !list.contains(model)) {
                    list.add(model);
                }
                data.moveToNext();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
