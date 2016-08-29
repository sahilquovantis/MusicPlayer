package com.quovantis.musicplayer.updated.ui.views.current_playlist;

import com.quovantis.musicplayer.updated.helper.MusicHelper;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.ArrayList;

/**
 * Created by sahil-goel on 29/8/16.
 */
public class CurrentPlaylistInteractorImp implements ICurrentPlaylistInteractor {

    @Override
    public void getCurrentPlaylist(ICurrentPlaylistInteractor.Listener listener) {
         listener.onUpdateUI(MusicHelper.getInstance().getCurrentPlaylist());
    }
}
