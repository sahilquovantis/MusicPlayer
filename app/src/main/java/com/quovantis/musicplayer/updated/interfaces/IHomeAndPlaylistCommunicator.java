package com.quovantis.musicplayer.updated.interfaces;

import com.quovantis.musicplayer.updated.models.SongPathModel;
import com.quovantis.musicplayer.updated.models.UserPlaylistModel;

/**
 * Created by sahil-goel on 12/10/16.
 */

public interface IHomeAndPlaylistCommunicator {
    void onOptionsDialogClickFromPlaylist(UserPlaylistModel model, boolean isClearQueue, boolean isPlaythisSong);
}
