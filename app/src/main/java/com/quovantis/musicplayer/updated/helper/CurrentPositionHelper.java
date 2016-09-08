package com.quovantis.musicplayer.updated.helper;

import android.util.Log;

import com.quovantis.musicplayer.updated.utility.Utils;

import java.util.Random;

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
        if (Utils.SHUFFLE_STATE == Utils.SHUFFLE_ON && size > 0) {
            Random random = new Random();
            mCurrentPosition = random.nextInt(size);
            Log.d("Training", "Random :  " + mCurrentPosition);
            return mCurrentPosition;
        }
        if (mCurrentPosition == size - 1 || mCurrentPosition >= size)
            mCurrentPosition = 0;
        else
            mCurrentPosition += 1;
        return mCurrentPosition;
    }
}
