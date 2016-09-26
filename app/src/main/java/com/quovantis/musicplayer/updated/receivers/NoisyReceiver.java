package com.quovantis.musicplayer.updated.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.quovantis.musicplayer.updated.services.MusicService;
import com.quovantis.musicplayer.updated.utility.Utils;

/**
 * Created by sahil-goel on 10/9/16.
 */
public class NoisyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      /*  if (intent != null) {
            Log.d("Training", "On Noisy Receiver");
            Intent intent1 = new Intent(context, MusicService.class);
            intent1.setAction(Utils.INTENT_ACTION_PAUSE);
            context.startService(intent1);
        }*/
    }
}
