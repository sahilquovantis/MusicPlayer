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
 * Show Dialog Options for songs which are in current queue.
 */

public class CurrentQueueOptionsDialog extends AlertDialog {

    private IOnCurrentQueueSongsDialogClickListener iListener;
    private SongDetailsModel mSongModel;
    private int mSongPosition;

    public CurrentQueueOptionsDialog(Context context, SongDetailsModel model, int pos, IOnCurrentQueueSongsDialogClickListener listener) {
        super(context);
        mSongModel = model;
        mSongPosition = pos;
        iListener = listener;
        setDialogTitle();
        setCancelable(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_current_queue_options_dialog);
        ButterKnife.bind(this);
        if (mSongModel != null) {
            TextView title = (TextView) findViewById(R.id.tv_dialog_title);
            title.setText(mSongModel.getSongTitle());
        }
    }

    private void setDialogTitle() {
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
    }

    @OnClick({R.id.ll_add_to_playlist, R.id.ll_remove_from_playlist})
    void onClick(LinearLayout view) {
        int id = view.getId();
        switch (id) {
            case R.id.ll_remove_from_playlist:
                iListener.onRemove(mSongPosition);
                dismiss();
                break;
            case R.id.ll_add_to_playlist:
                iListener.onAddToPlaylist(mSongModel);
                dismiss();
                break;
        }
    }

    public interface IOnCurrentQueueSongsDialogClickListener {
        void onRemove(int position);

        void onAddToPlaylist(SongDetailsModel model);
    }
}
