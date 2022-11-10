package com.curry.toolt.provider;

import android.content.Context;
import android.view.View;
import com.curry.function.App;
import com.curry.function.bean.Function;
import com.curry.toolt.R;
import com.curry.util.adpter.RecyclerViewHolder;
import com.curry.util.base.BaseViewProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: Toolt
 * @author: 陈博文
 * @create: 2022-07-12 21:08
 * @description: 选择一些功能需要使用的provider
 **/
public class FunctionSelectProvider extends BaseViewProvider<Function> {

    private List<Function> mAdd;
    private List<Function> mSub;
    private List<Function> mCollect;

    /**
     * 通过Contest和RecyclerView.ViewHolder的LayoutID
     *
     * @param mContext 　用来获取inflater
     */
    public FunctionSelectProvider(Context mContext, List<Function> collect) {
        super(mContext, R.layout.holder_func_select);
        mAdd = new ArrayList<>();
        mSub = new ArrayList<>();
        mCollect = collect;
        if (mCollect == null) mCollect = new ArrayList<>();
    }

    @Override
    public void bindView(RecyclerViewHolder holder, Function data) {
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdd.contains(data)) {
                    holder.setSrc(R.id.select_radio, R.drawable.unselective);
                    mAdd.remove(data);
                } else if (checkInCollect(data.getId())) {
                    holder.setSrc(R.id.select_radio, R.drawable.unselective);
                    mSub.add(data);
                } else if (!checkInCollect(data.getId()) && !mAdd.contains(data)) {
                    holder.setSrc(R.id.select_radio, R.drawable.selective);
                    mAdd.add(data);
                } else if (!checkInCollect(data.getId()) && mSub.contains(data)) {
                    holder.setSrc(R.id.select_radio, R.drawable.selective);
                    mSub.remove(data);
                }
            }
        }, R.id.select_holder);


        holder.setText(R.id.select_title, data.getTitle());
        if (App.getDotColor(data.getLevel()) == -1) {
            holder.getViewById(R.id.select_dot).setVisibility(View.GONE);
        } else {
            holder.setSrc(R.id.select_dot, App.getDotColor(data.getLevel()));
        }

        if (data.isCollect()) {
            holder.setSrc(R.id.select_radio, R.drawable.selective);
        } else {
            holder.setSrc(R.id.select_radio, R.drawable.unselective);
        }
    }

    /**
     * 检查当前功能是否在收藏中
     *
     * @param id
     * @return
     */
    private boolean checkInCollect(int id) {
        for (Function function : mCollect) {
            if (function.getId() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断收藏是否改变过
     *
     * @return
     */
    public boolean isCollectChanged() {
        if ((mAdd == null || mAdd.size() == 0) && (mSub == null || mSub.size() == 0)) return false;
        else return true;
    }

    /**
     * 获取当前的收藏
     *
     * @return
     */
    public List<Function> getCollect() {
        for (Function f : mSub) {
            if (mCollect.contains(f)) mCollect.remove(f);
        }
        for (Function f : mAdd) {
            if (!mCollect.contains(f)) mCollect.add(f);
        }

        return mCollect;
    }

    public List<Function> getAdd() {
        return mAdd;
    }

    public List<Function> getSub() {
        return mSub;
    }
}
