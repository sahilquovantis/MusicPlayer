package com.quovantis.musicplayer.updated.ui.views.folders;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
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
    private Uri mArtworkUri;
    private Context mContext;
    private IFolderClickListener iFolderClickListener;
    private ArrayList<SongPathModel> mSongPathModelArrayList = new ArrayList<>();

    public FoldersAdapter(Context context, ArrayList<SongPathModel> songPathModelList,
                          IFolderClickListener iFolderClickListener) {
        mArtworkUri = Uri.parse("content://media/external/audio/albumart");
        mContext = context;
        mSongPathModelArrayList = songPathModelList;
        this.iFolderClickListener = iFolderClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_folders_list_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final SongPathModel songPathModel = mSongPathModelArrayList.get(position);
        final String directory = songPathModel.getDirectory();
        final String path = songPathModel.getPath();
        final long id = songPathModel.getAlbumId();
        holder.mDirectoryNameTV.setText(directory);
        holder.mDirectoryPathTV.setText(path);
        Glide.with(mContext).load(ContentUris.withAppendedId(mArtworkUri, id)).asBitmap().error(R.drawable.music).
                placeholder(R.drawable.music).into(new BitmapImageViewTarget(holder.mDirectoryThumbnail) {
            @Override
            protected void setResource(Bitmap resource) {
                if (resource != null) {
                    Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                        public void onGenerated(Palette palette) {
                            Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                            if (vibrantSwatch != null) {
                                holder.mBackground.setBackgroundColor(vibrantSwatch.getRgb());
                            } else {
                                holder.mBackground.setBackgroundColor(ContextCompat.getColor(mContext, R.color.bg));
                            }
                        }
                    });
                }
                super.setResource(resource);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iFolderClickListener.onFoldersSinglePress(path, directory);
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

        @BindView(R.id.rl_background)
        RelativeLayout mBackground;
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
}
