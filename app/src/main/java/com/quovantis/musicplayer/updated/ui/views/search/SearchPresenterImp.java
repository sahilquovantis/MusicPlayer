package com.quovantis.musicplayer.updated.ui.views.search;

import android.content.Context;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sahil-goel on 2/9/16.
 */
public class SearchPresenterImp implements ISearchPresenter, ISearchInteractor.Listener {

    private ISearchView iSearchView;
    private ISearchInteractor iSearchInteractor;

    public SearchPresenterImp(ISearchView iSearchView) {
        this.iSearchView = iSearchView;
        iSearchInteractor = new SearchInteractorImp();
    }

    @Override
    public void fetchSongsList() {
        if (iSearchView != null)
            iSearchView.onShowProgress();
        iSearchInteractor.fetchSongsList(this);
    }

    @Override
    public void onFetchingList(List<SongDetailsModel> list) {
        if (iSearchView != null) {
            iSearchView.onHideProgress();
            iSearchView.onFetchingAllSongList(list);
        }
    }

    @Override
    public void filterResults(List<SongDetailsModel> list, String query) {
        if (iSearchView != null) {
            iSearchView.onShowProgress();
            ArrayList<SongDetailsModel> filterList = new ArrayList<>();
            if (!list.isEmpty()) {
                for (SongDetailsModel model : list) {
                    String title = model.getSongTitle().toLowerCase();
                    query = query.toLowerCase();
                    if (title.contains(query)) {
                        filterList.add(model);
                    }
                }
            }
            iSearchView.onUpdateUI(filterList, query);
            iSearchView.onHideProgress();
        }
    }

    @Override
    public void onDestroy() {
        iSearchView = null;
    }
}
