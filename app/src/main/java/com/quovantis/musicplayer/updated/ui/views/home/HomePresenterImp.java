package com.quovantis.musicplayer.updated.ui.views.home;

import android.app.Activity;
import android.content.Context;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.List;

/**
 * Created by sahil-goel on 11/9/16.
 */
public class HomePresenterImp implements IHomePresenter, IHomeInteractor.Listener {
    private IHomeView iHomeView;
    private IHomeInteractor iHomeInteractor;

    public HomePresenterImp(IHomeView iHomeView) {
        this.iHomeView = iHomeView;
        iHomeInteractor = new HomeInteractorImp();
    }

    @Override
    public void onDestroy() {
        iHomeView = null;
    }

    @Override
    public void getSongsListFromFolder(String path, boolean isClearQueue, boolean isPlaythisSong, Context context, Activity activity) {
        if (iHomeView != null)
            iHomeView.showProgressDialog();
        iHomeInteractor.getSongsListFromFolder(path, isClearQueue, isPlaythisSong, context, activity, this);
    }

    @Override
    public void onSuccess(List<SongDetailsModel> list, boolean isClearQueue, boolean isPlaythisSong) {
        if (iHomeView != null) {
            iHomeView.onSuccessGettingSongsListForAddingToQueue(list, isClearQueue, isPlaythisSong);
            iHomeView.hideProgressDialog();
        }
    }
}
