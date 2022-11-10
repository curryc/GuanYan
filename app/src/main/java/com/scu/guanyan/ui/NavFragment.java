package com.scu.guanyan.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.scu.guanyan.R;
import com.scu.guanyan.base.BaseFragment;
import com.scu.guanyan.base.ViewHolder;
import com.scu.guanyan.widget.RoundImageView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: Guanyan
 * @author: 陈博文
 * @create: 2022-11-06 22:40
 * @description: 导航栏
 **/
public class NavFragment extends BaseFragment {
    private List<ImageView> mNav;
    private RoundImageView mFocus;
    private int mCurPosition;
    private OnTabChanged mOnTabChanged;

    private static int ANIMATION_DURATION = 200;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_nav;
    }

    @Override
    protected void initView(ViewHolder viewHolder, View root) {
        mNav = new ArrayList<>();
        mNav.add(viewHolder.getViewById(R.id.home));
        mNav.add(viewHolder.getViewById(R.id.teach));
        mNav.add(viewHolder.getViewById(R.id.setting));
        mFocus = viewHolder.getViewById(R.id.focus);
        viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSelect(mNav.indexOf((ImageView) view), true);
            }
        }, R.id.home, R.id.teach, R.id.setting);
    }

    /**
     * 设置当点击新按钮时的操作
     * @param onTabChanged
     */
    public void setUp(OnTabChanged onTabChanged) {
        this.mOnTabChanged = onTabChanged;
    }


    private void doSelect(int seq, boolean ChangeTab) {
        float startX = mFocus.getX();
        float endX = seq * getResources().getDimensionPixelSize(R.dimen.nav_button_size);
        if (startX == endX) {
            this.mOnTabChanged.onReselect(seq);
        } else {
            moveAnimator(mFocus, startX, endX);
            if(ChangeTab){
                this.mOnTabChanged.onSelect(seq);
            }
        }
        mCurPosition = seq;
    }

    public void doSelect(int seq){
        doSelect(seq, true);
    }

    public interface OnTabChanged {
        void onSelect(int sequence);
        void onReselect(int sequence);
    }

    public void moveAnimator(View view, float original, float dest) {
        ValueAnimator mov = ObjectAnimator.ofFloat( original, dest);
        mov.setDuration(ANIMATION_DURATION);
        mov.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mFocus.setX((float)mov.getAnimatedValue());
            }
        });
        mov.start();
    }
}
