package com.scu.guanyan.base;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.WindowInsetsController;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.scu.guanyan.MainActivity;
import com.scu.guanyan.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * baseActivity 需要承担：
 * 在任意位置添加删除一个fragment
 * 点击右上角回退
 */
public abstract class BaseActivity extends AppCompatActivity {

    private Fragment mFragment;
    private long mBackPressedTime;
    private List<TurnBackListener> mTurnBackListeners = new ArrayList<>();
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 改进: 继承不彻底
        setContentView(getLayoutId());
        initData();
        initView();
//        IMMLeaks.fixFocusedViewLeak(this.getApplication()); // 修复 InputMethodManager 引发的内存泄漏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().getInsetsController().setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return false;
    }

    /**
     * 在一个特定位置添加一个fragment
     * @param containerId
     * @param fragment
     */
    public void addFragment(int containerId, Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (fragment.isAdded()) {
                if (mFragment != null) {
                    transaction.hide(mFragment).show(fragment);
                } else {
                    transaction.show(fragment);
                }
            } else {
                if (mFragment != null) {
                    transaction.hide(mFragment).add(containerId, fragment);
                } else {
                    transaction.add(containerId, fragment);
                }
            }
            mFragment = fragment;
            transaction.commit();
            getSupportFragmentManager().executePendingTransactions();
        }
    }


    /**
     * 两种toast
     *
     * @param text
     * @param time
     */
    private void toast(String text, int time) {
        if (mToast == null) {
            mToast = Toast.makeText(getApplicationContext(), text, time);
        } else {
            mToast.setText(text);
            mToast.setDuration(time);
        }
        mToast.show();
    }

    /**
     * 长时间Toast
     * @param text
     */
    public void toastLong(String text) {
        toast(text, Toast.LENGTH_LONG);
    }

    /**
     * 短时间Toast
     * @param text
     */
    public void toastShort(String text) {
        toast(text, Toast.LENGTH_SHORT);
    }

    /**
     * 点击回退的接口,通过这个接口可以设置回退实现的动作
     */
    public interface TurnBackListener {
        boolean onTurnBack();
    }

    /**
     * 向Activity中添加回退动作
     * @param l
     */
    public void addOnTurnBackListener(TurnBackListener l) {
        this.mTurnBackListeners.add(l);
    }

    /**
     * 实现回退
     */
    @Override
    public void onBackPressed() {
        for (TurnBackListener l : mTurnBackListeners) {
            if (l.onTurnBack()) return;
        }
        if (this instanceof MainActivity) {
            long curTime = SystemClock.uptimeMillis();
            if ((curTime - mBackPressedTime) < (3 * 1000)) {
                finish();
            } else {
                mBackPressedTime = curTime;
                toastLong(this.getString(R.string.tip_double_click_exit));
            }
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 进入activity
     *
     * @return
     */
    public <T extends Serializable> void openActivity(@Nullable Context context, Class<?> clz, String key, T value) {
        if (context == null) context = this;
        Intent i = new Intent(context, clz);
        i.putExtra(key, value);
        context.startActivity(i);
    }

    /**
     * 获取当前Activity的layout
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 初始化数据
     */
    protected void initData() {
    }

    /**
     * 初始化视图
     */
    protected abstract void initView();
}