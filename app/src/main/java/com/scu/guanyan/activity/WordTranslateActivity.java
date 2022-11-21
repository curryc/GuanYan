package com.scu.guanyan.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.scu.guanyan.R;
import com.scu.guanyan.base.BaseActivity;
import com.scu.guanyan.event.SignEvent;
import com.scu.guanyan.utils.sign.AvatarPaint;
import com.scu.guanyan.utils.sign.SignTranslator;
import com.scu.guanyan.widget.SignView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class WordTranslateActivity extends BaseActivity {
    private static String TAG = "TranslateActivity";

    private SignView mSignView;
    private EditText mEditText;
    private Button mSubmit;
    private SignTranslator mTranslator;
    private String mWords;
    private AvatarPaint mPainter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mTranslator = new SignTranslator(this, TAG);
        mPainter = new AvatarPaint(mSignView, mTranslator.getMode());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mPainter.destroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_trans_word;
    }

    @Override
    protected void initView() {
        mEditText = findViewById(R.id.input);
        mSubmit = findViewById(R.id.submit);
        mSignView = findViewById(R.id.sign);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTranslator.translate(mEditText.getText().toString(), 0);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleData(SignEvent event) {
        if (event.getFlag().equals(TAG)) {
            mWords = event.getMsg();
            Log.e(TAG, mWords);
            mPainter.startAndPlay();
        }
    }
}