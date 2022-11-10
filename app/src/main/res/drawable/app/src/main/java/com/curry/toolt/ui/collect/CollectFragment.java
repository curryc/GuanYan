package com.curry.toolt.ui.collect;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.curry.function.App;
import com.curry.function.Collect;
import com.curry.function.bean.Function;
import com.curry.toolt.R;
import com.curry.toolt.activity.SelectFuncActivity;
import com.curry.toolt.base.BaseFragment;
import com.curry.toolt.provider.FunctionProvider;
import com.curry.util.adpter.SingleTypeAdapter;
import com.curry.util.view.ViewHolder;
import org.jetbrains.annotations.NotNull;

public class CollectFragment extends BaseFragment implements View.OnClickListener {
    private final String TAG = "collectFragment";

    private boolean mZero;

    private ActivityResultLauncher mLauncher;
    private FrameLayout mContainer;
    private ViewHolder mHolder;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        check();

        if (mLauncher == null) {
            mLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result == null || result.getData() == null) return;
                    check();
                    boolean changed = result.getData().getBooleanExtra(SelectFuncActivity.SELECT_COLLECT,false);
                    if (changed) {
                        // 表示收藏已经变动过
                        updateUI();
                    }
                }
            });
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_collect;
    }

    @Override
    protected void initView(ViewHolder viewHolder, View root) {
        mHolder = viewHolder;
        mContainer = viewHolder.getViewById(R.id.collect_container);
        updateUI();
    }

    /**
     * 更新UI
     */
    private void updateUI() {
        mContainer.removeAllViews();
        if (!mZero) {
            View view = LayoutInflater.from(App.getContext()).inflate(R.layout.fragment_collect_init, mHolder.getViewById(R.id.collect_container), false);
            mContainer.addView(view);
            mHolder.setOnClickListener(this, R.id.collect_add_one);
        } else {
            RecyclerView mRecyclerView = new RecyclerView(this.getContext());
            mRecyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), 2));

            SingleTypeAdapter mAdapter = new SingleTypeAdapter();
            FunctionProvider provider = new FunctionProvider(this.getContext());
            provider.setLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    startActivity(new Intent(getContext(), SelectFuncActivity.class));
                    return false;
                }
            });
            mAdapter.register(Function.class, provider);
            mAdapter.addData(Collect.getCollects());

            mRecyclerView.setAdapter(mAdapter);

            mContainer.addView(mRecyclerView);
        }
    }

    /**
     * 检查当前有无收藏好确定当前布局
     */
    private void check(){
        mZero = true;
        if (Collect.getCollectIds() == null || Collect.getCollectIds().size() == 0) mZero = false;
    }

    @Override
    public void onClick(View v) {
        mLauncher.launch(new Intent(this.getContext(), SelectFuncActivity.class));
    }
}