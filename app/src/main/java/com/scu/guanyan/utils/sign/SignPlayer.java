package com.scu.guanyan.utils.sign;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.unity3d.player.UnityPlayer;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2022/12/19 22:20
 * @description:播放手语动画的工具
 **/
public class SignPlayer {
    private String mModelName = "kong";// 可以改变的模型名称
    private String mModelAction = "control";

    private Context mContext;
    private UnityPlayer mUnityPlayer;
    private ViewGroup mContainer;

    private static SignPlayer sSignPlayer;

    public static SignPlayer with(Context context){
        if(sSignPlayer == null) {
            sSignPlayer = new SignPlayer(context);
            sSignPlayer.resume();
        }
        return sSignPlayer;
    }

    public SignPlayer(Context context, ViewGroup container) {
        mContext = context;
        mContainer = container;
        mUnityPlayer = new UnityPlayer(context);
        View mView = mUnityPlayer.getView();
        container.addView(mView);
        mUnityPlayer.requestFocus();
    }

    public SignPlayer(Context context){
        mContext = context;
        mUnityPlayer = new UnityPlayer(mContext);
    }

    public SignPlayer setContainer(ViewGroup container){
        mContainer = container;
        View mView = mUnityPlayer.getView();
        container.addView(mView);
        mUnityPlayer.requestFocus();
        return this;
    }

    /**
     * 向模型发送消息
     * @param text
     */
    public void sendMessage(String text){
        UnityPlayer.UnitySendMessage(
                mModelName,
                mModelAction,
                text);
    }
    public View getView(){
        return mContainer;
    }

    /**
     * 生命周期控制
     */
    public void pause(){
        mUnityPlayer.pause();
    }
    public void resume(){
        mUnityPlayer.resume();
    }
    public void destroy(){
        mUnityPlayer.destroy();
    }
    public void lowMemory(){
        mUnityPlayer.lowMemory();
    }
    public void windowFocusChanged(boolean hasFocus){
        mUnityPlayer.windowFocusChanged(hasFocus);
    }
    public void configurationChanged(Configuration config){
        mUnityPlayer.configurationChanged(config);
    }
    public boolean injectEvent(KeyEvent event){
        return mUnityPlayer.injectEvent(event);
    }
    public void newIntent(Intent intent){
        mUnityPlayer.newIntent(intent);
    }

    /**
     * 设置播放器的模型
     * @param modelName
     */
    public void setModel(String modelName){
        this.mModelName = modelName;
    }
}
