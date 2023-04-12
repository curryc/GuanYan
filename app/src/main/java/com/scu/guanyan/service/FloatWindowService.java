package com.scu.guanyan.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.huawei.hms.signpal.GeneratorConstants;
import com.scu.guanyan.IAidlServiceToMain;
import com.scu.guanyan.R;
import com.scu.guanyan.Receiver.MainProcessReceiver;
import com.scu.guanyan.activity.DragToBoxActivity;
import com.scu.guanyan.event.AudioEvent;
import com.scu.guanyan.event.BaseEvent;
import com.scu.guanyan.event.BoxSelectEvent;
import com.scu.guanyan.event.SignEvent;
import com.scu.guanyan.utils.audio.RealTimeWords;
import com.scu.guanyan.utils.base.SharedPreferencesHelper;
import com.scu.guanyan.utils.sign.AvatarPaint;
import com.scu.guanyan.utils.sign.SignPlayer;
import com.scu.guanyan.utils.sign.SignTranslator;
import com.scu.guanyan.widget.BoxDrawingView;
import com.scu.guanyan.widget.DialogSelect;
import com.scu.guanyan.widget.SettingEntry;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2022/1/17 16:20
 * @description: 悬浮窗管理，悬浮翻译调用此服务
 **/
public class FloatWindowService extends Service {
    public static final String TAG = "FloatWindowService";
    public static final String BOX_TAG = "box_tag";

    private enum STATUS {
        RECORD,
        RECORD_STOP,
        CUT,
        CUT_STOP,
        SETTING
    }


    private final int INITIAL_WIDTH = 500;
    private final int INITIAL_HEIGHT = 700;
    private final int INITIAL_X = 0;
    private final int INITIAL_Y = 900;
    private final int VIEW_CHECK_TIME_MILLIS = 2000;

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private SignTranslator mTranslator;
    private AvatarPaint mPainter;
    private RealTimeWords mAudioUtils;
    private Handler mHandler;

    private View mDisplayView;
    private SignPlayer mUnityPlayer;
    private ImageView mControl, mSetting;
    private LinearLayout mRight;
    private BoxDrawingView.Box mBox;
    private boolean mAudioChannel; // true内部，false外部
    private IBinder mBinder;

    private STATUS status;

    private final CountDownTimer countDownTimer = new CountDownTimer(VIEW_CHECK_TIME_MILLIS, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            // 计时器正在计时中，不执行任何操作
        }

        @Override
        public void onFinish() {
            // 计时器时间到，将View设置为不可见
            if (mRight.getVisibility() == View.VISIBLE)
                mDisplayView.findViewById(R.id.right_menu).setVisibility(View.GONE);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        status = STATUS.RECORD_STOP;
        mBinder = new SignBinder();
        mAudioUtils = new RealTimeWords(this, TAG);
        mAudioChannel = false;
        mTranslator = new SignTranslator(this, TAG, (int) SharedPreferencesHelper.get(this, SignTranslator.FLASH_KEY, GeneratorConstants.FLUSH_MODE));


        mHandler = new Handler(Looper.myLooper());
//        {
//            @Override
//            public void handleMessage(@NonNull Message msg) {
//                if (msg.what == -1) {
//                    EventBus.getDefault().unregister(FloatWindowService.this);
//                    mTranslator.destroy();
//                    mPainter.destroy();
//                    mPainter = null;
//                    mTranslator = null;
////        if(playing){
////            mUnityPlayer.pause();
////            mUnityPlayer.stop();
////        }
////                    mUnityPlayer.destroy();
//                    mUnityPlayer = null;
//                    System.gc();
//
//                    Log.i(TAG, "closing float window");
//                    FloatWindowService.this.stopSelf();
//                }
//            }
//        };

//        mViewChecker = new Runnable() {
//            @Override
//            public void run() {
//                if (mControl.getVisibility() == View.VISIBLE) {
//                    mControl.setVisibility(View.GONE);
//                }
//            }
//        };

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
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = INITIAL_HEIGHT;
        layoutParams.x = INITIAL_X;
        layoutParams.y = INITIAL_Y;

        showFloatingWindow();

        mUnityPlayer.resume();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mUnityPlayer != null)
            mUnityPlayer.windowFocusChanged(true);
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
//        EventBus.getDefault().unregister(FloatWindowService.this);
//        mTranslator.destroy();
//        mPainter.destroy();
//        mPainter = null;
//        mTranslator = null;
////        if(playing){
////            mUnityPlayer.pause();
////            mUnityPlayer.stop();
////        }
//                    mUnityPlayer.destroy();
//        mUnityPlayer = null;
//        System.gc();

