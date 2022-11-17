package com.scu.guanyan.activity;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.scu.guanyan.R;
import com.scu.guanyan.base.BaseActivity;
import com.scu.guanyan.event.AudioEvent;
import com.scu.guanyan.utils.audio.RealTimeWords;
import com.scu.guanyan.utils.base.PermissionUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2022/11/17 15:36
 * @description:音频翻译
 **/
public class AudioTranslateActivity extends BaseActivity {
    private static String TAG = "AudioTranslateActivity";

    private Button mBegin,mEnd;
    private RealTimeWords mAudioUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        if(!PermissionUtils.checkPermissionFirst(AudioTranslateActivity.this, 0, new String[]{Manifest.permission.RECORD_AUDIO})){
            // 用户不给权限
            mBegin.setEnabled(false);
            mEnd.setEnabled(false);
        }
        mAudioUtils = new RealTimeWords(AudioTranslateActivity.this, TAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mAudioUtils.destroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_trans_audio;
    }

    @Override
    protected void initView() {
        mBegin = findViewById(R.id.begin);
        mEnd = findViewById(R.id.end);
        mBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAudioUtils.start();
                Toast.makeText(AudioTranslateActivity.this, "正在进行录音中...", Toast.LENGTH_SHORT).show();
            }
        });
        mEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAudioUtils.end();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleData(AudioEvent event){
        if(event.isOk()){
            Log.i(TAG,event.getData());
        }else{
            if(event.getMsg().equals("error")){
                Log.e(TAG, event.getData());
            }
        }
    }
}
