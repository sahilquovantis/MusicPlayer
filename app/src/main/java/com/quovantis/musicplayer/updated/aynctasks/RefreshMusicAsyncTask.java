package com.quovantis.musicplayer.updated.aynctasks;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.quovantis.musicplayer.updated.ui.views.folders.IFoldersInteractor;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.models.SongPathModel;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by sahil-goel on 24/8/16.
 */
public class RefreshMusicAsyncTask extends AsyncTask<Void, Integer, Void> {
    private Context mContext;
    private Realm mRealm;
    private IFoldersInteractor.RefreshSongsListListener iFoldersInteractor;
    private int mCursorSize = 0;

    public RefreshMusicAsyncTask(Context context, IFoldersInteractor.RefreshSongsListListener iFoldersInteractor) {
        mContext = context;
        this.iFoldersInteractor = iFoldersInteractor;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        iFoldersInteractor.onIntitalizeRefreshMusicListDialog();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        iFoldersInteractor.onRefreshMusicListUpdateFetchedSongs(values[0], mCursorSize);
        iFoldersInteractor.onRefreshMusicListUpdateProgress(values[0], mCursorSize);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d("Training", "Thread running : Getting Music List");
        mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mRealm.delete(SongDetailsModel.class);
            }
        });
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mRealm.delete(SongPathModel.class);
            }
        });
        updateDatabase();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        iFoldersInteractor.onRefreshListSuccessfully();
        iFoldersInteractor.onCancelRefreshMusicListDialog();
    }

    public void updateDatabase() {
        ContentResolver contentResolver = mContext.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] columns = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST};
        Cursor cursor = contentResolver.query(uri, columns, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                mCursorSize = cursor.getCount();
                Log.d("Training", "Cursor Size : " + mCursorSize);
                publishProgress(0);
                MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    final String songPath = cursor.getString(1);
                    metadataRetriever.setDataSource(songPath);
                    if (metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION) != null) {
                        final long duration = Long.parseLong(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

                        final String songName = cursor.getString(2);
                        final String songId = cursor.getString(0);
                        final String songArtist = cursor.getString(3);

                        final String path = songPath.substring(0, songPath.lastIndexOf("/"));
                        Log.d("Training", path);
                        final byte[] data = metadataRetriever.getEmbeddedPicture();
                        final int[] id = {getPathStoredID(path)};
                        if (id[0] == -1) {
                            final String directory = path.substring(path.lastIndexOf("/") + 1);
                            mRealm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    SongPathModel songPathModel = realm.createObject(SongPathModel.class);
                                    songPathModel.setSongDirectory(directory);
                                    songPathModel.setSongPath(path);
                                    songPathModel.setThumbnailData(data);
                                    songPathModel.setCompletePath(songPath);
                                    id[0] = getKey();
                                    songPathModel.setId(id[0]);
                                }
                            });
                        }

                        mRealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                SongDetailsModel songDetailsModel = realm.createObject(SongDetailsModel.class);
                                songDetailsModel.setSongID(songId);
                                songDetailsModel.setSongArtist(songArtist);
                                songDetailsModel.setSongTitle(songName);
                                songDetailsModel.setSongDuration(duration);
                                songDetailsModel.setSongThumbnailData(data);
                                songDetailsModel.setSongPathID(id[0]);
                            }
                        });
                    }
                    publishProgress(cursor.getPosition() + 1);
                    cursor.moveToNext();
                }
                cursor.close();
                metadataRetriever.release();
            }
        }
    }

    private int getPathStoredID(String path) {
        int songPathID = -1;
        RealmResults<SongPathModel> list = mRealm.where(SongPathModel.class).contains("mSongPath", path).findAll();
        if (!list.isEmpty() && list.size() == 1) {
            songPathID = list.get(0).getId();
        }
        return songPathID;
    }

    private int getKey() {
        int key;
        try {
            key = mRealm.where(SongPathModel.class).max("mId").intValue() + 1;
        } catch (ArrayIndexOutOfBoundsException ex) {
            key = 1;
        }
        return key;
    }
}
