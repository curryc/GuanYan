package com.scu.guanyan;

import android.os.Bundle;

import androidx.viewpager2.widget.ViewPager2;

import com.scu.guanyan.base.BaseActivity;
import com.scu.guanyan.base.FragmentAdapter;
import com.scu.guanyan.ui.NavFragment;
import com.scu.guanyan.ui.home.HomeFragment;
import com.scu.guanyan.ui.setting.SettingFragment;
import com.scu.guanyan.ui.teach.TeachFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private NavFragment mNav;
    private ViewPager2 mPager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        mNav = new NavFragment();
        mPager = findViewById(R.id.pager);

        mNav.setUp(new NavFragment.OnTabChanged() {
            @Override
            public void onSelect(int sequence) {
                mPager.setCurrentItem(sequence, true);
            }

            @Override
            public void onReselect(int sequence) {

            }
        });
        addFragment(R.id.nav, mNav);

        mPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), getLifecycle(), getPagers()));
        mPager.setCurrentItem(0, false);
        mPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mNav.doSelect(position);
            }
        });
    }

    private List<FragmentAdapter.PagerInfo> getPagers(){
        List<FragmentAdapter.PagerInfo> l = new ArrayList<>();
        Bundle bundle = new Bundle();
        l.add(new FragmentAdapter.PagerInfo("Home", HomeFragment.class, bundle));
        l.add(new FragmentAdapter.PagerInfo("TEACH", TeachFragment.class, bundle));
        l.add(new FragmentAdapter.PagerInfo("SETTING", SettingFragment.class, bundle));
        return l;
    }
}