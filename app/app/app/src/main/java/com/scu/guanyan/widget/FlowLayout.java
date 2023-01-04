package com.scu.guanyan.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * @program: Toolt
 * @author: 陈博文
 * @create: 2022-07-04 16:53
 * @description: 实现子视图的流式布局
 **/
public class FlowLayout extends ViewGroup {
    private final String TAG = "FlowLayout";

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0, height = 0;
        int lineWidth = 0, lineHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams params;
            if (child.getLayoutParams() instanceof MarginLayoutParams) {
                params = (MarginLayoutParams) child.getLayoutParams();
            } else {
                params = new MarginLayoutParams(0, 0);
            }

            int childWidth = child.getMeasuredWidth() + params.leftMargin + params.rightMargin;
            int childHeight = child.getMeasuredHeight() + params.topMargin + params.bottomMargin;

            if (lineWidth + childWidth > measureWidth) {
                // 需要另外起一行
                width = lineWidth > width ? lineWidth : width;
                height += lineHeight;

                lineHeight = childHeight;
                lineWidth = childWidth;
            } else {
                lineHeight = childHeight > lineHeight ? childHeight : lineHeight;
                lineWidth += childWidth;
            }
            if (i == getChildCount() - 1) {
                height += lineHeight;
                width = lineWidth > width ? lineWidth : width;
            }
        }
        setMeasuredDimension((widthMode==MeasureSpec.EXACTLY)?measureWidth:width,(heightMode==MeasureSpec.EXACTLY)?measureHeight:height);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int top = 0, left = 0;
        int lineWidth = 0, lineHeight = 0;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            MarginLayoutParams params;
            if (child.getLayoutParams() instanceof MarginLayoutParams) {
                params = (MarginLayoutParams) child.getLayoutParams();
            } else {
                params = new MarginLayoutParams(0, 0);
            }

            int childWidth = child.getMeasuredWidth() + params.leftMargin + params.rightMargin;
            int childHeight = child.getMeasuredHeight() + params.topMargin + params.bottomMargin;

            if (lineWidth + childWidth > this.getMeasuredWidth()) {
                // 需要另外起一行
                top += lineHeight;
                left = 0;

                lineHeight = childHeight;
                lineWidth = childWidth;
            } else {
                lineHeight = childHeight > lineHeight ? childHeight : lineHeight;
                lineWidth += childWidth;
            }
            // 计算子view的上下左右位置
            int leftPosition = left + params.leftMargin;
            int topPosition = top + params.topMargin;
            int rightPosition = leftPosition + child.getMeasuredWidth();
            int bottomPosition = topPosition + child.getMeasuredHeight();

            child.layout(leftPosition, topPosition, rightPosition, bottomPosition);
            left += childWidth;
        }
    }

}
