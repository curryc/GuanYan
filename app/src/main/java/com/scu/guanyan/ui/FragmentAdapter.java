package com.scu.guanyan.ui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: Toolt
 * @author: 陈博文
 * @create: 2022-06-30 23:17
 * @description: 对于ViewPager使用的FragmentAdapter, 继承自FragmentStateAdapter
 **/
public class FragmentAdapter extends FragmentStateAdapter {
    private final String TAG = "Fragment state adapter";

    private List<PagerInfo> list;
    private FragmentManager fm;

    /**
     * 通过一些PagerInfo列表的初始化一个Adapter
     *
     * @param fragmentManager 用来生成一个fragment
     * @param lifecycle       必要的lifeCycle
     * @param l               pagerInfo的泪飙
     */
    public FragmentAdapter(@NonNull @NotNull FragmentManager fragmentManager, Lifecycle lifecycle, List<PagerInfo> l) {
        super(fragmentManager, lifecycle);
        this.fm = fragmentManager;
        setPagerInfo(l);
    }


    /**
     * 在一个位置创建一个Fragment
     * 一开始发现这个方法一直不被调用,后来发现是嵌套在CoordinatorLayout中的一个FrameLayout中,就无法调用这个方法
     *
     * @param position
     * @return
     */
    @Override
    public Fragment createFragment(int position) {
        PagerInfo pi = list.get(position);
        Class<?> clz = pi.clz;
        Fragment fragment = fm.getFragmentFactory().instantiate(clz.getClassLoader(), clz.getName());
        if (pi.args != null) {
            fragment.setArguments(pi.args);
        }
        return fragment;
    }

    /**
     * 获取数量
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * 设置一个pagerInfo的列表,展示到ViewPager2中
     *
     * @param l
     */
    public void setPagerInfo(List<PagerInfo> l) {
        if (list != null) {
            list.clear();
        } else {
            list = new ArrayList<>();
        }
        list.addAll(l);
    }

    /**
     * 一个类用来确定一个ViewPager2中展示的fragment
     */
    public static class PagerInfo {
        private String title;
        private Class<?> clz;
        private Bundle args;

        public PagerInfo(String title, Class<?> clx, Bundle args) {
            this.title = title;
            this.clz = clx;
            this.args = args;
        }
    }
}
