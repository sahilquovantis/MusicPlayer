package com.quovantis.musicplayer.updated.helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.NotificationCompat;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.services.MusicService;
import com.quovantis.musicplayer.updated.ui.views.folders.FoldersActivity;
import com.quovantis.musicplayer.updated.ui.views.fullscreenmusiccontrols.FullScreenMusic;
import com.quovantis.musicplayer.updated.utility.Utils;

/**
 * @author sahil-goel
 *         This class creates and cancels the Notification {@link MusicService}
 */
public class NotificationHelper {

    private Context mContext;
    private NotificationManager mNotificationManager;

    public NotificationHelper(Context context) {
        mContext = context;
    }

    /**
     * Create the Notification for Current Song
     *
     * @param mCurrentMetaData Current Song MEdiaMetaData
     * @param token            MediaSession Token
     * @param iconForPlayPause
     * @param playPause
     * @param action
     */
    public Notification createNotification(MediaMetadataCompat mCurrentMetaData, MediaSessionCompat.Token token,
                                           int iconForPlayPause, String playPause, String action) {
        if (mCurrentMetaData != null && token != null) {
            String title = mCurrentMetaData.getDescription().getTitle().toString();
            String artist = mCurrentMetaData.getDescription().getSubtitle().toString();
            Bitmap image = mCurrentMetaData.getDescription().getIconBitmap();

            NotificationCompat.MediaStyle style = new NotificationCompat.MediaStyle();
            style.setMediaSession(token);

            Intent showActivityIntent = new Intent(mContext, FoldersActivity.class);
          //  showActivityIntent.setAction(Utils.LAUNCHED_FROM_NOTIFICATION);
            showActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent showActivityPendingIntent = PendingIntent.getActivity(mContext, 1, showActivityIntent, 0);

            NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(mContext)
                    .setSmallIcon(iconForPlayPause)
                    .setContentTitle(title)
                    .setContentText(artist)
                    .setContentIntent(showActivityPendingIntent)
                    .setLargeIcon(image)
                    .setStyle(new NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0, 1, 2)
                            .setMediaSession(token))
                    .setColor(0x3f51b5);
            builder.addAction(createAction(R.drawable.ic_action_previous, "Previous", Utils.INTENT_ACTION_PREVIOUS));
            builder.addAction(createAction(iconForPlayPause, playPause, action));
            builder.addAction(createAction(R.drawable.ic_action_next, "Next", Utils.INTENT_ACTION_NEXT));
            builder.addAction(createAction(R.drawable.ic_action_remove, "Close", Utils.INTENT_ACTION_STOP));
            style.setShowActionsInCompactView(0, 1, 2);
            return builder.build();
            /*mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, builder.build());*/
        }
        return null;
    }

    /**
     * It Cancel the Notification.
     */
    public void cancelNotification() {
        if (mNotificationManager != null) {
            mNotificationManager.cancelAll();
        }
    }

    /**
     * Create Pending Actions For Notification and Used When Icons Clicked from Notification
     *
     * @param icon         Icon For Action
     * @param title        Title For Action
     * @param intentAction IntentAction used to Differentiate Intents
     * @return Returns Notification Action
     */
    private NotificationCompat.Action createAction(int icon, String title, String intentAction) {
        Intent intent = new Intent(mContext, MusicService.class);
        intent.setAction(intentAction);
        PendingIntent pendingIntent = PendingIntent.getService(mContext, 1, intent, 0);
        return new NotificationCompat.Action.Builder(icon, title, pendingIntent).build();
    }
}
