package com.scu.guanyan.ui.teach;

import android.view.View;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.scu.guanyan.R;
import com.scu.guanyan.base.BaseFragment;
import com.scu.guanyan.base.ViewHolder;
import com.scu.guanyan.bean.TeachVideo;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: Guanyan
 * @author: 陈博文
 * @create: 2022-11-07 17:07
 * @description:
 **/
public class TeachFragment extends BaseFragment {
    private RecyclerView mRecyclerView;
    private TeachVideoAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_teach;
    }

    @Override
    protected void initView(ViewHolder viewHolder, View root) {
        mAdapter = new TeachVideoAdapter();
        mAdapter.addData(getSample());

        mRecyclerView = viewHolder.getViewById(R.id.data);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecyclerView.setAdapter(mAdapter);
    }

    private List<TeachVideo> getSample() {
        List<TeachVideo> l = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            l.add(new TeachVideo(i + "th video", null, null));
        }
        return l;
    }
}
