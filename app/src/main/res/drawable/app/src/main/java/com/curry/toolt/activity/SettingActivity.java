package com.curry.toolt.activity;

import android.widget.FrameLayout;
import androidx.appcompat.widget.Toolbar;
import com.curry.toolt.R;
import com.curry.toolt.base.TopToolbarActivity;

public class SettingActivity extends TopToolbarActivity {


    @Override
    protected void initContainer(FrameLayout container) {

    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        toolbar.setTitle(getString(R.string.drawer_setting));
    }
}