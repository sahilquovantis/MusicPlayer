package com.quovantis.musicplayer.updated.ui.views.home;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sahil-goel on 11/9/16.
 */
public class HomeAdapter extends FragmentStatePagerAdapter{
    private List<Fragment> mFragments;
    private List<String> mTitles;

    public HomeAdapter(FragmentManager fm) {
        super(fm);
        mFragments = new ArrayList<>();
        mTitles = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    public void addFragments(Fragment fragment, String title) {
        mFragments.add(fragment);
        mTitles.add(title);
    }

    public String getPageTitle(int pos) {
        return mTitles.get(pos);
    }

    public Fragment getFirstItem(int pos){
        return mFragments.get(pos);
    }
}
