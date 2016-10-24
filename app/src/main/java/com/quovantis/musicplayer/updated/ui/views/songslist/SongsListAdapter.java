package com.quovantis.musicplayer.updated.ui.views.songslist;

import android.content.ContentUris;
import android.content.Context;
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
import com.quovantis.musicplayer.updated.interfaces.SectionIndexedTitle;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.utility.CircleImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter class for Songs list {@link com.quovantis.musicplayer.updated.ui.views.allsongs.AllSongsFragment}
 * <p> {@link SongsListActivity}
 */
public class SongsListAdapter extends RecyclerView.Adapter<SongsListAdapter.ViewHolder> implements SectionIndexedTitle {
    private final Uri mArtworkUri;
    private Context mContext;
    private IMusicListClickListener iMusicListClickListener;
    private ArrayList<SongDetailsModel> mSongList;
    private String mQuery;

    public SongsListAdapter(Context context, IMusicListClickListener iMusicListClickListener, ArrayList<SongDetailsModel> mSongList) {
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int pos = position;
        final SongDetailsModel songDetailsModel = mSongList.get(pos);
        String title = songDetailsModel.getSongTitle();
        String query = mQuery;
        setSpan(title, query, holder.mSongTV);
        holder.mSongArtistTV.setText(songDetailsModel.getSongArtist());
        Glide.with(mContext).load(ContentUris.withAppendedId(mArtworkUri, songDetailsModel.getAlbumId())).asBitmap().placeholder(R.drawable.music).into(holder.mSongThumbnailIV);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iMusicListClickListener.onMusicListClick(pos);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                iMusicListClickListener.onActionOverFlowClick(songDetailsModel);
                return true;
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
        if (mSongList != null && !mSongList.isEmpty()) {
            return mSongList.size();
        } else {
            return 0;
        }
    }

    public void setList(List<SongDetailsModel> list) {
        mSongList.clear();
        mSongList.addAll(list);
    }

    public void setQuery(String query) {
        mQuery = query;
    }

    @Override
    public String getSectionedTitle(int position) {
        if (!mSongList.isEmpty() && mSongList.size() > position) {
            return mSongList.get(position).getSongTitle().substring(0, 1);
        }
        return " ";
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
