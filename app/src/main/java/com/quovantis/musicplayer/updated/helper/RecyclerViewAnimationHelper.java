package com.quovantis.musicplayer.updated.helper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.quovantis.musicplayer.R;

/**
 * Created by sahil-goel on 31/8/16.
 */
public class RecyclerViewAnimationHelper {
    public static void animate(Context mContext, RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(mContext, R.anim.recycler_view_scrool_animation);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }
}
