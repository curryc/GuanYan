package com.scu.guanyan.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.scu.guanyan.R;
import com.scu.guanyan.base.BaseActivity;
import com.scu.guanyan.event.SignEvent;
import com.scu.guanyan.utils.sign.SignTranslator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class WordTranslateActivity extends BaseActivity {
    private static String TAG = "TranslateActivity";

    private EditText mEditText;
    private Button mSubmit;
    private SignTranslator mTranslator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mTranslator = new SignTranslator(this, TAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_trans_word;
    }

    @Override
    protected void initView() {
        mEditText = findViewById(R.id.input);
        mSubmit = findViewById(R.id.submit);
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
            Log.e(TAG, event.getMsg());
        }
    }
}