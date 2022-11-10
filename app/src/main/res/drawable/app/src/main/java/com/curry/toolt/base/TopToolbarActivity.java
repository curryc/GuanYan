package com.curry.toolt.base;

import android.widget.FrameLayout;
import androidx.appcompat.widget.Toolbar;
import com.curry.function.App;
import com.curry.toolt.R;

public abstract class TopToolbarActivity extends BaseActivity {
    private Toolbar mToolbar;

    @Override
    protected final int getLayoutId() {
        return R.layout.activity_top_toolbar;
    }

    @Override
    protected final void initView() {
        mToolbar = findViewById(R.id.title_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setBackgroundColor(App.getThemeColor());
        initToolbar(mToolbar);
        initContainer(findViewById(R.id.content_container));
    }

    protected abstract void initContainer(FrameLayout container);

    protected abstract void initToolbar(Toolbar toolbar);
}