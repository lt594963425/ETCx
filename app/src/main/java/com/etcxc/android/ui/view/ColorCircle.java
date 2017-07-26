package com.etcxc.android.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.etcxc.android.utils.UIUtils;


/**
 * 纯色的圆
 * Created by xwpeng on 2017/07/26.
 */
public class ColorCircle extends View {

    private final String TAG = ColorCircle.class.getSimpleName();

    public ColorCircle(Context context) {
        super(context);
        init();
    }

    public ColorCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColorCircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ColorCircle(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mColor);
    }

    private final int DEFAULT_COLOR = getResources().getColor(android.R.color.transparent);
    private final int DEFAULT_RADIUS = UIUtils.dip2Px(16);
    private int mColor = DEFAULT_COLOR;
    private int mRadius;
    private Paint mPaint;

    /**
     * @param color getResources().getColor(int color)
     */
    public void setColor(@ColorInt int color) {
        if (mColor != color) {
            mColor = color;
            mPaint.setColor(mColor);
            invalidate();
        }
    }

    public void setRadius(int radius) {
        if (mRadius != radius) {
            mRadius = radius;
            getLayoutParams().width = mRadius * 2;
            getLayoutParams().height = mRadius * 2;
            requestLayout();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int newWidth, newHeight;
        ViewGroup.LayoutParams lp = getLayoutParams();
//        Log.v(TAG, "try to onMeasure, LayoutParams " + lp.width + "," + lp.height);
        if (lp.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            newWidth = DEFAULT_RADIUS;
        } else {
            newWidth = lp.width;
        }
        if (lp.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            newHeight = DEFAULT_RADIUS;
        } else {
            newHeight = lp.height;
        }
        mRadius = Math.min(newWidth, newHeight) / 2;
        setMeasuredDimension(newWidth, newHeight);
    }
}
