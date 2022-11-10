package com.curry.toolt.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.curry.util.view.ViewHolder;
import org.jetbrains.annotations.NotNull;

/**
 * @program: Toolt
 * @author: 陈博文
 * @create: 2022-06-30 19:58
 * @description: 对所有Fragment进行基础的生命周期控制和基础方法提供
 **/
public abstract class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment";
    private static final String SAVE_STATE_IS_HIDEN = "save_state";

    private Context mContext;
    private ViewHolder mViewHolder;
    private View mRootView;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mContext = null;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            if (!savedInstanceState.getBoolean(SAVE_STATE_IS_HIDEN)) {
                ft.hide(this);
            } else {
                ft.show(this);
            }
            ft.commit();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVE_STATE_IS_HIDEN, isHidden());
    }

    public ViewHolder getViewHolder() {
        return mViewHolder;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        if (mRootView != null) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null)
                parent.removeView(mRootView);
        } else {
            mViewHolder = new ViewHolder(inflater, container, getLayoutId());
            mRootView = mViewHolder.getRootView();
            initView(mViewHolder, mViewHolder.getRootView());
        }
        return mViewHolder.getRootView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mViewHolder.getRootView().getParent() != null) {
            ((ViewGroup) mViewHolder.getRootView().getParent()).removeView(mViewHolder.getRootView());
        }
    }

    /**
     * fragment的viewID设置
     * @return fragment的layout的ID
     */
    protected abstract int getLayoutId();

    /**
     * 初始化视图
     * @param viewHolder @see {@link ViewHolder}
     * @param root 根View
     */
    protected abstract void initView(ViewHolder viewHolder, View root);

    protected void initData() {}
}
