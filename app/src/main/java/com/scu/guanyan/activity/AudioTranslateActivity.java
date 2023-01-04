package com.scu.guanyan.activity;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.scu.guanyan.R;
import com.scu.guanyan.base.BaseActivity;
import com.scu.guanyan.event.AudioEvent;
import com.scu.guanyan.event.BaseEvent;
import com.scu.guanyan.event.SignEvent;
import com.scu.guanyan.utils.audio.RealTimeWords;
import com.scu.guanyan.utils.base.PermissionUtils;
import com.scu.guanyan.utils.sign.AvatarPaint;
import com.scu.guanyan.utils.sign.SignTranslator;
import com.scu.guanyan.widget.SignView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.scu.guanyan.MyUnityPlayer;
import com.unity3d.player.UnityPlayer;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2022/11/17 15:36
 * @description:音频翻译
 **/
public class AudioTranslateActivity extends BaseActivity {
    private static String TAG = "AudioTranslateActivity";

    private ImageView mAudio, mStop;
    private TextView mHint;
    private SignView mSignView;

    private RealTimeWords mAudioUtils;
    private SignTranslator mTranslator;
    private AvatarPaint mPainter;

    private boolean isRecord;
    private String mWords;

    private MyUnityPlayer mUnityPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        if(!PermissionUtils.checkPermissionFirst(AudioTranslateActivity.this, 0, new String[]{Manifest.permission.RECORD_AUDIO})){
            // 用户不给权限
            mAudio.setEnabled(false);
        }
        mTranslator = new SignTranslator(this, TAG);
        mAudioUtils = new RealTimeWords(AudioTranslateActivity.this, TAG);
        mPainter = new AvatarPaint(mUnityPlayer, mTranslator.getMode());
        isRecord = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mAudioUtils.destroy();
        mTranslator.destroy();
        mPainter.destroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_trans_audio;
    }

    @Override
    protected void initView() {
        mAudio = findViewById(R.id.audio);
//        mStop = findViewById(R.id.stop);
        mSignView = findViewById(R.id.sign);
        mHint = findViewById(R.id.hint);

        mAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isRecord) {
                    isRecord = true;
                    mAudio.setImageResource(R.drawable.ic_pause);
                    mAudioUtils.start();
                    mPainter.startAndPlay();
                    toastShort("正在录音...");
                }else{
                    isRecord = false;
                    mAudio.setImageResource(R.drawable.ic_play);
                    mAudioUtils.end();
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleData(BaseEvent event){
        if(event.getFlag().equals(TAG)) {
            if(event instanceof AudioEvent){
                Log.i(TAG, ((AudioEvent)event).getData());
                mWords = ((AudioEvent)event).getData();
                mHint.setText(mWords);
                mTranslator.translate(mWords);
            }else if(event instanceof SignEvent){
                // 模式不同， 可能会clear所有帧（flush模式）
//                    if(mTranslator.getMode() == 1){
//                        mPainter.clearFrameData();
//                    }
                mPainter.addFrameDataList(((SignEvent) event).getFrames());
            }
        }
    }
}
