package com.scu.guanyan.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.scu.guanyan.R;
import com.scu.guanyan.event.AudioEvent;
import com.scu.guanyan.event.BaseEvent;
import com.scu.guanyan.event.SignEvent;
import com.scu.guanyan.utils.audio.RealTimeWords;
import com.scu.guanyan.utils.sign.AvatarPaint;
import com.scu.guanyan.utils.sign.SignTranslator;
import com.scu.guanyan.widget.SignView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2022/1/17 16:20
 * * @description:悬浮窗管理，悬浮翻译调用此服务
 **/
public class FloatWindowService extends Service {
    private final String TAG = "FloatWindowService";
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private SignTranslator mTranslator;
    private AvatarPaint mPainter;
    private RealTimeWords mAudioUtils;
    private Handler mHandler;
    private Runnable mViewChecker;

    private final int INITIAL_WIDTH = 300;
    private final int INITIAL_HEIGHT = 400;
    private final int INITIAL_X = 0;
    private final int INITIAL_Y = 400;
    private final int VIEW_CHECK_TIME_MILLIS = 2000;

    private View mDisplayView;
    private SignView mSignView;
    private ImageView mAudio;

    private boolean isRecord = false;
    public boolean isStarted = false;

    @Override
    public void onCreate() {
        super.onCreate();
        isStarted = true;
        isRecord = false;
        mAudioUtils = new RealTimeWords(this, TAG);
        mTranslator = new SignTranslator(this, TAG);
        mPainter = new AvatarPaint(mSignView, mTranslator.getMode());
        mHandler = new Handler();
        mViewChecker = new Runnable() {
            @Override
            public void run() {
                if(mAudio.getVisibility() == View.VISIBLE){
                    mAudio.setVisibility(View.GONE);
                }
            }
        };

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
        EventBus.getDefault().register(this);
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
        EventBus.getDefault().unregister(this);
        windowManager.removeView(mDisplayView);
        windowManager = null;
        mAudioUtils.destroy();
        mTranslator.destroy();
        mPainter.destroy();
    }

    private void showFloatingWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        mDisplayView = layoutInflater.inflate(R.layout.float_sign, null);
        mAudio = mDisplayView.findViewById(R.id.audio);
        mSignView = mDisplayView.findViewById(R.id.sign);

        mDisplayView.setOnTouchListener(new FloatingOnTouchListener());

        mAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isRecord) {
                    isRecord = true;
                    mAudio.setImageResource(R.drawable.ic_pause);
                    mAudioUtils.start();
                    checkViewAfter();
//                    toastShort("正在录音...");
                }else{
                    isRecord = false;
                    mAudio.setImageResource(R.drawable.ic_play);
                    mAudioUtils.end();
                }
            }
        });

        windowManager.addView(mDisplayView, layoutParams);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleData(BaseEvent event){
        if(event.getFlag().equals(TAG)) {
            if(event instanceof AudioEvent){
                Log.i(TAG, ((AudioEvent)event).getData());
                mTranslator.translate(((AudioEvent)event).getData());
            }else if(event instanceof SignEvent){
                // 模式不同， 可能会clear所有帧（flush模式）
//                    if(mTranslator.getMode() == 1){
//                        mPainter.clearFrameData();
//                    }
                mPainter.addFrameDataList(((SignEvent) event).getFrames());
                mPainter.startAndPlay();
            }
        }
    }

    private void checkViewAfter(){
        mHandler.postDelayed(mViewChecker, VIEW_CHECK_TIME_MILLIS);
    }


    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x,y;
        private int ini_x, ini_y;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    ini_x = x = (int) event.getRawX();
                    ini_y = y = (int) event.getRawY();
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
                case MotionEvent.ACTION_UP:
                    if(Math.abs((int)event.getRawX() - ini_x) < 10 &&
                            Math.abs((int)event.getRawY() - ini_y) < 10){
                        if(mAudio.getVisibility() != View.VISIBLE){
                            mAudio.setVisibility(View.VISIBLE);
                            checkViewAfter();
                        }else if(mAudio.getVisibility() == View.VISIBLE){
                            mAudio.setVisibility(View.GONE);
                        }
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    }
}
