package com.quovantis.musicplayer.updated.ui.views.allsongs;

import android.app.Activity;
import android.content.Context;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.List;

/**
 * Created by sahil-goel on 11/9/16.
 */
public class AllSongsPresenterImp implements IAllSongsPresenter, IAllSongsInteractor.Listener {
    private IAllSongsView iAllSongsView;
    private IAllSongsInteractor iAllSongsInteractor;

    public AllSongsPresenterImp(IAllSongsView iAllSongsView) {
        this.iAllSongsView = iAllSongsView;
        iAllSongsInteractor = new AllSongsInteractorImp();
    }

    @Override
    public void getSongsList(Context context, Activity activity) {
        if (iAllSongsView != null)
            iAllSongsView.showProgress();
        iAllSongsInteractor.getSongsList(context, activity, this);
    }

    @Override
    public void onDestroy() {
        iAllSongsView = null;
    }

    @Override
    public void onGettingSongsList(List<SongDetailsModel> list) {
        if (iAllSongsView != null) {
            iAllSongsView.updateUi(list);
            iAllSongsView.hideProgress();
        }
    }
}
