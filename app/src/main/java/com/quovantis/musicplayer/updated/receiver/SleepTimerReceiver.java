package com.quovantis.musicplayer.updated.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.quovantis.musicplayer.updated.constants.AppKeys;
import com.quovantis.musicplayer.updated.helper.LoggerHelper;
import com.quovantis.musicplayer.updated.helper.MusicHelper;
import com.quovantis.musicplayer.updated.services.MusicService;

/**
 * Receiver Called when sleep timer is ended
 */

public class SleepTimerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equalsIgnoreCase(AppKeys.SLEEP_TIMER_ACTION)) {
            LoggerHelper.debug("Sleep Timer Receiver Called");
            Intent intent1 = new Intent();
            intent1.setAction(AppKeys.CLOSE_MUSIC_ACTION);
            context.sendBroadcast(intent1);
        }
    }
}