package com.curry.toolt.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.curry.function.App;
import com.curry.toolt.R;
import org.jetbrains.annotations.NotNull;

/**
 * @program: Toolt
 * @author: 陈博文
 * @create: 2022-06-30 17:23
 * @description: 下方导航栏的一个导航按钮
 **/
public class NavigationButton extends FrameLayout {
    private View mRootView;
    private ImageView mIcon;
    private TextView mTitle;

    private Class<?> clz;
    private String mTag;
    private int mSeq;
    private Fragment mFragment;

    public void setFragment(Fragment fragment){
        this.mFragment = fragment;
    }

    public Fragment getFragment(){return this.mFragment;}

    public Class getClz(){return this.clz;}

    public String getName(){return this.mTag;}

    public int getSeq(){return mSeq;}

    public String getTitle() {
        return mTitle.getText().toString();
    }

    private AnimatorSet mAnimator = new AnimatorSet();//组合动画

    public NavigationButton(@NonNull Context context) {
        super(context);
        init();
    }

    public NavigationButton(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NavigationButton(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public NavigationButton(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        mRootView = inflater.inflate(R.layout.button_navigation, this,true);

        mIcon = findViewById(R.id.nav_button_icon);
        mTitle = findViewById(R.id.nav_button_tag);
    }

    public void init(int icon, String title,int sequence, Class<?> clz){
        this.mIcon.setImageResource(icon);
        this.mTitle.setText(title);
        this.clz = clz;
        this.mTag = clz.getName();
        this.mSeq = sequence;
    }

    /**
     * 设置导航栏被选中或者不被选中
     * @param selected
     */
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        mIcon.setSelected(selected);
        mTitle.setSelected(selected);
        if (selected) {
            scaleAnimator(mRootView, 1f, 1.2f);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mIcon.setColorFilter(App.getThemeColor());
                mTitle.setTextColor(App.getThemeColor());
            }
        } else {
            scaleAnimator(mRootView, 1.2f, 1f);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mIcon.setColorFilter(App.getContext().getColor(R.color.black));
                mTitle.setTextColor(App.getContext().getColor(R.color.black));
            }
        }
    }

    /**
     * 实现选中或者不给选中的动画
     * @param view
     * @param orignal
     * @param dest
     */
    public void scaleAnimator(View view, float orignal, float dest) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", orignal, dest);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", orignal, dest);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new DecelerateInterpolator());
        mAnimator.play(scaleX).with(scaleY);//两个动画同时开始
        mAnimator.start();
    }
}
