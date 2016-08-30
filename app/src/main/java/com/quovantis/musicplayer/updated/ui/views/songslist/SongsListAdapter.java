package com.quovantis.musicplayer.updated.ui.views.songslist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.helper.LoadBitmapHelper;
import com.quovantis.musicplayer.updated.interfaces.IMusicListClickListener;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sahil-goel on 24/8/16.
 */
public class SongsListAdapter extends RecyclerView.Adapter<SongsListAdapter.ViewHolder> {
    private Context mContext;
    private IMusicListClickListener iMusicListClickListener;
    private ArrayList<SongDetailsModel> mSongList;

    public SongsListAdapter(Context context, IMusicListClickListener iMusicListClickListener, ArrayList<SongDetailsModel> mSongList) {
        mContext = context;
        this.iMusicListClickListener = iMusicListClickListener;
        this.mSongList = mSongList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_song_list_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int pos = position;
        final SongDetailsModel songDetailsModel = mSongList.get(pos);
        holder.mSongTV.setText(songDetailsModel.getSongTitle());
        holder.mSongArtistTV.setText(songDetailsModel.getSongArtist());
        LoadBitmapHelper.loadBitmap(mContext, holder.mSongThumbnailIV, songDetailsModel.getSongThumbnailData());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iMusicListClickListener.onMusicListClick(songDetailsModel);
            }
        });
        holder.mPopUpMenuLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iMusicListClickListener.onActionOverFlowClick(songDetailsModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mSongList != null && !mSongList.isEmpty()) {
            return mSongList.size();
        } else {
            return 0;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_song_thumbnail)
        ImageView mSongThumbnailIV;
        @BindView(R.id.tv_song_name)
        TextView mSongTV;
        @BindView(R.id.tv_song_artist)
        TextView mSongArtistTV;
        @BindView(R.id.ll_action)
        LinearLayout mPopUpMenuLL;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
