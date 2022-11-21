package com.scu.guanyan.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.scu.guanyan.R;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2022/1/17 16:20
 * * @description:悬浮窗管理，悬浮翻译调用此服务
 **/
public class FloatWindowService extends Service {
    private final String TAG = "FloatWindowService";
    public static boolean isStarted = false;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private View mDisplayView;

    private final int INITIAL_WIDTH = 800;
    private final int INITIAL_HEIGHT = 450;
    private final int INITIAL_X = 300;
    private final int INITIAL_Y = 300;

    @Override
    public void onCreate() {
        super.onCreate();
        isStarted = true;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.RIGHT | Gravity.TOP;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = INITIAL_WIDTH;
        layoutParams.height = INITIAL_HEIGHT;
        layoutParams.x = INITIAL_X;
        layoutParams.y = INITIAL_Y;


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showFloatingWindow();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        windowManager.removeView(mDisplayView);
        windowManager = null;
    }

    private void showFloatingWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        mDisplayView = layoutInflater.inflate(R.layout.float_sign, null);
        mDisplayView.setOnTouchListener(new FloatingOnTouchListener());


        windowManager.addView(mDisplayView, layoutParams);
    }

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    layoutParams.x = layoutParams.x - (nowX - x);
                    layoutParams.y = layoutParams.y + (nowY - y);
                    windowManager.updateViewLayout(view, layoutParams);
                    x = nowX;
                    y = nowY;
                    break;
                default:
                    break;
            }
            return true;
        }
    }
}
