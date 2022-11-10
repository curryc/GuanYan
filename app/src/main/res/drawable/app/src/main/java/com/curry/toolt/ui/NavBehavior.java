package com.curry.toolt.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.curry.function.App;
import com.google.android.material.appbar.AppBarLayout;
import org.jetbrains.annotations.NotNull;

import com.curry.toolt.R;

/**
 * @program: Toolt
 * @author: 陈博文
 * @create: 2022-07-02 15:47
 * @description: 通过这个Behavior来实现下方navFragment的展示和消失
 **/
public class NavBehavior extends CoordinatorLayout.Behavior<View> {
    private float mStartYPosition; // 起始的Y轴位置
    private float mEndPosition;

    private boolean flag;
    private boolean flagForRecord;

    public NavBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        mStartYPosition = 0;
        flag = true;
        flagForRecord = true;
    }

    @Override
    public boolean layoutDependsOn(@NonNull @NotNull CoordinatorLayout parent, @NonNull @NotNull View child, @NonNull @NotNull View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(@NonNull @NotNull CoordinatorLayout parent, @NonNull @NotNull View child, @NonNull @NotNull View dependency) {
        if (dependency.getY() > mStartYPosition && !flag) {
            showButtonNav(child);
        } else if (dependency.getY() < mStartYPosition && flag) {
            hideButtonNav(child);
        }
        recordData(dependency, child);
        return super.onDependentViewChanged(parent, child, dependency);
    }

    private void recordData(View dependency, View child) {
        mStartYPosition = dependency.getY();
    }

    private void showButtonNav(final View view) {
        flag = !flag;
        ValueAnimator anim = ValueAnimator.ofFloat(view.getY(), mEndPosition);
        anim.setDuration(300);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setY((Float) anim.getAnimatedValue());
            }
        });
        anim.start();

        // 另外几种动画实现
//        if (view != null && view.getVisibility() == View.INVISIBLE) {
//            Animation translateAnimation = new TranslateAnimation(view.getLeft(), view.getLeft(), view.getHeight(), 0);
//            translateAnimation.setDuration(300);
//            translateAnimation.setInterpolator(new OvershootInterpolator(0.6f));
//            view.startAnimation(translateAnimation);
//        }

//        Animation anim = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_in_bottom);
//        view.startAnimation(anim);
    }

    private void hideButtonNav(final View view) {
        flag = !flag;
        if(flagForRecord) {
            mEndPosition = view.getY();
            flagForRecord = !flagForRecord;
        }
        ValueAnimator anim = ValueAnimator.ofFloat(view.getY(), view.getY() + view.getHeight());
        anim.setDuration(App.ANIM_DURATION);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setY((Float) anim.getAnimatedValue());
            }
        });
        anim.start();
    }
}
