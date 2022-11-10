package com.curry.toolt.activity;

import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.curry.function.App;
import com.curry.function.bean.Function;
import com.curry.toolt.R;
import com.curry.toolt.base.TopToolbarActivity;
import com.curry.toolt.provider.FunctionProvider;
import com.curry.util.adpter.SingleTypeAdapter;

public class AllFunctionsActivity extends TopToolbarActivity {
    private RecyclerView mRecyclerView;

    protected void initContainer(FrameLayout container){
        mRecyclerView = new RecyclerView(this);
        container.addView(mRecyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        SingleTypeAdapter mAdapter = new SingleTypeAdapter();
        FunctionProvider provider = new FunctionProvider(this);
        provider.setLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(AllFunctionsActivity.this, SelectFuncActivity.class));
                return false;
            }
        });
        mAdapter.register(Function.class, provider);
        mAdapter.addData(App.getFunctions());

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        toolbar.setTitle(getString(R.string.drawer_all));
    }
}