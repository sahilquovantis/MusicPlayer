package com.quovantis.musicplayer.updated.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.interfaces.IQueueOptionsDialog;
import com.quovantis.musicplayer.updated.models.UserPlaylistModel;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sahil-goel on 12/10/16.
 */

public class PlaylistOptionsDialog extends AlertDialog {

    private UserPlaylistModel mPlaylistModel;
    private IQueueOptionsDialog.onPlaylistClickListener iListener;

    public PlaylistOptionsDialog(Context context, UserPlaylistModel model, IQueueOptionsDialog.onPlaylistClickListener listener) {
        super(context);
        iListener = listener;
        mPlaylistModel = model;
        setDialogTitle();
        setCancelable(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_playlist_options_dialog);
        ButterKnife.bind(this);
        TextView title = (TextView) findViewById(R.id.tv_dialog_title);
        title.setText(mPlaylistModel.getPlaylistName());
    }

    @OnClick({R.id.ll_add_to_queue, R.id.ll_rename_playlist, R.id.ll_delete_playlist, R.id.ll_play_and_clear})
    public void onClick(LinearLayout linearLayout) {
        int id = linearLayout.getId();
        switch (id) {
            case R.id.ll_add_to_queue:
                iListener.onClickFromPlaylistOptionsDialog(mPlaylistModel, false, false);
                dismiss();
                break;
            case R.id.ll_rename_playlist:
                iListener.onRename(mPlaylistModel);
                dismiss();
                break;
            case R.id.ll_delete_playlist:
                iListener.onDelete(mPlaylistModel);
                dismiss();
                break;
            case R.id.ll_play_and_clear:
                iListener.onClickFromPlaylistOptionsDialog(mPlaylistModel, true, true);
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
}
