package com.quovantis.musicplayer.updated.ui.views.songslist;

import android.app.Activity;
import android.content.Context;

import com.quovantis.musicplayer.updated.interfaces.ICommonKeys;
import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.ArrayList;

/**
 * Created by sahil-goel on 24/8/16.
 */
public class SongsPresenterImp implements ISongsPresenter, ISongsInteractor.Listener {
    private ISongsView iSongsView;
    private ISongsInteractor iSongsInteractor;

    public SongsPresenterImp(Context context, ISongsView iSongsView) {
        this.iSongsView = iSongsView;
        iSongsInteractor = new SongsInteractorImp(context);
    }

    @Override
    public void updateUI(long id, String action, String path, Activity activity) {
        if (iSongsView != null) {
            iSongsView.onShowProgress();
            if (action.equalsIgnoreCase(ICommonKeys.PLAYLIST_ACTION))
                iSongsInteractor.getSongsList(id, action, this);
            else if (action.equalsIgnoreCase(ICommonKeys.FOLDERS_ACTION))
                iSongsInteractor.getSongsList(path, action, this, activity);
        }
    }

    @Override
    public void onUpdateSongsList(ArrayList<SongDetailsModel> list) {
        if (iSongsView != null) {
            if (list == null || list.isEmpty())
                iSongsView.onEmptyList();
            else
                iSongsView.onUpdateUI(list);
            iSongsView.onHideProgres();
        }
    }

    @Override
    public void onDestroy() {
        iSongsView = null;
    }
}
