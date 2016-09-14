package com.quovantis.musicplayer.updated.interfaces;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;
import com.quovantis.musicplayer.updated.models.SongPathModel;

/**
 * Created by sahil-goel on 26/8/16.
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
