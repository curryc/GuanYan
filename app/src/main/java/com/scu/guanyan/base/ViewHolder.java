package com.scu.guanyan.base;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * @program: Toolt
 * @author: 陈博文
 * @create: 2022-06-30 20:01
 * @description: 对View进行基本的储存和获取
 **/
public class ViewHolder {
    public static final String TAG = "ViewHolder";

    private View mRootView;
    private SparseArray<View> mViews;

    public ViewHolder(LayoutInflater inflater, ViewGroup parent, int layoutId) {
        this.mRootView = inflater.inflate(layoutId, parent, false);
        this.mViews = new SparseArray<>();
    }

    /**
     * 通过一个ID获取一个子View
     * @param layoutId
     * @param <T>
     * @return
     */
    public <T extends View> T getViewById(int layoutId) {
        View view = mViews.get(layoutId);
        if (view == null) {
            view = mRootView.findViewById(layoutId);
            mViews.put(layoutId, view);
        }
        if (view == null) {
            Log.e(TAG, "view is null");
        }
        return (T) view;
    }

    /**
     * 获取根View
     * @return
     */
    public View getRootView() {
        return mRootView;
    }

    /**
     * 对子View设置Text
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
            Log.e(TAG, "can set text");
            return false;
        }
    }

    /**
     * 对子View设置image的src
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
            Log.e(TAG, "can set src");
            return false;
        }
    }

    /**
     * 对子View设置backgroundColor
     * @param layoutId
     * @param color
     * @return
     */
    public boolean setBackgroundColor(int color, int...layoutId) {
        for (int i : layoutId) {
            try {
                ImageView view = getViewById(i);
                view.setBackgroundColor(color);
            } catch (Exception e) {
                Log.e(TAG, "can set src");
                return false;
            }
        }
        return true;
    }

    /**
     * 对子View设置ClickListener
     * @param listener
     * @param ids
     * @return
     */
    public boolean setOnClickListener(View.OnClickListener listener, int... ids) {
        if (ids == null) {
            return false;
        } else {
            for (int id : ids) {
                getViewById(id).setOnClickListener(listener);
            }
            return true;
        }
    }

    /**
     * 在子view中加载图片
     * @param context
     * @param url
     * @param res_id
     */
    public void loadImage(Context context, String url, int res_id) {
        ImageView imageView = getViewById(res_id);
        Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(imageView);
    }
}
