package com.curry.toolt.provider;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.curry.function.App;
import com.curry.function.bean.Function;
import com.curry.function.bean.FunctionCatalog;
import com.curry.toolt.R;
import com.curry.toolt.widget.RoundDotButton;
import com.curry.util.adpter.RecyclerViewHolder;
import com.curry.util.base.BaseViewProvider;
import com.curry.util.view.FlowLayout;

/**
 * @program: Toolt
 * @author: 陈博文
 * @create: 2022-07-04 11:02
 * @description: 展示一个功能分类的provider
 **/
public class FunctionCatalogProvider extends BaseViewProvider<FunctionCatalog> {
    private SparseArray<FlowLayout> mFlows;
    private SparseArray<FrameLayout> mHintContainers;
    private SparseArray<ImageView> mRights;

    private GradientDrawable mHintBackground;

    public FunctionCatalogProvider(Context mContext) {
        super(mContext, R.layout.holder_func_catalog);
        mFlows = new SparseArray<>();
        mHintContainers = new SparseArray<>();
        mRights = new SparseArray<>();

        mHintBackground = new GradientDrawable();
        mHintBackground.setTint(App.getThemeColor());
        mHintBackground.setCornerRadius(mContext.getResources().getDimensionPixelSize(R.dimen.round_rec_radius));
    }

    @SuppressLint("Range")
    @Override
    public void bindView(RecyclerViewHolder holder, FunctionCatalog data) {
        FrameLayout mHintContainer;
        ImageView mRight;
        FlowLayout mFlow;

        mHintContainer = holder.getViewById(R.id.catalog_hint_container);
        mRight = holder.getViewById(R.id.catalog_right);
        mFlow = holder.getViewById(R.id.catalog_flow);


        // 初始化
        mHintContainer.setAlpha(0);
        mHintContainer.setBackground(mHintBackground);

        mFlows.put(holder.hashCode(), mFlow);
        mHintContainers.put(holder.hashCode(), mHintContainer);
        mRights.put(holder.hashCode(), mRight);

        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hashcode = holder.hashCode();
                showOrHideItems(mFlows.get(hashcode), mHintContainers.get(hashcode), mRights.get(hashcode));
            }
        }, R.id.catalog_upper);
        holder.setSrc(R.id.catalog_icon, data.getIcon());
        holder.setText(R.id.catalog_title, data.getTitle());
        holder.setText(R.id.catalog_hint, data.getFunctions().size() + "个应用");

        addItems(mFlow, data);
    }

    private void addItems(FlowLayout mFlow, FunctionCatalog data) {
        RoundDotButton button;
        for (Function function : data.getFunctions()) {
            button = new RoundDotButton(mFlow.getContext());
            button.getRootView().setPadding(4, 0, 4, 0);
            button.init(function.getTitle(), function.getLevel(), function.getStarter());
            mFlow.addView(button);
        }
    }

    /**
     * 收纳和展示功能
     */
    private void showOrHideItems(FlowLayout mFlow, FrameLayout mHintContainer, ImageView mRight) {
        TranslateAnimation anim;
        if (mFlow.getVisibility() == View.VISIBLE) {
            mFlow.setVisibility(View.GONE);

            ValueAnimator rotate = ValueAnimator.ofFloat(0, -90F);
            ValueAnimator appear = ValueAnimator.ofFloat(0, 255);
            rotate.setDuration(App.ANIM_DURATION);
            appear.setDuration(App.ANIM_DURATION);

            rotate.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mRight.setRotation((Float) animation.getAnimatedValue());
                }
            });

            appear.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mHintContainer.setAlpha((Float) animation.getAnimatedValue());
                }
            });

            rotate.start();
            appear.start();
        } else {
            anim = new TranslateAnimation(0, 0, -mFlow.getMeasuredHeight(), 0);
            anim.setDuration(App.ANIM_DURATION);
            mFlow.startAnimation(anim);

            mFlow.setVisibility(View.VISIBLE);

            ValueAnimator rotate = ValueAnimator.ofFloat(-90F, 0);
            ValueAnimator appear = ValueAnimator.ofFloat(255, 0);
            rotate.setDuration(App.ANIM_DURATION);
            appear.setDuration(App.ANIM_DURATION);

            rotate.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mRight.setRotation((Float) animation.getAnimatedValue());
                }
            });

            appear.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mHintContainer.setAlpha((Float) animation.getAnimatedValue());
                }
            });

            rotate.start();
            appear.start();
        }
    }
}

