package com.quovantis.musicplayer.updated.helper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.quovantis.musicplayer.R;

/**
 * Created by sahil-goel on 8/9/16.
 */
public class AnimationHelper {

    public static void CircularReveal(View myView) {
        if (myView != null) {
            int cx = myView.getBottom();
            int cy = myView.getRight();
            float finalRadius = (float) Math.hypot(cx, cy);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                Animator anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);
                myView.setVisibility(View.VISIBLE);
                anim.start();

            } else {
                myView.setVisibility(View.VISIBLE);
            }
        }
    }

    public static void hideView(final View myView) {
        int cx = myView.getWidth() / 2;
        int cy = myView.getHeight() / 2;

        float initialRadius = (float) Math.hypot(cx, cy);

        Animator anim = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    myView.setVisibility(View.INVISIBLE);
                }
            });
            anim.start();
        } else {
            myView.setVisibility(View.INVISIBLE);
        }


    }
}
