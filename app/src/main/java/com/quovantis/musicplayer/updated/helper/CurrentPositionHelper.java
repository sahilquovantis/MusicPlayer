package com.quovantis.musicplayer.updated.helper;

import com.quovantis.musicplayer.updated.constants.AppMusicKeys;

import java.util.Random;

/**
 * This Class helps in maintaining current Position of the queue.
 */
class CurrentPositionHelper {

    private int mCurrentPosition;

    CurrentPositionHelper() {
        mCurrentPosition = 0;
    }

    int getCurrentPosition() {
        return mCurrentPosition;
    }

    void setCurrentPosition(int mCurrentPosition) {
        this.mCurrentPosition = mCurrentPosition;
    }

    int getPreviousSong(int size) {
        if (mCurrentPosition == 0 || mCurrentPosition < 0)
            mCurrentPosition = size - 1;
        else
            mCurrentPosition -= 1;
        return mCurrentPosition;
    }

    int getNextSong(int size) {
        if (AppMusicKeys.REPEAT_STATE == AppMusicKeys.REPEAT_ON) {
            return mCurrentPosition;
        }
        if (AppMusicKeys.SHUFFLE_STATE == AppMusicKeys.SHUFFLE_ON && size > 0) {
            Random random = new Random();
            mCurrentPosition = random.nextInt(size);
            return mCurrentPosition;
        }
        if (mCurrentPosition == size - 1 || mCurrentPosition >= size)
            mCurrentPosition = 0;
        else
            mCurrentPosition += 1;
        return mCurrentPosition;
    }
}
