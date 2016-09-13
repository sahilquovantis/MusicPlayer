package com.quovantis.musicplayer.updated.ui.views.allsongs;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.helper.RecyclerViewAnimationHelper;
import com.quovantis.musicplayer.updated.interfaces.IMusicListClickListener;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.utility.CircleImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sahil-goel on 13/9/16.
 */
public class AllSongsAdapter extends RecyclerView.Adapter<AllSongsAdapter.ViewHolder> {
    private Context mContext;
    private IMusicListClickListener iMusicListClickListener;
    private Cursor mCursor;
    private String mQuery;
    private Uri mArtworkUri;

    public AllSongsAdapter(Context context, IMusicListClickListener iMusicListClickListener, Cursor mCursor) {
        mContext = context;
        mArtworkUri = Uri.parse("content://media/external/audio/albumart");
        this.iMusicListClickListener = iMusicListClickListener;
        this.mCursor = mCursor;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_song_list_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int pos = position;
        mCursor.moveToPosition(position);
        final String title = mCursor.getString(2);
        final String id = mCursor.getString(0);
        final String artist = mCursor.getString(3);
        final String path = mCursor.getString(1);
        long albumId = mCursor.getLong(4);
        String query = mQuery;
        setSpan(title, query, holder.mSongTV);
        holder.mSongArtistTV.setText(artist);
        Glide.with(mContext).load(ContentUris.withAppendedId(mArtworkUri, albumId)).asBitmap().placeholder(R.drawable.music).into(holder.mSongThumbnailIV);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iMusicListClickListener.onMusicListClick(pos);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                SongDetailsModel songDetailsModel = new SongDetailsModel();
                songDetailsModel.setSongArtist(artist);
                songDetailsModel.setSongID(id);
                songDetailsModel.setSongTitle(title);
                songDetailsModel.setSongPath(path);
                iMusicListClickListener.onActionOverFlowClick(songDetailsModel);
                return true;
            }
        });
        holder.mPopUpMenuLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SongDetailsModel songDetailsModel = new SongDetailsModel();
                songDetailsModel.setSongArtist(artist);
                songDetailsModel.setSongID(id);
                songDetailsModel.setSongTitle(title);
                songDetailsModel.setSongPath(path);
                iMusicListClickListener.onActionOverFlowClick(songDetailsModel);
            }
        });
        RecyclerViewAnimationHelper.animate(mContext, holder);
    }

    private void setSpan(String title, String query, TextView mSongTV) {
        mSongTV.setMaxLines(1);
        if (query != null && query.trim().length() > 0) {
            Spannable spannable = new SpannableString(title);
            int start = title.toLowerCase().indexOf(query.toLowerCase());
            int end = start + query.length();
            spannable.setSpan(new ForegroundColorSpan(Color.BLUE), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mSongTV.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            mSongTV.setText(spannable);
            mSongTV.setSelected(true);
            mSongTV.setSingleLine();
        } else {
            mSongTV.setText(title);
            mSongTV.setEllipsize(TextUtils.TruncateAt.END);
        }
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void setList(List<SongDetailsModel> list) {
        //  mSongList.clear();
        // mSongList.addAll(list);
    }

    public void setQuery(String query) {
        mQuery = query;
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
}
