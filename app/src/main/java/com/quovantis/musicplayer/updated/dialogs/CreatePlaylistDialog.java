package com.quovantis.musicplayer.updated.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.interfaces.IOnCreatePlaylistDialog;

/**
 * Created by sahil-goel on 29/8/16.
 */
public class CreatePlaylistDialog {
    public static void showDialog(Context context, final IOnCreatePlaylistDialog iOnCreatePlaylistDialog) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_playlist_dialog, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        final EditText name = (EditText) view.findViewById(R.id.et_playlist_name);
        TextView createPlaylist = (TextView) view.findViewById(R.id.tv_create_playlist);
        TextView cancelDialog = (TextView) view.findViewById(R.id.tv_cancel);
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        createPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().trim().length() > 0) {
                    iOnCreatePlaylistDialog.onCreatePlaylist(name.getText().toString().trim());
                    dialog.dismiss();
                } else
                    name.setError("Can not be blanked");
            }
        });
    }
}
