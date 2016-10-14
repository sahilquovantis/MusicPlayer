package com.quovantis.musicplayer.updated.interfaces;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.models.SongPathModel;
import com.quovantis.musicplayer.updated.models.UserPlaylistModel;

/**
 * Options Dialog Listener For Folders, Songs and Playlist
 */
public interface IQueueOptionsDialog {
    interface onSongClickListener {
        void onClickFromSpecificSongOptionsDialog(SongDetailsModel model, boolean isClearQueue, boolean isPlaythisSong);

        void onAddToPlaylist(SongDetailsModel model);
    }

    interface onFolderClickListener {
        void onClickFromFolderOptionsDialog(SongPathModel model, boolean isClearQueue, boolean isPlaythisSong);

        void onAddToPlaylist(SongPathModel model);
    }
}
