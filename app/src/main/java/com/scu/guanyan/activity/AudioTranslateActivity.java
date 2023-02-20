package com.scu.guanyan.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;
import com.scu.guanyan.R;
import com.scu.guanyan.base.BaseActivity;
import com.scu.guanyan.base.BaseUnityActivity;
import com.scu.guanyan.event.AudioEvent;
import com.scu.guanyan.event.BaseEvent;
import com.scu.guanyan.event.SignEvent;
import com.scu.guanyan.utils.audio.RealTimeWords;
import com.scu.guanyan.utils.base.PermissionUtils;
import com.scu.guanyan.utils.sign.AvatarPaint;
import com.scu.guanyan.utils.sign.SignPlayer;
import com.scu.guanyan.utils.sign.SignTranslator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2022/11/17 15:36
 * @description:音频翻译
 **/
public class AudioTranslateActivity extends BaseUnityActivity {
    private static String TAG = "AudioTranslateActivity";
    private static int REQUEST_CODE = 0x002;

    private ImageView mAudio;
    private TextView mHint;

    private RealTimeWords mAudioUtils;
    private SignTranslator mTranslator;
    private AvatarPaint mPainter;

    private boolean isRecord;
    private String mWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        mAudioUtils.destroy();
        mTranslator.destroy();
        mPainter.destroy();
        mUnityPlayer.destroy();
        mTranslator = null;
        mUnityPlayer = null;
        mAudioUtils = null;
        mPainter = null;
        super.onDestroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_trans_audio;
    }

    @Override
    protected void initData() {
        super.initData();
        mTranslator = new SignTranslator(this, TAG);
        mAudioUtils = new RealTimeWords(AudioTranslateActivity.this, TAG);
        mUnityPlayer = SignPlayer.with(this, findViewById(R.id.sign));
        mPainter = new AvatarPaint(mUnityPlayer, mTranslator.getMode());
        isRecord = false;
    }

    @Override
    protected int getUnityContainerId() {
        return R.id.sign;
    }

    @Override
    protected void initOtherViews() {
        mAudio = findViewById(R.id.audio);
        mHint = findViewById(R.id.hint);

        mAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRecord) {
                    if (ActivityCompat.checkSelfPermission(AudioTranslateActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(AudioTranslateActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                    ) {
                        isRecord = true;
                        mAudio.setImageResource(R.drawable.ic_pause);
                        mAudioUtils.start();
                        if (!mPainter.isPlaying())
                            mPainter.startAndPlay();
                        toastShort("正在录音...");
                    } else {
                        Snackbar snack = Snackbar.make(findViewById(R.id.sign), "需要权限", Snackbar.LENGTH_LONG);
                        snack.setAnimationMode(Snackbar.ANIMATION_MODE_FADE);
                        View v = snack.getView();
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) v.getLayoutParams();
                        params.gravity = Gravity.TOP;
                        v.setLayoutParams(params);
                        snack.show();
                        PermissionUtils.checkPermissionFirst(AudioTranslateActivity.this, REQUEST_CODE, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECORD_AUDIO});
                    }
                } else {
                    isRecord = false;
                    mAudio.setImageResource(R.drawable.ic_play);
                    mAudioUtils.end();
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleData(BaseEvent event) {
        if (event.getFlag().equals(TAG)) {
            if (event instanceof AudioEvent) {
                Log.i(TAG, ((AudioEvent) event).getData());
                mWords = ((AudioEvent) event).getData();
                mHint.setText(mWords);
                mPainter.checkAndClear();
                mTranslator.translate(mWords);
            } else if (event instanceof SignEvent) {
                // 模式不同， 可能会clear所有帧（flush模式）
//                    if(mTranslator.getMode() == 1){
//                        mPainter.clearFrameData();
//                    }
                mPainter.addFrameDataList(((SignEvent) event).getFrames());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                isRecord = true;
                mAudio.setImageResource(R.drawable.ic_pause);
                mAudioUtils.start();
                if (!mPainter.isPlaying())
                    mPainter.startAndPlay();
                toastShort("正在录音...");
            }
        }
    }
}
