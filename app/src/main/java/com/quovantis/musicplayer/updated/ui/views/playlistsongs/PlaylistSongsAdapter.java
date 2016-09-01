package com.quovantis.musicplayer.updated.ui.views.playlistsongs;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.helper.LoadBitmapHelper;
import com.quovantis.musicplayer.updated.interfaces.IPlaylistSongsClickListener;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sahil-goel on 30/8/16.
 */
public class PlaylistSongsAdapter extends RecyclerView.Adapter<PlaylistSongsAdapter.ViewHolder> {

    private ArrayList<SongDetailsModel> mList;
    private Context mContext;
    private IPlaylistSongsClickListener iPlaylistSongsClickListener;

    public PlaylistSongsAdapter(ArrayList<SongDetailsModel> mList, Context mContext, IPlaylistSongsClickListener iPlaylistSongsClickListener) {
        this.mList = mList;
        this.mContext = mContext;
        this.iPlaylistSongsClickListener = iPlaylistSongsClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_playlist_songs_row, parent, false);
        return new ViewHolder(view);    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final SongDetailsModel songDetailsModel = mList.get(position);
        holder.mSongTV.setText(songDetailsModel.getSongTitle());
        holder.mSongArtistTV.setText(songDetailsModel.getSongArtist());
        LoadBitmapHelper.loadBitmap(mContext, holder.mSongThumbnailIV, songDetailsModel.getSongThumbnailData());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iPlaylistSongsClickListener.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_song_thumbnail)
        ImageView mSongThumbnailIV;
        @BindView(R.id.tv_song_name)
        TextView mSongTV;
        @BindView(R.id.tv_song_artist)
        TextView mSongArtistTV;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
