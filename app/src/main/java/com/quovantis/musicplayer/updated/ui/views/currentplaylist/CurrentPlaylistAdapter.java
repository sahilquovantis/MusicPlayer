package com.quovantis.musicplayer.updated.ui.views.currentplaylist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.helper.LoadBitmapHelper;
import com.quovantis.musicplayer.updated.interfaces.ICurrentPlaylistClickListener;
import com.quovantis.musicplayer.updated.interfaces.IItemTouchHelperAdapter;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.utility.CircleImageView;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sahil-goel on 29/8/16.
 */
public class CurrentPlaylistAdapter extends RecyclerView.Adapter<CurrentPlaylistAdapter.ViewHolder> implements
        IItemTouchHelperAdapter {

    private ArrayList<SongDetailsModel> mQueueList;
    private Context mContext;
    private ICurrentPlaylistClickListener iCurrentPlaylistClickListener;

    public CurrentPlaylistAdapter(ArrayList<SongDetailsModel> mQueueList, Context mContext,
                                  ICurrentPlaylistClickListener iCurrentPlaylistClickListener) {
        this.mQueueList = mQueueList;
        this.mContext = mContext;
        this.iCurrentPlaylistClickListener = iCurrentPlaylistClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.current_playlist_single_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position1) {
        int pos = position1;
        final SongDetailsModel model = mQueueList.get(holder.getAdapterPosition());
        if (model != null) {
            String title = model.getSongTitle();
            String artist = model.getSongArtist();
            holder.mQueueSOngTitle.setText(title);
            holder.mQueueSongArtist.setText(artist);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    iCurrentPlaylistClickListener.onClick(holder.getAdapterPosition());
                }
            });
            LoadBitmapHelper.loadBitmap(mContext, holder.mImage, model.getSongThumbnailData());
        }
    }

    @Override
    public int getItemCount() {
        return mQueueList.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        iCurrentPlaylistClickListener.onSongsMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        iCurrentPlaylistClickListener.onSongRemove(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_queue_song_title)
        TextView mQueueSOngTitle;
        @BindView(R.id.iv_song_thumbnail)
        CircleImageView mImage;
        @BindView(R.id.tv_queue_song_artist)
        TextView mQueueSongArtist;
        @BindView(R.id.ll_drag_icon)
        LinearLayout mDragIconLL;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
