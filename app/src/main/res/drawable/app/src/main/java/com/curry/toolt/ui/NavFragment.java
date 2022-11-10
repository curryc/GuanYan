package com.curry.toolt.ui;

import android.view.View;
import com.curry.toolt.R;
import com.curry.toolt.base.BaseFragment;
import com.curry.toolt.ui.collect.CollectFragment;
import com.curry.toolt.ui.home.HomeFragment;
import com.curry.toolt.ui.more.MoreFragment;
import com.curry.util.view.ViewHolder;
import com.curry.toolt.widget.NavigationButton;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: Toolt
 * @author: 陈博文
 * @create: 2022-06-30 21:09
 * @description: 下方的导航栏
 **/
public final class NavFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "NavFragment";

    private List<NavigationButton> buttons;

    private NavigationButton mCurrentNavButton;
    OnTabChanged onTabChanged;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_nav;
    }

    @Override
    protected void initView(ViewHolder viewHolder, View root) {
        buttons = new ArrayList<>();

        // 改进：自定义button顺序和数量
        buttons.add(root.findViewById(R.id.nav_collect));
        buttons.add(root.findViewById(R.id.nav_home));
        buttons.add(root.findViewById(R.id.nav_more));

        buttons.get(0).init(R.drawable.ic_nav_collect, getString(R.string.collect), 0, CollectFragment.class);
        buttons.get(1).init(R.drawable.ic_nav_home, getString(R.string.home), 1, HomeFragment.class);
        buttons.get(2).init(R.drawable.ic_nav_more, getString(R.string.more), 2, MoreFragment.class);

        viewHolder.setOnClickListener(this, R.id.nav_collect, R.id.nav_home, R.id.nav_more);

        doSelect(buttons.get(1), false);
    }

    public List<NavigationButton> getButtons(){
        return buttons;
    }

    /**
     * 点击一个button后的,需要进行的操作
     * @param newNavButton 点击的button
     * @param changeTab 是否需要切换Tab
     */
    private void doSelect(NavigationButton newNavButton, boolean changeTab) {
        NavigationButton oldNavButton = null;
        if (mCurrentNavButton != null) {
            oldNavButton = mCurrentNavButton;
            if (oldNavButton == newNavButton) {
                onReselect(oldNavButton);
                return;
            }
            oldNavButton.setSelected(false);
        }
        newNavButton.setSelected(true);
        if (changeTab)
            this.onTabChanged.onTabChanged(newNavButton.getSeq());
        mCurrentNavButton = newNavButton;
    }

    /**
     * 选择一个位置的button后的操作
     * @param position
     */
    public void doSelect(int position) {
        doSelect(buttons.get(position), false);
    }

    @Override
    public void onClick(View v) {
        if (v instanceof NavigationButton) {
            NavigationButton button = (NavigationButton) v;
            doSelect(button, true);
        }
    }
    /**
     * 一个按钮被两次点击的方法
     * @param button
     */
    private void onReselect(NavigationButton button) {
    }

    /**
     * 设置当点击新按钮时的操作
     * @param onTabChanged
     */
    public void setUp(OnTabChanged onTabChanged) {
        this.onTabChanged = onTabChanged;
    }


    /**
     * 点击button后需要展示的方法接口,实现这个接口用来实现导航
     */
    public interface OnTabChanged {
        void onTabChanged(int newPosition);
    }
}
