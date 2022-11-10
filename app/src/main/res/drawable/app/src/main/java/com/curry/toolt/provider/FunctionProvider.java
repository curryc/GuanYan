package com.curry.toolt.provider;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.AdapterView;
import com.curry.function.App;
import com.curry.function.bean.Function;
import com.curry.toolt.R;
import com.curry.util.adpter.RecyclerViewHolder;
import com.curry.util.base.BaseViewProvider;

/**
 * @program: Toolt
 * @author: 陈博文
 * @create: 2022-07-12 08:57
 * @description: 在全部应用中展示所有功能
 **/
public class FunctionProvider extends BaseViewProvider<Function> {
    private AdapterView.OnLongClickListener mLongClickListener;
    private View.OnClickListener mOnClickListener;
    private GradientDrawable background;

    /**
     * 通过Contest和RecyclerView.ViewHolder的LayoutID
     *
     * @param mContext 　用来获取inflater
     */
    public FunctionProvider(Context mContext) {
        super(mContext, R.layout.holder_func);

        background = new GradientDrawable();
        background.setTint(App.getThemeColor());
        background.setCornerRadius(mContext.getResources().getDimensionPixelSize(R.dimen.function_rec_radius));
    }

    public FunctionProvider(Context mContext, View.OnLongClickListener longClickListener) {
        this(mContext);
        this.mLongClickListener = longClickListener;
    }

    @Override
    public void bindView(RecyclerViewHolder holder, Function data) {
        holder.getViewById(R.id.holder_fuc_button).setBackground(background);
        holder.setText(R.id.func_title, data.getTitle());
        if (App.getDotColor(data.getLevel()) == -1) {
            holder.getViewById(R.id.func_dot).setVisibility(View.GONE);
        } else {
            holder.setSrc(R.id.func_dot, App.getDotColor(data.getLevel()));
        }

        if (mOnClickListener == null) {
            holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, data.getStarter());
                    mContext.startActivity(i);
                }
            }, holder.getRootView());
        } else {
            holder.setOnClickListener(mOnClickListener, holder.getRootView());
        }
        holder.getRootView().setOnLongClickListener(mLongClickListener);
    }

    /**
     * 设置点击监听, 不设置默认为打开这个按钮的starter
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(View.OnClickListener onItemClickListener) {
        this.mOnClickListener = onItemClickListener;
    }

    /**
     * 设置长按监听
     *
     * @param onLongClickListener
     */
    public void setLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.mLongClickListener = onLongClickListener;
    }
}
