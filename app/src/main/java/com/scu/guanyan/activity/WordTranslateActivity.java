package com.scu.guanyan.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.huawei.hms.signpal.GeneratorConstants;
import com.scu.guanyan.R;
import com.scu.guanyan.base.BaseActivity;
import com.scu.guanyan.event.BaseEvent;
import com.scu.guanyan.event.SignEvent;
import com.scu.guanyan.utils.sign.Avatar;
import com.scu.guanyan.utils.sign.AvatarPaint;
import com.scu.guanyan.utils.sign.SignTranslator;
import com.scu.guanyan.widget.FlowLayout;
import com.scu.guanyan.widget.SignView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.scu.guanyan.MyUnityPlayer;
import com.unity3d.player.UnityPlayer;

public class WordTranslateActivity extends BaseActivity {
    private static String TAG = "TranslateActivity";

    private SignView mSignView;
    private EditText mEditText;
    private Button mSave,mSubmit;
    private FlowLayout mCommonWords;
    private ViewGroup.LayoutParams mBubbleParams;

    private SignTranslator mTranslator;
    private AvatarPaint mPainter;

    private LinearLayout unityLayout;
    private MyUnityPlayer mUnityPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_trans_word);
        EventBus.getDefault().register(this);

        unityLayout = findViewById(R.id.unityLayout);
        getWindow().setFormat(PixelFormat.RGBX_8888); // <--- This makes xperia play happy
        // 创建Unity视图
        mUnityPlayer = new MyUnityPlayer(this);
        // 添加Unity视图
        unityLayout.addView(mUnityPlayer.getView());
        mUnityPlayer.requestFocus();
//        Avatar.getInstance().initBone();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mTranslator = new SignTranslator(this, TAG);
        mPainter = new AvatarPaint(mUnityPlayer, mTranslator.getMode());
        mPainter.startAndPlay();

    }

//    @Override protected void onNewIntent(Intent intent) {
//        // To support deep linking, we need to make sure that the client can get access to
//        // the last sent intent. The clients access this through a JNI api that allows them
//        // to get the intent set on launch. To update that after launch we have to manually
//        // replace the intent with the one caught here.
//        super.onNewIntent(intent);
//        setIntent(intent);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnityPlayer.quit();
        EventBus.getDefault().unregister(this);
        mTranslator.destroy();
        mPainter.destroy();
    }

    // Pause Unity
    @Override protected void onPause()
    {
        super.onPause();
        mUnityPlayer.pause();
    }

    // Resume Unity
    @Override protected void onResume()
    {
        super.onResume();
        mUnityPlayer.resume();
    }

    // Low Memory Unity
    @Override public void onLowMemory()
    {
        super.onLowMemory();
        mUnityPlayer.lowMemory();
    }

    // Trim Memory Unity
    @Override public void onTrimMemory(int level)
    {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_RUNNING_CRITICAL)
        {
            mUnityPlayer.lowMemory();
        }
    }

    // This ensures the layout will be correct.
    @Override public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mUnityPlayer.configurationChanged(newConfig);
    }

    // Notify Unity of the focus change.
    @Override public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        mUnityPlayer.windowFocusChanged(hasFocus);
    }

    // For some reason the multiple keyevent type is not supported by the ndk.
    // Force event injection by overriding dispatchKeyEvent().
    @Override public boolean dispatchKeyEvent(KeyEvent event)
    {
        if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
            return mUnityPlayer.injectEvent(event);
        return super.dispatchKeyEvent(event);
    }

    // Pass any events not handled by (unfocused) views straight to UnityPlayer
    @Override public boolean onKeyUp(int keyCode, KeyEvent event)     { return mUnityPlayer.injectEvent(event); }
    @Override public boolean onKeyDown(int keyCode, KeyEvent event)   { return mUnityPlayer.injectEvent(event); }
    @Override public boolean onTouchEvent(MotionEvent event)          { return mUnityPlayer.injectEvent(event); }
    /*API12*/ public boolean onGenericMotionEvent(MotionEvent event)  { return mUnityPlayer.injectEvent(event); }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_trans_word;
    }

    @Override
    protected void initView() {
        mEditText = findViewById(R.id.input);
        mSave = findViewById(R.id.save);
        mSubmit = findViewById(R.id.submit);
        mSignView = findViewById(R.id.sign);
        mCommonWords = findViewById(R.id.common_words);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTranslator.translate(mEditText.getText().toString());
            }
        });
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = mEditText.getText().toString();
                if(!str.equals("")){
                    mCommonWords.addView(generateBubble(str));
                }
            }
        });
    }

    private TextView generateBubble(String text){
        if(mBubbleParams == null){
            mBubbleParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
//            mBubbleParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        TextView bubble = new Button(this);
        bubble.setText(text);
        bubble.setLayoutParams(mBubbleParams);
        bubble.setPadding(15, 2, 15,2);
        bubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTranslator.translate(text);
            }
        });
        return bubble;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleData(BaseEvent event) {
        if(event.getFlag().equals(TAG)) {
            if (event instanceof SignEvent) {
                if(event.isOk()) {
//                    Log.e(TAG, event.getMsg());
                    // 模式不同， 可能会clear所有帧（flush模式）
//                    if(mTranslator.getMode() == GeneratorConstants.FLUSH_MODE){
//                        mPainter.clearFrameData();
//                    }
                    mPainter.addFrameDataList(((SignEvent) event).getFrames());
                }else{
                    Log.e(TAG, event.getMsg());
                }
            }
        }
    }
}