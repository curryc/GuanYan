package com.scu.guanyan.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.scu.guanyan.R;

/**
 * @program: Toolt
 * @author: 陈博文
 * @create: 2022-07-04 16:55
 * @description: a rount rectangle to show a function
 **/
public class RoundDotButton extends FrameLayout {
    private final String TAG = "RoundRecButton";
    private Context mContext;

    private View mRootView;
    private TextView mTitle;
    private ImageView mDot;

    public RoundDotButton(@NonNull Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public RoundDotButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public RoundDotButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    public RoundDotButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext = context;
        init();
    }

    private void init(){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        // 下面必须设置未attachToRoot为true，否则将不显示！花费了一晚上
        mRootView = inflater.inflate(R.layout.button_round_dot, this, true);

        mTitle = findViewById(R.id.button_round_dot_title);
        mDot = findViewById(R.id.button_round_dot_dot);

//        FrameLayout button = mRootView.findViewById(R.id.button_round_dot);
//        GradientDrawable background = new GradientDrawable();
//        background.setTint();
//        background.setCornerRadius(getResources().getDimensionPixelOffset(R.dimen.round_dot_button_radius));
//        button.setBackground(background);
    }

    /**
     * 初始化一个按钮，通过规定一个按钮的文字和级别
     * @param title
     * @param level
     */
    public void init(String title, int level, OnClickListener listener, OnClickListener dotListener){
        this.mTitle.setText(title);
//        drawDot(level);
        mTitle.setOnClickListener(listener);
        mDot.setOnClickListener(dotListener);
        drawDot(-1);
    }

    public String getTitle(){
        return mTitle.getText().toString();
    }

    public void showDot(){
        drawDot(0);
    }

    public void hideDot(){
        drawDot(-1);
    }

    /**
     * 只根据文字初始化按钮，默认level未0，不显示小点
     * @param title
     */
    public void init(String title, OnClickListener listener,OnClickListener dotListener){
        init(title, 0, listener, dotListener);
    }

    /**
     * 根据级别确定level的颜色
     * @param level
     */
    private void drawDot(int level){
        if(level == -1){
            mDot.setVisibility(View.GONE);
        }else {
            mDot.setBackgroundColor(Color.RED);
        }
    }
}
