package com.quovantis.musicplayer.updated.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.helper.LoggerHelper;
import com.quovantis.musicplayer.updated.interfaces.IOnCreatePlaylistDialogListener;
import com.quovantis.musicplayer.updated.utility.KeyboardUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Custom Playlist Dialog
 */
public class CreatePlaylistDialog extends AlertDialog implements View.OnClickListener {

    @BindView(R.id.et_playlist_name)
    EditText mPlaylistNameET;
    @BindView(R.id.tv_cancel)
    TextView mCancelTV;
    @BindView(R.id.tv_create_playlist)
    TextView mCreatePlaylistTV;
    private IOnCreatePlaylistDialogListener iOnCreatePlaylistListener;

    public CreatePlaylistDialog(Context context, IOnCreatePlaylistDialogListener playlistDialog) {
        super(context);
        iOnCreatePlaylistListener = playlistDialog;
        setDialogTitle();
        setCancelable(true);
    }

    private void setDialogTitle() {
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_playlist_dialog);
        ButterKnife.bind(this);
        mCancelTV.setOnClickListener(this);
        mCreatePlaylistTV.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_create_playlist:
                String playlistName = mPlaylistNameET.getText().toString();
                if (playlistName.trim().length() > 0) {
                    iOnCreatePlaylistListener.onCreatePlaylist(playlistName.trim());
                    dismiss();
                } else
                    mPlaylistNameET.setError("Can not be blanked");
                break;
        }
    }
}
