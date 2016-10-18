package com.quovantis.musicplayer.updated.ui.views.playlists;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.models.UserPlaylistModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sahil-goel on 30/8/16.
 */
public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    private final Uri mArtworkUri;
    private ArrayList<UserPlaylistModel> mUserPlaylistList;
    private Context mContext;
    private IPlaylistClickListener iPlaylistClickListener;

    public PlaylistAdapter(ArrayList<UserPlaylistModel> mUserPlaylistList, Context mContext, IPlaylistClickListener iPlaylistClickListener) {
        this.mUserPlaylistList = mUserPlaylistList;
        this.mContext = mContext;
        this.iPlaylistClickListener = iPlaylistClickListener;
        mArtworkUri = Uri.parse("content://media/external/audio/albumart");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_playlist_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final UserPlaylistModel model = mUserPlaylistList.get(position);
        final String title = model.getPlaylistName();
        final long tracks = model.getPlaylist() == null ? 0 : model.getPlaylist().size();
        holder.mPlaylistNameTV.setText(title);
        holder.mTotalTracksTV.setText(tracks + " tracks");
        if (tracks > 0)
            Glide.with(mContext).load(ContentUris.withAppendedId(mArtworkUri, model.getPlaylist().get(0).getAlbumId())).asBitmap().placeholder(R.drawable.music).into(holder.mPlaylistThumbnailIV);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iPlaylistClickListener.onClick(model, title);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                iPlaylistClickListener.onLongPress(model);
                return true;
            }
        });
        holder.mOptionsLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iPlaylistClickListener.onLongPress(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUserPlaylistList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_playlist_thumbnail)
        ImageView mPlaylistThumbnailIV;
        @BindView(R.id.tv_playlist_name)
        TextView mPlaylistNameTV;
        @BindView(R.id.tv_playlist_tracks)
        TextView mTotalTracksTV;
        @BindView(R.id.ll_action)
        LinearLayout mOptionsLL;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    interface IPlaylistClickListener {
        void onClick(UserPlaylistModel model, String name);

        void onLongPress(UserPlaylistModel model);
    }
}
