package com.curry.toolt.activity;

import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.curry.function.App;
import com.curry.function.Collect;
import com.curry.function.bean.Function;
import com.curry.toolt.R;
import com.curry.toolt.base.TopToolbarActivity;
import com.curry.toolt.provider.FunctionSelectProvider;
import com.curry.util.adpter.SingleTypeAdapter;

import java.util.ArrayList;
import java.util.List;

public class SelectFuncActivity extends TopToolbarActivity {
    public static final String SELECT_COLLECT = "SELECT_COLLECT";

    private RecyclerView mRecyclerView;

    FunctionSelectProvider mProvider;

    @Override
    protected void initContainer(FrameLayout container) {
        mRecyclerView = new RecyclerView(this);
        container.addView(mRecyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        SingleTypeAdapter mAdapter = new SingleTypeAdapter();
        mProvider = new FunctionSelectProvider(this, getCollectFunctions(Collect.getCollectIds()));
        mAdapter.register(Function.class, mProvider);
        mAdapter.addData(App.getFunctions());

        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 通过一系列id获得一些列功能
     * @param ids
     * @return
     */
    private List<Function> getCollectFunctions(List<Integer> ids){
        if(ids == null) return null;
        List<Function> functions =new ArrayList<>();
        for (int id : ids) {
            functions.add(App.getFunctionById(id));
        }
        return functions;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        checkAndStoreCollect();
    }

    private void checkAndStoreCollect() {
        if(mProvider.isCollectChanged()){
            Collect.deleteCollects(mProvider.getSub());
            Collect.putCollects(mProvider.getAdd());
            Intent i = new Intent();
            i.putExtra(SELECT_COLLECT, true);

            this.setResult(RESULT_OK, i);
            finish();
        }
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        toolbar.setTitle(getString(R.string.select));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndStoreCollect();
                finish();
            }
        });
    }
}