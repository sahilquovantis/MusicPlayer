package com.quovantis.musicplayer.updated.ui.views.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.quovantis.musicplayer.R;

/**
 * Created by sahil-goel on 20/10/16.
 */

public class CustomBubble extends View {

    int left, bottom, top, right;
    private Context mContext;
    private Paint mPaint;
    private Path mPath;
    private float[] mPoints;
    private Paint mTextPaint;
    private String mTitle;
    private int mColor;

    public CustomBubble(Context context) {
        this(context, null);
    }

    public CustomBubble(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomBubble(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        mTitle = " ";
        mPaint = new Paint();
        mColor = ContextCompat.getColor(mContext, R.color.colorAccent);
        mPaint.setColor(mColor);
        mPaint.setDither(false);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mTextPaint = new Paint();
        mTextPaint.setColor(ContextCompat.getColor(mContext, android.R.color.white));
        mTextPaint.setDither(true);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(getResources().getDimension(R.dimen.sp_16));
        mPath = new Path();
        mPoints = new float[8];
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle((left + right) / 2, (top + bottom) / 2, (top + bottom) / 2, mPaint);
        mPoints[0] = (left + right) / 2;
        mPoints[1] = bottom;
        mPoints[2] = right;
        mPoints[3] = bottom;
        mPoints[4] = right;
        mPoints[5] = (top + bottom) / 2;
        mPoints[6] = (left + right) / 2;
        mPoints[7] = bottom;
        canvas.drawVertices(Canvas.VertexMode.TRIANGLES, 8, mPoints, 0, null, 0, null, 0, null, 0, 0, mPaint);
        mPath.moveTo(mPoints[0], mPoints[1]);
        mPath.lineTo(mPoints[2], mPoints[3]);
        mPath.lineTo(mPoints[4], mPoints[5]);
        canvas.drawPath(mPath, mPaint);
        float width = mTextPaint.measureText(mTitle);
        canvas.drawText(mTitle, (left + right - width) / 2, (bottom + top + width) / 2, mTextPaint);
        super.onDraw(canvas);
    }

    public void setText(String text) {
        mTitle = text;
        invalidate();
    }

    public void setColor(int color) {
        mColor = color;
        mPaint.setColor(mColor);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        left = getLeft();
        right = left + getMeasuredWidth();
        top = getTop();
        bottom = top + getMeasuredHeight();
    }
}
