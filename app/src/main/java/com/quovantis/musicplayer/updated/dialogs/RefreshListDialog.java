package com.quovantis.musicplayer.updated.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.quovantis.musicplayer.R;

/**
 * Created by sahil-goel on 23/8/16.
 */
public class RefreshListDialog {

    private Context mContext;
    private AlertDialog mDialog;
    private TextView mDialogTitleTV;
    private ProgressBar mProgressBar;
    private TextView mSongsFetchedTV;

    public RefreshListDialog(Context mContext) {
        this.mContext = mContext;
    }

    public void showProgressDialog() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.progress_dialog, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        builder.setView(view);
        mDialogTitleTV = (TextView) view.findViewById(R.id.tv_dialog_title);
        mProgressBar = (ProgressBar) view.findViewById(R.id.pb_update_progress);
        mSongsFetchedTV = (TextView) view.findViewById(R.id.tv_songs_fetched);
        mDialog = builder.create();
        mDialog.show();
    }

    public void cancelProgressDialog() {
        if (mDialog != null)
            mDialog.cancel();
    }

    public void updateProgress(int progress, int total) {
        mProgressBar.setMax(total);
        mProgressBar.setProgress(progress);
    }

    public void updateFetchedSongs(int value, int total) {
        mSongsFetchedTV.setText("Songs Fetched : " + value + "/" + total);
    }
}