        Log.i(TAG, "closing float window");
        super.onDestroy();
    }


    private void showFloatingWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        mDisplayView = layoutInflater.inflate(R.layout.float_sign, null);
        mUnityPlayer = SignPlayer.with(getApplicationContext(), mDisplayView.findViewById(R.id.sign));
        mRight = mDisplayView.findViewById(R.id.right_menu);
        mSetting = mDisplayView.findViewById(R.id.setting);
        mControl = mDisplayView.findViewById(R.id.control);
        mPainter = new AvatarPaint(mUnityPlayer, mTranslator.getMode());

        mDisplayView.findViewById(R.id.sign).setOnTouchListener(new FloatingOnTouchListener());

        mDisplayView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                EventBus.getDefault().post(new FloatCloseEvent(HomeFragment.TAG, getString(R.string.float_trans), true));
//                Log.i("hello","hello close float");
                Intent intent = new Intent(FloatWindowService.this, MainProcessReceiver.class);
                intent.setAction("service closed");
                sendBroadcast(intent);
//                mHandler.sendEmptyMessage(-1);
                destroy();
            }
        });

        mControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status == STATUS.RECORD_STOP) {
                    status = STATUS.RECORD;
                    mControl.setImageResource(android.R.drawable.ic_media_pause);
                    mAudioUtils.start();
                    checkViewAfter();
                } else if (status == STATUS.RECORD) {
                    status = STATUS.RECORD_STOP;
                    mControl.setImageResource(android.R.drawable.ic_media_play);
                    mAudioUtils.end();
                } else if (status == STATUS.CUT_STOP) {
                    status = STATUS.CUT;
                    mControl.setImageResource(android.R.drawable.ic_media_pause);
//                    mAudioUtils.start();
                    checkViewAfter();
                } else if (status == STATUS.CUT) {
                    status = STATUS.CUT_STOP;
                    mControl.setImageResource(android.R.drawable.ic_media_play);
//                    mAudioUtils.end();
                }

            }
        });

        mSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status == STATUS.RECORD || status == STATUS.RECORD_STOP) {
//                    if (status == STATUS.RECORD_STOP) {
//                        status = STATUS.SETTING;
//                        if (!mAudioChannel) {
//                            // 切换到内部
//                            mAudioUtils.setChannel(MediaRecorder.AudioSource.REMOTE_SUBMIX);
//                            Toast.makeText(getApplicationContext(), "已切换到内部声音", Toast.LENGTH_SHORT).show();
//                        } else {
//                            // 切换到外部
//                            mAudioUtils.setChannel(MediaRecorder.AudioSource.MIC);
//                            Toast.makeText(getApplicationContext(), "已切换到麦克风", Toast.LENGTH_SHORT).show();
//                        }
//                        status = STATUS.RECORD_STOP;
//                    } else {
//                        Toast.makeText(getApplicationContext(), "请先暂停录音", Toast.LENGTH_SHORT).show();
//                    }
                } else if (status == STATUS.CUT || status == STATUS.CUT_STOP) {
                    if (status == STATUS.CUT_STOP) {
                        status = STATUS.SETTING;
                        windowManager.addView(generateFullScreenView(), getFullScreenLayoutParams());
                        status = STATUS.CUT_STOP;
                    } else {
                        Toast.makeText(getApplicationContext(), "请先暂停录屏", Toast.LENGTH_SHORT).show();
                    }
                } else if (status == STATUS.SETTING) {

                }
            }
        });

        mDisplayView.findViewById(R.id.change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status == STATUS.RECORD || status == STATUS.RECORD_STOP) {
                    if (status == STATUS.RECORD_STOP) {
                        status = STATUS.SETTING;
                        mAudioUtils.destroy();
                        status = STATUS.CUT_STOP;
                    } else {
                        Toast.makeText(getApplicationContext(), "请先暂停录音", Toast.LENGTH_SHORT).show();
                    }
                } else if (status == STATUS.CUT || status == STATUS.CUT_STOP) {
                    if (status == STATUS.CUT_STOP) {
                        status = STATUS.SETTING;
                        mAudioUtils.destroy();
                        status = STATUS.RECORD_STOP;
                    } else {
                        Toast.makeText(getApplicationContext(), "请先暂停录屏", Toast.LENGTH_SHORT).show();
                    }
                } else if (status == STATUS.SETTING) {

                }
            }
        });

        windowManager.addView(mDisplayView, layoutParams);

    }

    private View generateFullScreenView() {
        View mView = LayoutInflater.from(this).inflate(R.layout.activity_drag_to_box, null);

        mView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                windowManager.removeView(mView);
            }
        });

        mView.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBox = ((BoxDrawingView)mView.findViewById(R.id.box)).getCurrentBox();
                windowManager.removeView(mView);
                Toast.makeText(getApplicationContext(), "setting successfully", Toast.LENGTH_SHORT).show();
            }
        });

        return mView;
    }

    private WindowManager.LayoutParams getFullScreenLayoutParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        params.format = PixelFormat.RGBA_8888;
        params.gravity = Gravity.RIGHT | Gravity.TOP;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;

        return params;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleData(BaseEvent event) {
        if (event.getFlag().equals(TAG)) {
            if (event instanceof AudioEvent) {
                if (event.isOk() && !((AudioEvent) event).getData().equals("")) {
                    Log.i(TAG, ((AudioEvent) event).getData());
                    mPainter.checkAndClear();
                    mTranslator.translate(((AudioEvent) event).getData());
                }
            } else if (event instanceof SignEvent) {
                if (event.isOk() && ((SignEvent) event).getFrames().size() != 0) {
                    mPainter.addFrameDataList(((SignEvent) event).getFrames());
                    if (!mPainter.isPlaying())
                        mPainter.startAndPlay();
                }
            } else if (event instanceof BoxSelectEvent) {
                if (event.isOk()) {
                    this.mBox = ((BoxSelectEvent) event).getBox();
                }
            }
        }
    }

    private void checkViewAfter() {
        if (countDownTimer != null) {
            // 如果计时器已经启动，则取消计时器
            countDownTimer.cancel();
        }
        if (mRight.getVisibility() != View.VISIBLE) {
            mRight.setVisibility(View.VISIBLE);
            countDownTimer.start();
        } else if (mRight.getVisibility() == View.VISIBLE) {
            mRight.setVisibility(View.GONE);
        }
    }


    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x, y;
        private int ini_x, ini_y;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    countDownTimer.cancel();
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
                    if (Math.abs((int) event.getRawX() - ini_x) < 10 &&
                            Math.abs((int) event.getRawY() - ini_y) < 10) {
                        checkViewAfter();
                    }else{
                        if (countDownTimer != null) {
                            // 如果计时器已经启动，则取消计时器
                            countDownTimer.cancel();
                        }
                        countDownTimer.start();
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    }


    // Low Memory Unity
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mUnityPlayer != null)
            mUnityPlayer.lowMemory();
    }

    // Trim Memory Unity
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_RUNNING_CRITICAL) {
            mUnityPlayer.lowMemory();
        }
    }

    // This ensures the layout will be correct.
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mUnityPlayer != null)
            mUnityPlayer.configurationChanged(newConfig);
    }

    public class SignBinder extends IAidlServiceToMain.Stub {

        @Override
        public void closeService() throws RemoteException {
//            android.os.Process.killProcess(android.os.Process.myPid());

//            mHandler.sendEmptyMessage(-1);

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    destroy();
                }
            });
        }
    }

    private void destroy() {
        Log.i(TAG, "closing float window");
        EventBus.getDefault().unregister(FloatWindowService.this);
        mTranslator.destroy();
        mPainter.destroy();
        mAudioUtils.destroy();
        mAudioUtils = null;
        mPainter = null;
        mTranslator = null;
//        if(playing){
//            mUnityPlayer.pause();
//            mUnityPlayer.stop();
//        }
//                    mUnityPlayer.destroy();
        mUnityPlayer = null;
        System.gc();

//                    FloatWindowService.this.stopSelf();

        android.os.Process.killProcess(android.os.Process.myPid());
    }


    // TODO： OCR 文字识别转为手语
    // TODO： 录音的通道改变

}
