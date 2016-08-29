package com.quovantis.musicplayer.updated.ui.views.current_playlist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sahil-goel on 29/8/16.
 */
public class CurrentPlaylistAdapter extends RecyclerView.Adapter<CurrentPlaylistAdapter.ViewHolder>{

    private ArrayList<SongDetailsModel> mQueueList;
    private Context mContext;

    public CurrentPlaylistAdapter(ArrayList<SongDetailsModel> mQueueList, Context mContext) {
        this.mQueueList = mQueueList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.current_playlist_single_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SongDetailsModel model = mQueueList.get(position);
        String title = model.getSongTitle();
        String artist = model.getSongArtist();
        holder.mQueueSOngTitle.setText(title);
        holder.mQueueSongArtist.setText(artist);
    }

    @Override
    public int getItemCount() {
        return mQueueList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_queue_song_title)
        TextView mQueueSOngTitle;
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
