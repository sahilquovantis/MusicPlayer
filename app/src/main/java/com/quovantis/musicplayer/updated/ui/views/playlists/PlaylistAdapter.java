package com.quovantis.musicplayer.updated.ui.views.playlists;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.helper.LoadBitmapHelper;
import com.quovantis.musicplayer.updated.interfaces.IPlaylistClickListener;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.models.UserPlaylistModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sahil-goel on 30/8/16.
 */
public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    private ArrayList<UserPlaylistModel> mUserPlaylistList;
    private Context mContext;
    private IPlaylistClickListener iPlaylistClickListener;

    public PlaylistAdapter(ArrayList<UserPlaylistModel> mUserPlaylistList, Context mContext, IPlaylistClickListener iPlaylistClickListener) {
        this.mUserPlaylistList = mUserPlaylistList;
        this.mContext = mContext;
        this.iPlaylistClickListener = iPlaylistClickListener;
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
        long tracks = model.getPlaylist() == null ? 0 : model.getPlaylist().size();
        holder.mPlaylistNameTV.setText(title);
        holder.mTotalTracksTV.setText(tracks + " tracks");
        if (tracks > 0)
            LoadBitmapHelper.loadBitmap(mContext, holder.mPlaylistThumbnailIV, model.getPlaylist().get(0).getSongThumbnailData());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iPlaylistClickListener.onClick(model.getPlaylistId(), title);
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

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
