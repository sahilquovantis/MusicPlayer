package com.quovantis.musicplayer.updated.helper;

/**
 * Created by sahil-goel on 6/9/16.
 */
public class CurrentPositionHelper {

    private int mCurrentPosition;

    public CurrentPositionHelper() {
        mCurrentPosition = 0;
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    public void setCurrentPosition(int mCurrentPosition) {
        this.mCurrentPosition = mCurrentPosition;
    }

    public int getPreviousSong(int size) {
        if (mCurrentPosition == 0 || mCurrentPosition < 0)
            mCurrentPosition = size - 1;
        else
            mCurrentPosition -= 1;
        return mCurrentPosition;
    }

    public int getNextSong(int size) {
        if (mCurrentPosition == size - 1 || mCurrentPosition >= size)
            mCurrentPosition = 0;
        else
            mCurrentPosition += 1;
        return mCurrentPosition;
    }
}
