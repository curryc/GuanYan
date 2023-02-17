package com.scu.guanyan.activity;

import android.view.View;

import com.scu.guanyan.R;
import com.scu.guanyan.base.BaseActivity;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2022/11/23 14:28
 * @description:关于软件
 **/
public class AboutAppActivity extends BaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void initView() {
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
