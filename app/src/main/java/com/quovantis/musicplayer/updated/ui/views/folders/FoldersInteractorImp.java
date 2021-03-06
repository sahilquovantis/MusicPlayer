package com.quovantis.musicplayer.updated.ui.views.folders;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.quovantis.musicplayer.updated.models.SongPathModel;

import java.util.ArrayList;

/**
 * Created by sahil-goel on 23/8/16.
 */
public class FoldersInteractorImp implements IFoldersInteractor,
        LoaderManager.LoaderCallbacks<Cursor> {

    private Context mContext;
    private IFoldersInteractor.Listener mListener;

    @Override
    public void getFoldersList(Context context, Activity activity, IFoldersInteractor.Listener listener) {
        mContext = context;
        mListener = listener;
        activity.getLoaderManager().restartLoader(1, null, this);
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
        mListener.onUpdateFoldersList(list);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
