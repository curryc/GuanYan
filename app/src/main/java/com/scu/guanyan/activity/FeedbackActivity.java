package com.scu.guanyan.activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.scu.guanyan.R;
import com.scu.guanyan.base.BaseActivity;
import com.scu.guanyan.event.BaseEvent;
import com.scu.guanyan.utils.base.Web;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2022/12/31 16:42
 * @description: 输入遇到的问题和解决建议
 **/
public class FeedbackActivity extends BaseActivity {
    private final String TAG = "feedbackActivity";

    private EditText mQuestion, mAdvise,mName, mTel;
    private Button mSubmit;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_feedback;
    }

    @Override
    protected void initData() {
        super.initData();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void initView() {
        mQuestion = findViewById(R.id.edit_quz);
        mAdvise = findViewById(R.id.edit_advise);
        mName = findViewById(R.id.edit_name);
        mTel  =findViewById(R.id.edit_phone);
        mSubmit = findViewById(R.id.submit);

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Web.postFeedback(TAG, mQuestion.getText().toString(), mAdvise.getText().toString(), mName.getText().toString(), mTel.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Callback(BaseEvent event){
        if(event.getFlag().equals(TAG)){
            if(event.isOk()){
                // 成功提交反馈
                toastShort("200");
            }
        }
    }
}
