package com.scu.guanyan.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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

public abstract class BaseBackActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.BackAppTheme);
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            int[] colors = {getColor(R.color.app_color),getColor(R.color.white)};
            GradientDrawable background = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
            background.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            actionBar.setBackgroundDrawable(background);
            actionBar.setTitle(getWindowTitle());
            Drawable upArrow = ContextCompat.getDrawable(this, androidx.appcompat.R.drawable.abc_ic_ab_back_material);
            if(upArrow != null) {
                upArrow.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_ATOP);
                actionBar.setHomeAsUpIndicator(upArrow);
            }
            actionBar.setHideOnContentScrollEnabled(false);
            actionBar.setHomeButtonEnabled(true);//设置左上角的图标是否可以点击
            actionBar.setDisplayHomeAsUpEnabled(true);//给左上角图标的左边加上一个返回的图标


        }
    }


    public abstract String getWindowTitle();

}