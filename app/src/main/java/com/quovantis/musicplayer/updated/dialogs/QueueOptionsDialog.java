package com.quovantis.musicplayer.updated.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.helper.MusicHelper;
import com.quovantis.musicplayer.updated.interfaces.IQueueOptionsDialog;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;

/**
 * Created by sahil-goel on 25/8/16.
 */
public class QueueOptionsDialog {
    public static void showDialog(Context context, final SongDetailsModel model, final IQueueOptionsDialog listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.queue_options_dialog, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        LinearLayout clearAndPlay = (LinearLayout) view.findViewById(R.id.ll_play_and_clear);
        LinearLayout addToQueue = (LinearLayout) view.findViewById(R.id.ll_add_to_queue);
        addToQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicHelper.getInstance().addSongToPlaylist(model, false, false);
                dialog.dismiss();
            }
        });
        clearAndPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicHelper.getInstance().addSongToPlaylist(model, true, true);
                listener.onClearAndPlay(model.getSongID());
                dialog.dismiss();
            }
        });
    }
}