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

import com.scu.guanyan.R;
import com.scu.guanyan.base.BaseActivity;
import com.scu.guanyan.event.BaseEvent;
import com.scu.guanyan.event.SignEvent;
import com.scu.guanyan.utils.sign.AvatarPaint;
import com.scu.guanyan.utils.sign.SignTranslator;
import com.scu.guanyan.widget.FlowLayout;
import com.scu.guanyan.widget.SignView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class WordTranslateActivity extends BaseActivity {
    private static String TAG = "TranslateActivity";

    private SignView mSignView;
    private EditText mEditText;
    private Button mSave,mSubmit;
    private FlowLayout mCommonWords;
    private ViewGroup.LayoutParams mBubbleParams;

    private SignTranslator mTranslator;
    private AvatarPaint mPainter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mTranslator = new SignTranslator(this, TAG);
        mPainter = new AvatarPaint(mSignView, mTranslator.getMode());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mTranslator.destroy();
        mPainter.destroy();
    }

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
        }
        TextView bubble = new Button(this);
        bubble.setText(text);
        bubble.setLayoutParams(mBubbleParams);
        bubble.setPadding(5, 2, 5,2);
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
                    Log.e(TAG, event.getMsg());
                    // 模式不同， 可能会clear所有帧（flush模式）
//                    if(mTranslator.getMode() == 1){
//                        mPainter.clearFrameData();
//                    }
                    mPainter.addFrameDataList(((SignEvent) event).getFrames());
                    mPainter.startAndPlay();
                }else{
                    Log.e(TAG, event.getMsg());
                }
            }
        }
    }
}