package com.quovantis.musicplayer.updated.ui.views.playlistsongslist;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.helper.RecyclerViewAnimationHelper;
import com.quovantis.musicplayer.updated.interfaces.IItemTouchHelperAdapter;
import com.quovantis.musicplayer.updated.interfaces.IMusicListClickListener;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.utility.CircleImageView;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sahil-goel on 14/10/16.
 */
class PlaylistSongsAdapter extends RecyclerView.Adapter<PlaylistSongsAdapter.ViewHolder> implements
        IItemTouchHelperAdapter {
    private final Uri mArtworkUri;
    private Context mContext;
    private IMusicListClickListener iMusicListClickListener;
    private ArrayList<SongDetailsModel> mSongList;

    PlaylistSongsAdapter(Context context, IMusicListClickListener iMusicListClickListener, ArrayList<SongDetailsModel> mSongList) {
        mContext = context;
        mArtworkUri = Uri.parse("content://media/external/audio/albumart");
        this.iMusicListClickListener = iMusicListClickListener;
        this.mSongList = mSongList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_song_list_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final SongDetailsModel songDetailsModel = mSongList.get(position);
        String title = songDetailsModel.getSongTitle();
        holder.mSongTV.setMaxLines(1);
        holder.mSongTV.setText(title);
        holder.mSongTV.setEllipsize(TextUtils.TruncateAt.END);
        holder.mSongArtistTV.setText(songDetailsModel.getSongArtist());
        Glide.with(mContext).load(ContentUris.withAppendedId(mArtworkUri, songDetailsModel.getAlbumId())).asBitmap().placeholder(R.drawable.music).into(holder.mSongThumbnailIV);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iMusicListClickListener.onMusicListClick(position);
            }
        });
        holder.mPopUpMenuLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iMusicListClickListener.onActionOverFlowClick(songDetailsModel);
            }
        });
        RecyclerViewAnimationHelper.animate(mContext, holder);
    }

    @Override
    public int getItemCount() {
        if (mSongList != null && !mSongList.isEmpty()) {
            return mSongList.size();
        } else {
            return 0;
        }
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mSongList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        mSongList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemMoveCompleted() {
        iMusicListClickListener.onItemMovedCompleted();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_song_thumbnail)
        CircleImageView mSongThumbnailIV;
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

    interface IMusicListClickListener {
        void onMusicListClick(int pos);

        void onItemMovedCompleted();

        void onActionOverFlowClick(SongDetailsModel model);
    }
}
