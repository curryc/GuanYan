package com.scu.guanyan.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2023/5/9 13:27
 * @description:按下可以倒计时的按钮
 **/
public class PressedTimerButton extends View implements View.OnClickListener {
    private float mSize;
    private int mWidth, mHeight;
    private long mTime, mTick, mStart;
    private float mAngle = 0f, mPriorAngle = 0f;
    private Paint mCenterPaint, mOuterPaint, mHighlightPaint;
    private Handler mTimer;
    private Runnable mTimerUpdater;
    private HandlerThread mThreadForTimer;
    private boolean mPressed = false;

    private OnPressedCallback mOnPressedCallback;

    ValueAnimator mCenterFracAnimator;
    private float mCenterFrac = 0.8f;
    private ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener;

    public PressedTimerButton(@NonNull Context context) {
        this(context, 10000, 20);
    }

    public PressedTimerButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mTime = 10000;
        this.mTick = this.mStart = 0;
        initData();
    }

    public PressedTimerButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mTime = 10000;
        this.mTick = this.mStart = 0;
        initData();
    }

    public PressedTimerButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mTime = 10000;
        this.mTick = this.mStart = 0;
        initData();
    }

    public PressedTimerButton(Context context, long time, int size) {
        super(context);
        this.mTime = time;
        this.mTick = this.mStart = 0;
        initData();
    }

    private void initData() {
        this.setOnClickListener(this);
        mThreadForTimer = new HandlerThread("timer");
        mThreadForTimer.start();
        mTimer = new Handler(mThreadForTimer.getLooper());
        this.mTimerUpdater = new Runnable() {
            @Override
            public void run() {
                if (mAngle < 360) {
                    mTick = System.currentTimeMillis();
                    mAngle = mPriorAngle + (((float) (mTick - mStart)) / mTime) * 360f;
                    mTimer.postDelayed(this, 70);
                } else {
                    mCenterPaint.setColor(Color.RED);
                }
                invalidate();
            }
        };

        mCenterPaint = new Paint();
        mCenterPaint.setStyle(Paint.Style.FILL);
        mCenterPaint.setColor(Color.WHITE);

        mOuterPaint = new Paint();
        mOuterPaint.setStyle(Paint.Style.FILL);
        mOuterPaint.setColor(Color.GRAY);

        mHighlightPaint = new Paint();
        mHighlightPaint.setStyle(Paint.Style.FILL);
        mHighlightPaint.setColor(0xffded7cd);

        mAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mCenterFrac = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        };
    }

    public void init() {
        this.mTick = this.mStart = 0;
        this.mAngle = mPriorAngle = 0;
        mCenterPaint.setColor(Color.WHITE);
        invalidate();
    }

    public void setTimeInterval(long timeInterval) {
        this.mTime = timeInterval;
    }

    public void setOnPressedCallback(OnPressedCallback onPressedCallback) {
        mOnPressedCallback = onPressedCallback;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(mWidth / 2, mHeight / 2, mSize, mOuterPaint);
        canvas.drawArc(mWidth / 2 - mSize, mHeight / 2 - mSize, mWidth / 2 + mSize, mHeight / 2 + mSize,
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
        if (this.mOnPressedCallback != null)
            this.mOnPressedCallback.onPressed(pressed);

        if (pressed) {
            if (mAngle < 360) {
                if (mCenterFracAnimator != null && mCenterFracAnimator.isRunning())
                    mCenterFracAnimator.end();
                mCenterFracAnimator = ValueAnimator.ofFloat(mCenterFrac, 0.7f);
                mCenterFracAnimator.addUpdateListener(mAnimatorUpdateListener);
                mCenterFracAnimator.start();
                mTimer.post(mTimerUpdater);
            } else {
                mCenterPaint.setColor(Color.RED);
                invalidate();
            }
        } else {
            mPriorAngle = mAngle;
            if (mAngle < 360) {
                if (mCenterFracAnimator.isRunning())
                    mCenterFracAnimator.end();
                mCenterFracAnimator = ValueAnimator.ofFloat(mCenterFrac, 0.8f);
                mCenterFracAnimator.addUpdateListener(mAnimatorUpdateListener);
                mCenterFracAnimator.start();

                mTimer.removeCallbacks(mTimerUpdater);
            } else {
                mCenterPaint.setColor(Color.WHITE);
                invalidate();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (!mPressed) {
            mPressed = true;
            mStart = System.currentTimeMillis();
            setPressed(true);
            onPressed(true);
        } else {
            mPressed = false;
            setPressed(false);
            onPressed(false);
        }

    }

    public interface OnPressedCallback {
        void onPressed(boolean pressed);
    }
}
