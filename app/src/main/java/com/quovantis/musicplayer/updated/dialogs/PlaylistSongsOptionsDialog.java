package com.quovantis.musicplayer.updated.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Show Dialog Options for Songs which are saved as a playlist.
 */

public class PlaylistSongsOptionsDialog extends AlertDialog {

    private SongDetailsModel mSongModel;
    private OnPlaylistSongsDialogClickListener iListener;

    public PlaylistSongsOptionsDialog(Context context, SongDetailsModel mSongModel, OnPlaylistSongsDialogClickListener listener) {
        super(context);
        this.mSongModel = mSongModel;
        iListener = listener;
        setDialogTitle();
        setCancelable(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_playlist_songs_options_dialog);
        ButterKnife.bind(this);
        TextView dialogTitleTV = (TextView) findViewById(R.id.tv_dialog_title);
        dialogTitleTV.setText(mSongModel.getSongTitle());
    }

    @OnClick({R.id.ll_remove_from_playlist, R.id.ll_add_to_playlist, R.id.ll_add_to_queue, R.id.ll_play_and_clear})
    void onClick(LinearLayout view) {
        int id = view.getId();
        switch (id) {
            case R.id.ll_add_to_playlist:
                iListener.onAddToPlaylist(mSongModel);
                dismiss();
                break;
            case R.id.ll_play_and_clear:
                iListener.onClickFromSpecificSongOptionsDialog(mSongModel, true, true);
                dismiss();
                break;
            case R.id.ll_add_to_queue:
                iListener.onClickFromSpecificSongOptionsDialog(mSongModel, false, false);
                dismiss();
                break;
            case R.id.ll_remove_from_playlist:
                iListener.onRemove(mSongModel);
                dismiss();
                break;
        }
    }

    private void setDialogTitle() {
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
    }

    public interface OnPlaylistSongsDialogClickListener {
        void onClickFromSpecificSongOptionsDialog(SongDetailsModel model, boolean isClearQueue, boolean isPlayThisSong);

        void onRemove(SongDetailsModel model);

        void onAddToPlaylist(SongDetailsModel model);
    }
}