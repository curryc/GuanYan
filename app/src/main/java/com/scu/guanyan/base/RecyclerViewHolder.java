package com.scu.guanyan.base;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import org.jetbrains.annotations.NotNull;

/**
 * @program: GuanYan
 * @author: 陈博文
 * @create: 2022-11-13 16:20
 * @description: 一个ViewHolder
 **/
public abstract class RecyclerViewHolder extends RecyclerView.ViewHolder{
    private final String TAG = "base recycler view holder";

    private final SparseArray<View> mViews = new SparseArray<>();

    public RecyclerViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
    }


    /**
     * 获取根View
     * @return
     */
    public View getRootView(){
        return itemView;
    }

    /**
     * 在根view中获取一个子view
     * @param id 子view 的id
     * @param <T> 子view
     * @return
     */
    public <T extends View> T getViewById(int id){
        return (T) bindView(id);
    }

    private <T extends View> T bindView(int id) {
        T view = (T) mViews.get(id);
        if (view == null) {
            view = (T) itemView.findViewById(id);
            mViews.put(id, view);
        }
//        T view = itemView.findViewById(id);
        return view;
    }

    /**
     * 设置clickListener
     * @param listener ClickListener
     * @param ids 一系列需要设置这个listener的id
     */
    public void setOnClickListener(View.OnClickListener listener, int...ids){
        if(listener == null)return;
        for (int id : ids) {
            getViewById(id).setOnClickListener(listener);
        }
    }

    /**
     * 设置clickListener，直接通过指定View进行设置
     * @param listener
     * @param views
     */
    public void setOnClickListener(View.OnClickListener listener, View...views){
        for (View view : views) {
            view.setOnClickListener(listener);
        }
    }

    /**
     * 在子view中设置Text
     * @param layoutId
     * @param text
     * @return
     */
    public boolean setText(int layoutId, String text) {
        try {
            TextView view = getViewById(layoutId);
            view.setText(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 在子view中设置图片的src
     * @param layoutId
     * @param src
     * @return
     */
    public boolean setSrc(int layoutId, int src) {
        try {
            ImageView view = getViewById(layoutId);
            view.setImageResource(src);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 在子view中loadImage
     * @param context
     * @param url
     * @param res_id
     */
    public void loadImage(Context context, String url, int res_id) {
        ImageView imageView = getViewById(res_id);
        Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(imageView);
    }

    abstract public void bind(Object data);
}
