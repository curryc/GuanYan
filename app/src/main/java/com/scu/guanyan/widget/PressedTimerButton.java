package com.scu.guanyan.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2023/5/9 13:27
 * @description:按下可以倒计时的按钮
 **/
public class PressedTimerButton extends View implements View.OnTouchListener {
    private float mSize;
    private int mWidth, mHeight;
    private long mTime, mTick, mStart;
    private float mAngle = 0f;
    private Paint mCenterPaint, mOuterPaint,mHighlightPaint;

    private float mCenterFrac = 0.8f;
    private ValueAnimator mCenterFracAnimator;

    public PressedTimerButton(@NonNull Context context) {
        this(context, 10000, 20);
    }

    public PressedTimerButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mTime = 10000;
        this.mTick = this.mStart = 0;
        init();
    }

    public PressedTimerButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mTime = 10000;
        this.mTick = this.mStart = 0;
        init();
    }

    public PressedTimerButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mTime = 10000;
        this.mTick = this.mStart = 0;
        init();
    }

    public PressedTimerButton(Context context, long time, int size) {
        super(context);
        this.mTime = time;
        this.mTick = this.mStart = 0;
        init();
    }

    private void init() {
        this.setOnTouchListener(this);

        mCenterPaint = new Paint();
        mCenterPaint.setStyle(Paint.Style.FILL);
        mCenterPaint.setColor(Color.WHITE);

        mOuterPaint = new Paint();
        mOuterPaint.setStyle(Paint.Style.FILL);
        mOuterPaint.setColor(Color.GRAY);

        mHighlightPaint = new Paint();
        mHighlightPaint.setStyle(Paint.Style.FILL);
        mHighlightPaint.setColor(Color.RED);

        mCenterFracAnimator= new ValueAnimator();
        mCenterFracAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mCenterFrac = valueAnimator.getAnimatedFraction();
            }
        });
    }

    public void setTimeInterval(long timeInterval){
        this.mTime = timeInterval;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(mWidth / 2, mHeight / 2, mSize, mOuterPaint);
        canvas.drawArc(mWidth/2 - mSize, mHeight/2 - mSize, mWidth/2 + mSize,mHeight/2 + mSize,
                -90,
                mAngle,
                true,
                mHighlightPaint);
        canvas.drawCircle(mWidth / 2, mHeight / 2, mSize * mCenterFrac, mCenterPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mHeight = h;
        mWidth = w;
        mSize = ((float) Math.min(w, h)) / 2;
    }

    private void onPressed(boolean pressed) {
        if (pressed) {
            mCenterFracAnimator.setFloatValues(mCenterFrac, 0.7f);
            mCenterFracAnimator.start();
        } else {
            mCenterFracAnimator.setFloatValues(mCenterFrac, 0.8f);
            mCenterFracAnimator.start();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStart = System.currentTimeMillis();
                setPressed(true);
                onPressed(true);
                break;
            case MotionEvent.ACTION_MOVE:
                mTick = System.currentTimeMillis();
                mAngle += (((float)(mTick - mStart)) / mTime) * 360f;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                setPressed(false);
                onPressed(false);
                break;
            default:
                break;
        }
        return true;
    }


}
