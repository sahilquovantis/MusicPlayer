package com.quovantis.musicplayer.updated.helper;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.quovantis.musicplayer.R;

/**
 * Created by sahil-goel on 24/8/16.
 */
public class SongOptionsDialog {
    private View mDialogCustomView;
    private Dialog mBuilder;
    private WindowManager.LayoutParams mDialogLayoutParams;

    public SongOptionsDialog(Context context){
        mDialogCustomView = LayoutInflater.from(context).inflate(R.layout.custom_dialog, null);
        mDialogLayoutParams = new WindowManager.LayoutParams();
        mBuilder = new Dialog(context);
        mBuilder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialogLayoutParams.copyFrom(mBuilder.getWindow().getAttributes());
        mDialogLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mDialogLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        mDialogLayoutParams.gravity = Gravity.CENTER;
        mBuilder.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
    public void showDialog() {
        LinearLayout playFolder = (LinearLayout) mDialogCustomView.findViewById(R.id.ll_play_folder);
        LinearLayout addToQueueFolder = (LinearLayout) mDialogCustomView.findViewById(R.id.ll_add_folder_to_queue);
        playFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //iFolderClickListener.onLongClick(FOLDER_PLAY, id);
                mBuilder.cancel();
            }
        });
        addToQueueFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //iFolderClickListener.onLongClick(FOLDER_ADD_TO_QUEUE, id);
                mBuilder.cancel();
            }
        });
        mBuilder.setContentView(mDialogCustomView);
        mBuilder.show();
        mBuilder.getWindow().setAttributes(mDialogLayoutParams);
    }
}
