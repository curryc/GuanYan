package com.scu.guanyan.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;
import com.scu.guanyan.R;
import com.scu.guanyan.base.BaseActivity;
import com.scu.guanyan.event.BaseEvent;
import com.scu.guanyan.event.SignEvent;
import com.scu.guanyan.utils.base.PermissionUtils;
import com.scu.guanyan.utils.base.SharedPreferencesHelper;
import com.scu.guanyan.utils.sign.AvatarPaint;
import com.scu.guanyan.utils.sign.SignPlayer;
import com.scu.guanyan.utils.sign.SignTranslator;
import com.scu.guanyan.widget.FlowLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class WordTranslateActivity extends BaseActivity {
    private static String TAG = "TranslateActivity";
    private static String BUBBLE_KEY = "save_words";
    private int READ_PHONE_STATE_CODE = 0x001;

    private SignPlayer mUnityPlayer;
    private EditText mEditText;
    private Button mSave, mSubmit;
    private FlowLayout mCommonWords;
    private ViewGroup.MarginLayoutParams mBubbleParams;
    private String mFirstWord;
    private List<String> mBubbles;

    private Handler mHandler;

    private SignTranslator mTranslator;
    private AvatarPaint mPainter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        EventBus.getDefault().register(this);  // 这一语句建立在activity不是单独的线程，而unity必须在单独的进程中存在
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
//        EventBus.getDefault().unregister(this);
        mTranslator.destroy();
        mPainter.destroy();
        mUnityPlayer.destroy();
        super.onDestroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_trans_word;
    }

    @Override
    protected void initData() {
        super.initData();
        mTranslator = new SignTranslator(this, TAG);
        mPainter = new AvatarPaint(mUnityPlayer, mTranslator.getMode());
//        mPainter.startAndPlay();
        mBubbles = new ArrayList<>();
        mBubbles = SharedPreferencesHelper.getListString(this, BUBBLE_KEY);

        mHandler = new Handler();
    }

    @Override
    protected void initView() {
        mEditText = findViewById(R.id.input);
        mSave = findViewById(R.id.save);
        mSubmit = findViewById(R.id.submit);
        mUnityPlayer = new SignPlayer(this, findViewById(R.id.sign));
        mCommonWords = findViewById(R.id.common_words);

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              translate(mEditText.getText().toString());
            }
        });
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = mEditText.getText().toString();
                if (!str.equals("")) {
                    if (mBubbles.contains(str)) {
                        toastShort("Already In");
                    } else {
                        mCommonWords.addView(generateBubble(str));
                        SharedPreferencesHelper.addListStringItem(WordTranslateActivity.this, BUBBLE_KEY, str);
                    }
                }
            }
        });
        for (String str : mBubbles) {
            mCommonWords.addView(generateBubble(str));
        }
    }

    private TextView generateBubble(String text) {
        if (mBubbleParams == null) {
            mBubbleParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
            mBubbleParams.setMargins(15, 2, 15, 2);
        }
        TextView bubble = new Button(this);
        bubble.setText(text);
        bubble.setTextColor(Color.WHITE);
        bubble.setBackground(getDrawable(R.drawable.round_rec_variant));
        bubble.setLayoutParams(mBubbleParams);
        bubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translate(text);
            }
        });
        return bubble;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleData(BaseEvent event) {
        if (event.getFlag().equals(TAG)) {
            if (event instanceof SignEvent) {
                if (event.isOk()) {
//                    Log.e(TAG, event.getMsg());
                    // 模式不同， 可能会clear所有帧（flush模式）
//                    if(mTranslator.getMode() == GeneratorConstants.FLUSH_MODE){
//                        mPainter.clearFrameData();
//                    }
                    mPainter.addFrameDataList(((SignEvent) event).getFrames());
                } else {
                    Log.e(TAG, event.getMsg());
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_PHONE_STATE_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                translate(mFirstWord);
            }
        }
    }

    private void translate(String text){
        if (ActivityCompat.checkSelfPermission(WordTranslateActivity.this,  Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            mTranslator.translate(text);
        } else {
            PermissionUtils.checkPermissionFirst(WordTranslateActivity.this, READ_PHONE_STATE_CODE, new String[]{Manifest.permission.READ_PHONE_STATE});

            mFirstWord = text;
            Snackbar snack = Snackbar.make(findViewById(R.id.sign), "需要权限", Snackbar.LENGTH_LONG);
            snack.setAnimationMode(Snackbar.ANIMATION_MODE_FADE);
            View v = snack.getView();
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) v.getLayoutParams();
            params.gravity = Gravity.TOP;
            v.setLayoutParams(params);
            snack.show();
        }
    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
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
}