package com.quovantis.musicplayer.updated.ui.views.allsongs;

import android.app.Activity;
import android.content.Context;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.ArrayList;
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
            iAllSongsView.onFetchingAllSongsList(list);
            iAllSongsView.hideProgress();
        }
    }

    @Override
    public void filterResults(List<SongDetailsModel> list1, String query) {
        if (iAllSongsView != null) {
            iAllSongsView.showProgress();
            ArrayList<SongDetailsModel> list = new ArrayList<>();
            if (!list1.isEmpty()) {
                for (SongDetailsModel model : list1) {
                    String title = model.getSongTitle().toLowerCase();
                    query = query.toLowerCase();
                    if (title.contains(query)) {
                        list.add(model);
                    }
                }
            }
            iAllSongsView.updateUi(list);
            iAllSongsView.hideProgress();
        }
    }
}
