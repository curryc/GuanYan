package com.scu.guanyan.utils.sign;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.scu.guanyan.R;
import com.scu.guanyan.base.BaseUnityPlayer;
import com.unity3d.player.UnityPlayer;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2022/12/19 22:20
 * @description:播放手语动画的工具
 **/
public class SignPlayer {

    private Context mContext;
    private BaseUnityPlayer mUnityPlayer;
    private ViewGroup mContainer;

    private ImageView mLoading;
    private static SignPlayer sSignPlayer;

    private List<Integer> mContainerIds;

    public static SignPlayer with(Context context, ViewGroup parent) {
        Log.i("sign player", parent.getId() + "/"+String.valueOf(sSignPlayer==null));
        if (sSignPlayer == null) {
            Log.i("sign player", "created");
            sSignPlayer = new SignPlayer(context, parent);
        } else {
            sSignPlayer.setContainer(parent);
        }
        return (SignPlayer) sSignPlayer;
    }

    public SignPlayer setContainer(ViewGroup container) {
        mUnityPlayer.init(mUnityPlayer.getSettings().getInt("gles_mode", 1), false);
        View mView = mUnityPlayer.getView();
        mUnityPlayer.resume();
        mContainerIds.add(container.getId());
//        mContainer.removeView(mView);
        if (mView.getParent() != null) {
            ((ViewGroup) mView.getParent()).removeView(mView);
        }
        mContainer = container;
        mContext = container.getContext();
        container.addView(mView);
        mUnityPlayer.requestFocus();

        mLoading = new ImageView(container.getContext());
        Glide.with(container.getContext()).load(R.drawable.origin_loading).into(mLoading);
        mLoading.setVisibility(View.INVISIBLE);
        ViewGroup.LayoutParams params = new FrameLayout.LayoutParams(120, 120, Gravity.TOP | Gravity.LEFT);
        container.addView(mLoading, params);
        return this;
    }

    public ViewGroup getContainer() {
        return mContainer;
    }

    private SignPlayer(Context context, ViewGroup container) {
        mContext = context;
        mContainer = container;
        mContainerIds = new ArrayList<>();
        mContainerIds.add(mContainer.getId());
        mUnityPlayer = new BaseUnityPlayer(context);
        container.addView(mUnityPlayer.getView());


        mUnityPlayer.setFocusable(false);
        mLoading = new ImageView(context);
        Glide.with(context).load(R.drawable.origin_loading).into(mLoading);
        mLoading.setVisibility(View.INVISIBLE);
        ViewGroup.LayoutParams params = new FrameLayout.LayoutParams(120, 120, Gravity.TOP | Gravity.LEFT);
        container.addView(mLoading, params);
    }

    public void loading() {
        mLoading.setVisibility(View.VISIBLE);
    }

    public void prepare() {
        mLoading.setVisibility(View.INVISIBLE);
    }

    private SignPlayer(Context context) {
        mContext = context;
        mUnityPlayer = new BaseUnityPlayer(mContext);
    }


    /**
     * 向模型发送消息
     *
     * @param text
     */

    public static void sendMessage(String modelName, String action, String text) {
        UnityPlayer.UnitySendMessage(
                modelName,
                action,
                text
        );
    }

    public View getView() {
        return mUnityPlayer.getView();
    }

    /**
     * 生命周期控制
     */
    public void pause() {
        mUnityPlayer.pause();
    }

    public void start() {
        mUnityPlayer.start();
    }

    public void stop() {
        mUnityPlayer.stop();
    }

    public void resume() {
        mUnityPlayer.resume();
        mUnityPlayer.postInvalidate();
    }

    public void destroy() {
        Log.i("Guanyan", "signplayer  destroyed");
        mUnityPlayer.destroy();
    }

    public void quit() {
        mUnityPlayer.quit();
    }

    public void lowMemory() {
        mUnityPlayer.lowMemory();
    }

    public void windowFocusChanged(boolean hasFocus) {
        mUnityPlayer.windowFocusChanged(hasFocus);
    }

    public void configurationChanged(Configuration config) {
        mUnityPlayer.configurationChanged(config);
    }

    public boolean injectEvent(KeyEvent event) {
        return mUnityPlayer.injectEvent(event);
    }

    public void requestFocus() {
        mUnityPlayer.requestFocus();
    }

    public Context getContext() {
        return mContext;
    }
}
