package com.scu.guanyan.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.scu.guanyan.R;
import com.scu.guanyan.base.BaseActivity;
import com.scu.guanyan.utils.sign.AvatarPaint;
import com.scu.guanyan.utils.sign.SignTranslator;
import com.scu.guanyan.widget.SettingEntry;

import java.util.HashMap;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2022/12/13 21:31
 * @description:
 **/
public class MoreSettingActivity extends BaseActivity {
    private LinearLayout mSettingList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_more_setting;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        mSettingList = findViewById(R.id.setting_list);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        addItems();
    }

    private void addItems() {
        mSettingList.addView(new SettingEntry(this, getString(R.string.is_flash), SignTranslator.FLASH_KEY, new HashMap() {{
            put("yes", 1);
            put("no", 0);
        }}));

        mSettingList.addView(new SettingEntry(this, getString(R.string.fps), AvatarPaint.ANIM_SPEED, new HashMap() {{
            put("12 fps", 12);
            put("30 fps", 30);
            put("45 fps", 45);
            put("60 fps", 60);
        }}));
    }
}
