package com.scu.guanyan.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class BoxDrawingView extends View {
    private static final String TAG = "BoxDrawingView";

    private Box mCurrentBox;
    //    private List<Box> mBoxes = new ArrayList<>();
    private Paint mBoxPaint;

    public float[] getInfo(){
        return new float[]{mCurrentBox.mOrigin.x,mCurrentBox.mOrigin.y,mCurrentBox.mCurrent.x,mCurrentBox.mCurrent.y};
    }

    public Box getCurrentBox(){
        return mCurrentBox;
    }

    public BoxDrawingView(Context context) {
        this(context, null);
    }

    public BoxDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(Color.RED);

        mCurrentBox = new Box(new PointF(0,0));
        mCurrentBox.setCurrent(new PointF(0,0));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF current = new PointF(event.getX(), event.getY());
        String action = "";
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                action = "ACTION_DOWN";
                mCurrentBox = new Box(current);
//                mBoxes.add(mCurrentBox);
                break;
            case MotionEvent.ACTION_UP:
                action = "ACTION_UP";
                break;
            case MotionEvent.ACTION_MOVE:
                action = "ACTION_MOVE";
                if (mCurrentBox != null) {
                    mCurrentBox.setCurrent(current);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                action = "ACTION_CANCEL";
                break;
        }
        Log.i(TAG, action + " at x= " + current.x + ", y= " + current.y);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float left = Math.min(mCurrentBox.getOrigin().x, mCurrentBox.getCurrent().x);
        float right = Math.max(mCurrentBox.getOrigin().x, mCurrentBox.getCurrent().x);
        float top = Math.min(mCurrentBox.getOrigin().y, mCurrentBox.getCurrent().y);
        float bottom = Math.max(mCurrentBox.getOrigin().y, mCurrentBox.getCurrent().y);

        canvas.drawRect(left, top, right, bottom, mBoxPaint);
//        }
    }


    public static class Box {
        private PointF mOrigin;
        private PointF mCurrent;

        public Box(PointF origin) {
            mOrigin = origin;
            mCurrent = origin;
        }

        public PointF getOrigin() {
            return mOrigin;
        }

        public PointF getCurrent() {
            return mCurrent;
        }

        public void setCurrent(PointF current) {
            mCurrent = current;
        }

        public void init(){
            mOrigin = new PointF(0,0);
            mCurrent = new PointF(0,0);
        }
    }
}
