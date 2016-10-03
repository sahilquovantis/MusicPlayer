package com.quovantis.musicplayer.updated.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.interfaces.IQueueOptionsDialog;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.models.SongPathModel;

/**
 * Created by sahil-goel on 25/8/16.
 */
public class QueueOptionsDialog extends AlertDialog implements View.OnClickListener {

    private IQueueOptionsDialog.onFolderClickListener iOnFolderClickListener;
    private IQueueOptionsDialog.onSongClickListener iOnSongClickListener;
    private SongPathModel mFolder;
    private SongDetailsModel mSongModel;

    public QueueOptionsDialog(Context context, SongDetailsModel mSongModel, IQueueOptionsDialog.onSongClickListener listener) {
        super(context);
        iOnSongClickListener = listener;
        this.mSongModel = mSongModel;
        setDialogTitle();
        setCancelable(true);
    }

    public QueueOptionsDialog(Context context, SongPathModel pathModel, IQueueOptionsDialog.onFolderClickListener listener) {
        super(context);
        iOnFolderClickListener = listener;
        this.mFolder = pathModel;
        setDialogTitle();
        setCancelable(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.queue_options_dialog);
        findViewById(R.id.ll_play_and_clear).setOnClickListener(this);
        findViewById(R.id.ll_add_to_queue).setOnClickListener(this);
        findViewById(R.id.ll_add_to_playlist).setOnClickListener(this);
        TextView dialogTitle = (TextView) findViewById(R.id.tv_dialog_title);
        if (mFolder != null) {
            dialogTitle.setText(mFolder.getDirectory());
        } else if (mSongModel != null) {
            dialogTitle.setText(mSongModel.getSongTitle());
        }
    }

    private void setDialogTitle() {
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ll_add_to_playlist:
                if (mSongModel != null) {
                    iOnSongClickListener.onAddToPlaylist(mSongModel);
                } else if (mFolder != null) {
                    iOnFolderClickListener.onAddToPlaylist(mFolder);
                }
                dismiss();
                break;
            case R.id.ll_play_and_clear:
                if (mSongModel != null) {
                    iOnSongClickListener.onClickFromSpecificSongOptionsDialog(mSongModel, true, true);
                } else if (mFolder != null) {
                    iOnFolderClickListener.onClickFromFolderOptionsDialog(mFolder, true, true);
                }
                dismiss();
                break;
            case R.id.ll_add_to_queue:
                if (mSongModel != null) {
                    iOnSongClickListener.onClickFromSpecificSongOptionsDialog(mSongModel, false, false);
                } else if (mFolder != null) {
                    iOnFolderClickListener.onClickFromFolderOptionsDialog(mFolder, false, false);
                }
                dismiss();
                break;
        }
    }
}
