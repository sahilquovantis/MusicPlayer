package com.quovantis.musicplayer.updated.ui.views.folders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.interfaces.IFolderClickListener;
import com.quovantis.musicplayer.updated.models.SongPathModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sahil-goel on 23/8/16.
 */
public class FoldersAdapter extends RecyclerView.Adapter<FoldersAdapter.ViewHolder> {

    private Context mContext;
    private IFolderClickListener iFolderClickListener;
    private MediaMetadataRetriever metadataRetriever;
    private ArrayList<SongPathModel> mSongPathModelArrayList = new ArrayList<>();

    public FoldersAdapter(Context context, ArrayList<SongPathModel> songPathModelList,
                          IFolderClickListener iFolderClickListener) {
        mContext = context;
        mSongPathModelArrayList = songPathModelList;
        metadataRetriever = new MediaMetadataRetriever();
        this.iFolderClickListener = iFolderClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_folders_list_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final SongPathModel songPathModel = mSongPathModelArrayList.get(position);
        final String directory = songPathModel.getSongDirectory();
        String path = songPathModel.getSongPath();
        path = path.substring(0, path.lastIndexOf("/"));
        final long id = songPathModel.getId();
        holder.mDirectoryNameTV.setText(directory);
        holder.mDirectoryPathTV.setText(path);
        loadBitmap(holder.mDirectoryThumbnail, songPathModel.getCompletePath());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iFolderClickListener.onFoldersSinglePress(id, directory);
            }
        });

       holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                iFolderClickListener.onFoldersLongPress(songPathModel);
               return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSongPathModelArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_folder_name)
        TextView mDirectoryNameTV;
        @BindView(R.id.tv_folder_path)
        TextView mDirectoryPathTV;
        @BindView(R.id.iv_song_thumbnail)
        ImageView mDirectoryThumbnail;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void loadBitmap(ImageView imageView, String data) {
        if (cancelPotentialWork(data, imageView)) {
            final DisplayImage task = new DisplayImage(imageView);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(data);
        }
    }

    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<DisplayImage> bitmapWorkerTask;

        public AsyncDrawable(DisplayImage bitmapTask) {
            bitmapWorkerTask = new WeakReference<DisplayImage>(bitmapTask);
        }

        public DisplayImage getBitmapWorkerTask() {
            return bitmapWorkerTask.get();
        }
    }

    public static boolean cancelPotentialWork(String data, ImageView imageView) {
        final DisplayImage bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final String bitmapData = bitmapWorkerTask.data;
            if (bitmapData != data) {
                bitmapWorkerTask.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }

    private static DisplayImage getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }


    class DisplayImage extends AsyncTask<String, Void, Bitmap> {

        private WeakReference<ImageView> imageViewWeakReference;
        public String data = null;

        DisplayImage(ImageView iv) {
            imageViewWeakReference = new WeakReference<ImageView>(iv);
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            data = strings[0];
            Bitmap bitmap = null;
            try {
                metadataRetriever.setDataSource(data);
                byte[] imageData = metadataRetriever.getEmbeddedPicture();
                if (imageData == null) {
                    bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.music);
                } else {
                    bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                }
            } catch (IllegalArgumentException e) {
                bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.music);
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (isCancelled()) {
                bitmap = null;
            }
            if (bitmap != null && imageViewWeakReference != null) {
                ImageView imageView = imageViewWeakReference.get();
                final DisplayImage bitmapWorkerTask =
                        getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }
}
