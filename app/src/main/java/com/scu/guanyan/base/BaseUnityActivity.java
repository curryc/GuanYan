package com.scu.guanyan.base;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.scu.guanyan.utils.sign.SignPlayer;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2022/12/19 17:14
 * @description:仿照unityPlayActivity写的活动，提供对UnityPlayer的基础生命周期控制
 **/
public abstract class BaseUnityActivity extends BaseActivity {
    protected SignPlayer mUnityPlayer;

    protected boolean playing = false;

    protected abstract int getUnityContainerId();

    protected abstract void initOtherViews();

    protected boolean destroyFlag = false;

    private long resumeTime = 0l;

    Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == -1 && !destroyFlag) {
                destroyAll();
                destroyFlag = true;
                BaseUnityActivity.this.finish();

            }
        }
    };

    protected void destroyAll() {
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUnityPlayer.start();
    }


    // Quit Unity
    @Override
    protected void onDestroy() {
        if (mUnityPlayer != null) {
            mUnityPlayer.destroy();
        }
        System.gc();
        super.onDestroy();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mUnityPlayer != null) mUnityPlayer.stop();
    }

    // Pause Unity
    @Override
    protected void onPause() {
        super.onPause();
        if (mUnityPlayer != null) mUnityPlayer.pause();
    }

    // Resume Unity
    @Override
    protected void onResume() {
        super.onResume();
        resumeTime = System.currentTimeMillis();
        if (mUnityPlayer == null) {
            mUnityPlayer = new SignPlayer(this);
        }
        mUnityPlayer.resume();

        addOnTurnBackListener(new TurnBackListener() {
            @Override
            public boolean onTurnBack() {
//                mHandler.sendEmptyMessage(-1);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // 想要流畅，直接上这个
//                        android.os.Process.killProcess(android.os.Process.myPid());
                        if (!destroyFlag && System.currentTimeMillis() - resumeTime < 2000) {
                            destroyAll();
                            destroyFlag = true;
                            BaseUnityActivity.this.finish();
                        } else {
                            finish();
                        }
                    }
                });
                return true;
            }
        });
    }


    @Override
    protected final void initView() {
        ViewGroup l = findViewById(getUnityContainerId());
        if (mUnityPlayer == null) mUnityPlayer = SignPlayer.with(this, l);
        initOtherViews();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    // Low Memory Unity
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mUnityPlayer != null) mUnityPlayer.lowMemory();
    }

    // Trim Memory Unity
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_RUNNING_CRITICAL) {
            if (mUnityPlayer != null) mUnityPlayer.lowMemory();
        }
    }

    // This ensures the layout will be correct.
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mUnityPlayer != null) mUnityPlayer.configurationChanged(newConfig);
    }

    // Notify Unity of the focus change.
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (mUnityPlayer != null) mUnityPlayer.windowFocusChanged(hasFocus);
    }

    // For some reason the multiple keyevent type is not supported by the ndk.
    // Force event injection by overriding dispatchKeyEvent().
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_MULTIPLE) return mUnityPlayer.injectEvent(event);
        return super.dispatchKeyEvent(event);
    }
}
