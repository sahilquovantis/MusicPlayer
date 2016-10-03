package com.quovantis.musicplayer.updated.ui.views.fullscreenmusiccontrols;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.helper.MusicHelper;
import com.quovantis.musicplayer.updated.interfaces.ICurrentPlaylistClickListener;
import com.quovantis.musicplayer.updated.interfaces.IItemTouchHelperAdapter;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.utility.CircleImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.claucookie.miniequalizerlibrary.EqualizerView;

/**
 * Created by sahil-goel on 29/8/16.
 */
public class CurrentPlaylistAdapter extends RecyclerView.Adapter<CurrentPlaylistAdapter.ViewHolder> implements
        IItemTouchHelperAdapter {

    private boolean mIsPlaying;
    private Context mContext;
    private ICurrentPlaylistClickListener iCurrentPlaylistClickListener;
    private Uri mArtworkUri;

    public CurrentPlaylistAdapter(Context mContext,
                                  ICurrentPlaylistClickListener iCurrentPlaylistClickListener) {
        mArtworkUri = Uri.parse("content://media/external/audio/albumart");
        this.mContext = mContext;
        mIsPlaying = false;
        this.iCurrentPlaylistClickListener = iCurrentPlaylistClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.current_playlist_single_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position1) {
        final SongDetailsModel model = MusicHelper.getInstance().getCurrentPlaylist().get(holder.getAdapterPosition());
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
            Glide.with(mContext).load(ContentUris.withAppendedId(mArtworkUri, model.getAlbumId())).asBitmap().placeholder(R.drawable.music).into(holder.mImage);
            if (position1 == MusicHelper.getInstance().getCurrentPosition()) {
                holder.mEqualizer.setVisibility(View.VISIBLE);
                if (mIsPlaying)
                    holder.mEqualizer.animateBars();
                else
                    holder.mEqualizer.stopBars();
            } else {
                holder.mEqualizer.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return MusicHelper.getInstance().getCurrentPlaylist().size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        iCurrentPlaylistClickListener.onSongsMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        iCurrentPlaylistClickListener.onSongRemove(position);
    }

    public boolean isPlaying() {
        return mIsPlaying;
    }

    public void setIsPlaying(boolean mIsPlaying) {
        this.mIsPlaying = mIsPlaying;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_queue_song_title)
        TextView mQueueSOngTitle;
        @BindView(R.id.iv_song_thumbnail)
        CircleImageView mImage;
        @BindView(R.id.tv_queue_song_artist)
        TextView mQueueSongArtist;
        @BindView(R.id.equalizer_view)
        EqualizerView mEqualizer;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
