package com.scu.guanyan.base;

import android.content.Context;
import android.view.MotionEvent;

import com.unity3d.player.UnityPlayer;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2022/12/29 21:46
 * @description:
 **/
public class BaseUnityPlayer extends UnityPlayer {
    @Override
    public void destroy() {
        return;
    }

    /**
     * 不要捕捉任何事件，这仅仅是个动画播放器
     * @param motionEvent
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    public BaseUnityPlayer(Context context) {
        super(context);
    }

//    @Override
//    protected void kill() {
//
//        return;
//    }
}
