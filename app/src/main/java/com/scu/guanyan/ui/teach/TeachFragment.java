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
    private final String PREFIX = "https://www.bilibili.com/video/";

    private RecyclerView mRecyclerView;
    private TeachVideoAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_teach;
    }

    @Override
    protected void initView(ViewHolder viewHolder, View root) {
        mAdapter = new TeachVideoAdapter(getContext());
        mAdapter.addData(getSample());

        mRecyclerView = viewHolder.getViewById(R.id.data);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecyclerView.setAdapter(mAdapter);
    }

    private List<TeachVideo> getSample() {
        List<TeachVideo> l = new ArrayList<>();
        l.add(new TeachVideo("手语基础教程", "http://i2.hdslb.com/bfs/archive/f9d53a166177f274d40ad0049dd298cc3f404eb2.jpg", "https://www.bilibili.com/video/BV1LW411s7gR"));
        l.add(new TeachVideo("中国手语教程零基础学起", "http://i2.hdslb.com/bfs/archive/ada1cd7ad4093e8251a2a800161518d7dcf35a2b.jpg", "https://www.bilibili.com/video/BV1nx411i7gV"));
        l.add(new TeachVideo("中国标准通用手语学习教学", "http://i0.hdslb.com/bfs/archive/ff9dd116c1c9520dc49992d7e5ca9ad66c8a6440.jpg", "https://www.bilibili.com/video/BV1bb411k771"));
        l.add(new TeachVideo("手语配套视频 《中国手语教程》", "http://i1.hdslb.com/bfs/archive/414ecf04b45319a7afb9200f114bf322ac672e98.jpg", "https://www.bilibili.com/video/BV1KA411V7KC"));
        l.add(new TeachVideo("手语教程100句", "http://i0.hdslb.com/bfs/archive/bfd43f4631880f471ec34e2fe018afc0624885b0.jpg", "https://www.bilibili.com/video/BV1W4411d7SX"));
        return l;
    }
}
