package com.quovantis.musicplayer.updated.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.quovantis.musicplayer.R;

import java.lang.ref.WeakReference;

/**
 * Created by sahil-goel on 30/8/16.
 */
public class LoadBitmapHelper {
    public static void loadBitmap(Context mContext, ImageView imageView, byte[] data) {
        if (cancelPotentialWork(data, imageView)) {
            final DisplayImage task = new DisplayImage(imageView, mContext);
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

    public static boolean cancelPotentialWork(byte[] data, ImageView imageView) {
        final DisplayImage bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final byte[] bitmapData = bitmapWorkerTask.data;
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


    static class DisplayImage extends AsyncTask<byte[], Void, Bitmap> {

        private WeakReference<ImageView> imageViewWeakReference;
        public byte[] data = null;
        private Context mContext;

        DisplayImage(ImageView iv, Context mContext) {
            imageViewWeakReference = new WeakReference<ImageView>(iv);
            this.mContext = mContext;
        }

        @Override
        protected Bitmap doInBackground(byte[]... strings) {
            data = strings[0];
            Bitmap bitmap = null;
            if (data == null) {
                bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.music);
            } else {
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
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
