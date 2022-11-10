package com.curry.toolt.ui.home;

import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.curry.function.App;
import com.curry.function.bean.FunctionCatalog;
import com.curry.toolt.R;
import com.curry.toolt.base.BaseFragment;
import com.curry.toolt.provider.FunctionCatalogProvider;
import com.curry.util.adpter.HeaderFooterAdapter;
import com.curry.util.view.ViewHolder;

public class HomeFragment extends BaseFragment {
    private final String TAG = "HomeFragment";

    private RecyclerView mRecyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(ViewHolder viewHolder, View root) {
        mRecyclerView = viewHolder.getViewById(R.id.home_container);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        HeaderFooterAdapter mAdapter = new HeaderFooterAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.register(FunctionCatalog.class, new FunctionCatalogProvider(getContext()));
        mAdapter.addData(App.getFunctionCatalog());
    }
}