package com.quovantis.musicplayer.updated.ui.views.home;

import com.quovantis.musicplayer.updated.models.SongDetailsModel;

import java.util.List;

/**
 * Created by sahil-goel on 11/9/16.
 */
public interface IHomeView {
    void showProgressDialog();

    void hideProgressDialog();

    void onSuccessGettingSongsListForAddingToQueue(List<SongDetailsModel> list, boolean isClearQueue, boolean isPlaythisSong);
}
