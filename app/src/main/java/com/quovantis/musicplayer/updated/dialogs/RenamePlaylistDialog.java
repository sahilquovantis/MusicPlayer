package com.quovantis.musicplayer.updated.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.models.UserPlaylistModel;
import com.quovantis.musicplayer.updated.ui.views.playlists.PlaylistFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Show Dialog For Renaming Playlist Name
 */

public class RenamePlaylistDialog extends AlertDialog {

    @BindView(R.id.et_playlist_name)
    EditText mPlaylistNameET;
    private IRenamePlaylist iListener;
    private UserPlaylistModel mPlaylistModel;

    public RenamePlaylistDialog(Context context, UserPlaylistModel model, IRenamePlaylist iRenamePlaylist) {
        super(context);
        mPlaylistModel = model;
        iListener = iRenamePlaylist;
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
        setContentView(R.layout.custom_rename_playlist_dialog);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_cancel, R.id.tv_rename_playlist})
    public void onClick(TextView view) {
        int id = view.getId();
        switch (id) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_rename_playlist:
                String playlistName = mPlaylistNameET.getText().toString();
                if (playlistName.trim().length() > 0) {
                    iListener.onRenamePlaylist(mPlaylistModel, playlistName);
                    dismiss();
                } else
                    mPlaylistNameET.setError("Can not be blanked");

                break;
        }
    }

    public void setPlaylistName(String name) {
        mPlaylistNameET.setText(name);
        mPlaylistNameET.extendSelection(mPlaylistNameET.getText().length());
    }

    public interface IRenamePlaylist {
        void onRenamePlaylist(UserPlaylistModel model, String newName);
    }
}
