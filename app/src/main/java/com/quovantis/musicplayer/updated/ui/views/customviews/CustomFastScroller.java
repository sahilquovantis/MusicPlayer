package com.quovantis.musicplayer.updated.ui.views.customviews;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.interfaces.SectionIndexedTitle;

/**
 * Created by sahil-goel on 19/10/16.
 */

public class CustomFastScroller extends LinearLayout {

    private static final long BUBBLE_ANIMATION_DURATION = 500;
    private static final long BUBBLE_HIDE_DURATION = 500;
    private final int mBubbleColor;
    private final int mScrollBarColor;
    private View mTouchHandle;
    private CustomBubble mNewBubble;
    private int mHeight;
    private RecyclerView mRecyclerView;
    private final ScrollListener mScrollListener = new ScrollListener();
    private ObjectAnimator mAnimator = null;
    private Handler mHandler;

    public CustomFastScroller(Context context) {
        this(context, null);
    }

    public CustomFastScroller(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomFastScroller(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomFastScroller, 0, 0);
        mBubbleColor = typedArray.getColor(R.styleable.CustomFastScroller_bubbleColor, ContextCompat.getColor(context, R.color.colorPrimary));
        mScrollBarColor = typedArray.getColor(R.styleable.CustomFastScroller_scrollBarColor, ContextCompat.getColor(context, R.color.colorAccent));
        initialise(context);
    }

    private void initialise(Context context) {
        mHandler = new Handler();
        setOrientation(HORIZONTAL);
        setClipChildren(false);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.custom_scroller, this);
        mTouchHandle = findViewById(R.id.handle);
        mNewBubble = (CustomBubble) findViewById(R.id.custom_bubble);
        mNewBubble.setColor(mBubbleColor);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getX() < mTouchHandle.getX()) {
                return false;
            }
            mTouchHandle.setSelected(true);
            if (mAnimator != null)
                mAnimator.cancel();
            if (mNewBubble.getVisibility() != VISIBLE)
                showBubble();
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            setPosition(event.getY());
            setRecyclerViewPosition(event.getY());
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            mTouchHandle.setSelected(false);
            hideBubble();
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            mTouchHandle.setSelected(false);
            hideBubble();
            return true;
        }
        return super.onTouchEvent(event);
    }

    private void showBubble() {
        if (mNewBubble == null)
            return;
        mNewBubble.setVisibility(VISIBLE);
        if (mAnimator != null)
            mAnimator.cancel();
        mAnimator = ObjectAnimator.ofFloat(mNewBubble, "alpha", 0f, 1f).setDuration(BUBBLE_ANIMATION_DURATION);
        mAnimator.start();
    }


    private void hideBubble() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mNewBubble == null)
                    return;
                if (mAnimator != null)
                    mAnimator.cancel();
                mAnimator = ObjectAnimator.ofFloat(mNewBubble, "alpha", 1f, 0f).setDuration(BUBBLE_ANIMATION_DURATION);
                mAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mAnimator = null;
                        mNewBubble.setVisibility(INVISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);
                        mAnimator = null;
                        mNewBubble.setVisibility(INVISIBLE);
                    }
                });
                mAnimator.start();
            }
        }, BUBBLE_HIDE_DURATION);
    }

    private void setRecyclerViewPosition(float y) {
        if (mRecyclerView != null && (mRecyclerView.getAdapter() instanceof SectionIndexedTitle)) {
            int itemCount = mRecyclerView.getAdapter().getItemCount();
            float proportion;
            if (mTouchHandle.getY() == 0) {
                proportion = 0f;
            } else if (mTouchHandle.getY() + mTouchHandle.getHeight() >= mHeight) {
                proportion = 1f;
            } else {
                proportion = y / (float) mHeight;
            }
            int targetPos = getValueInRange(0, itemCount - 1, (int) (proportion * (float) itemCount));
            mRecyclerView.scrollToPosition(targetPos);
            mNewBubble.setText(((SectionIndexedTitle) mRecyclerView.getAdapter()).getSectionedTitle(targetPos));
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldWidth, int oldHeight) {
        super.onSizeChanged(w, h, oldWidth, oldHeight);
        mHeight = h;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        mRecyclerView.addOnScrollListener(mScrollListener);
    }

    private void setPosition(float y) {
        int handleHeight = mTouchHandle.getHeight();
        int bubbleHeight = mNewBubble.getHeight();
        if (mNewBubble != null) {
            int a = mHeight - bubbleHeight - handleHeight / 2;
            int b = (int) (y - bubbleHeight);
            mNewBubble.setY(getValueInRange(0, a, b));
        }
        int c = mHeight - handleHeight;
        int d = (int) (y - handleHeight / 2);
        mTouchHandle.setY(getValueInRange(0, c, d));
    }

    private int getValueInRange(int min, int max, int value) {
        int minimum = Math.max(min, value);
        return Math.min(minimum, max);
    }

    class ScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (mTouchHandle == null || mTouchHandle.isSelected())
                return;

            final int verticalScrollOffset = recyclerView.computeVerticalScrollOffset();
            final int verticalScrollRange = recyclerView.computeVerticalScrollRange();
            float proportion = (float) verticalScrollOffset / ((float) verticalScrollRange - mHeight);
            setPosition(mHeight * proportion);
        }
    }
}
