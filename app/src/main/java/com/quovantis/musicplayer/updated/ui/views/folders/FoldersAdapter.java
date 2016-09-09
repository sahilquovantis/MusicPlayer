package com.quovantis.musicplayer.updated.ui.views.folders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final SongPathModel songPathModel = mSongPathModelArrayList.get(position);
        final String directory = songPathModel.getSongDirectory();
        String path = songPathModel.getSongPath();
        path = path.substring(0, path.lastIndexOf("/"));
        final long id = songPathModel.getId();
        holder.mDirectoryNameTV.setText(directory);
        holder.mDirectoryPathTV.setText(path);
        byte[] data = songPathModel.getThumbnailData();
        if (data == null) {
            Glide.with(mContext).load(R.drawable.music).asBitmap().placeholder(R.drawable.music).into(new BitmapImageViewTarget(holder.mDirectoryThumbnail) {
                @Override
                protected void setResource(Bitmap resource) {
                    Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                        public void onGenerated(Palette palette) {
                            Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                            if (vibrantSwatch != null) {
                                holder.mBackground.setBackgroundColor(vibrantSwatch.getRgb());
                            }
                        }
                    });
                    super.setResource(resource);
                }
            });
        } else {
            Glide.with(mContext).load(data).asBitmap().placeholder(R.drawable.music).into(new BitmapImageViewTarget(holder.mDirectoryThumbnail) {
                @Override
                protected void setResource(Bitmap resource) {
                    Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                        public void onGenerated(Palette palette) {
                            Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                            if (vibrantSwatch != null) {
                                holder.mBackground.setBackgroundColor(vibrantSwatch.getRgb());
                            }
                        }
                    });
                    super.setResource(resource);
                }
            });
        }

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
