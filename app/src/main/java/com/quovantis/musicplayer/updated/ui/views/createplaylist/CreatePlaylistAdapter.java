package com.quovantis.musicplayer.updated.ui.views.createplaylist;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.interfaces.IAddToExistingPlaylistClickListener;
import com.quovantis.musicplayer.updated.models.UserPlaylistModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sahil-goel on 7/9/16.
 */
public class CreatePlaylistAdapter extends RecyclerView.Adapter<CreatePlaylistAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<UserPlaylistModel> mList;
    private IAddToExistingPlaylistClickListener iAddToExistingPlaylistClickListener;
    private Uri mArtworkUri;

    public CreatePlaylistAdapter(Context mContext, ArrayList<UserPlaylistModel> mList,
                                 IAddToExistingPlaylistClickListener iAddToExistingPlaylistClickListener) {
        this.mContext = mContext;
        mArtworkUri = Uri.parse("content://media/external/audio/albumart");
        this.mList = mList;
        this.iAddToExistingPlaylistClickListener = iAddToExistingPlaylistClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_add_to_playlist_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final UserPlaylistModel model = mList.get(position);
        holder.mPlaylistTV.setText(model.getPlaylistName());
        long tracks = model.getPlaylist() == null ? 0 : model.getPlaylist().size();
        if (tracks > 0)
            Glide.with(mContext).load(ContentUris.withAppendedId(mArtworkUri, model.getPlaylist().get(0).getAlbumId())).asBitmap().placeholder(R.drawable.music).into(holder.mPlaylistThumbnaiIIV);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iAddToExistingPlaylistClickListener.onAddToExistingPlaylist(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_playlist_thumbnail)
        ImageView mPlaylistThumbnaiIIV;
        @BindView(R.id.tv_playlist)
        TextView mPlaylistTV;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
