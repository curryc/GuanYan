package com.scu.guanyan.activity;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.huawei.hms.signpal.GeneratorConstants;
import com.scu.guanyan.R;
import com.scu.guanyan.base.BaseUnityActivity;
import com.scu.guanyan.event.BaseEvent;
import com.scu.guanyan.event.SignEvent;
import com.scu.guanyan.utils.base.PermissionUtils;
import com.scu.guanyan.utils.base.SharedPreferencesHelper;
import com.scu.guanyan.utils.sign.AvatarPaint;
import com.scu.guanyan.utils.sign.SignPlayer;
import com.scu.guanyan.utils.sign.SignTranslator;
import com.scu.guanyan.utils.word.WordUtil;
import com.scu.guanyan.widget.FlowLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class SegmentTranslateActivity extends BaseUnityActivity {

    private static String TAG = "SegmentTranslate";
    private int READ_PHONE_STATE_CODE = 0x001;

    private EditText mEditText;
    private Button mSubmit;
    private FlowLayout mSegmentWords;
    private ViewGroup.MarginLayoutParams mBubbleParams;
    private String mFirstWord;
    private List<String> mSegments;
    private Handler mSegmentThread;
    private WordUtil mWordUtil;

    private SignTranslator mTranslator;
    private AvatarPaint mPainter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_trans_segment;
    }

    @Override
    protected int getUnityContainerId() {
        return R.id.sign;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initData() {
        super.initData();
        mSegments = new ArrayList<>();

        mTranslator = new SignTranslator(SegmentTranslateActivity.this, TAG, (int) SharedPreferencesHelper.get(SegmentTranslateActivity.this, SignTranslator.FLASH_KEY, GeneratorConstants.FLUSH_MODE));
        mUnityPlayer = SignPlayer.with(SegmentTranslateActivity.this, findViewById(R.id.sign));
        mPainter = new AvatarPaint(mUnityPlayer, mTranslator.getMode());
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mWordUtil = new WordUtil();
            }
        });
    }

    @Override
    protected void destroyAll() {
        EventBus.getDefault().unregister(this);
        mTranslator.destroy();
        mPainter.destroy();
        mPainter = null;
        mTranslator = null;
        mWordUtil = null;
//        if(playing){
//            mUnityPlayer.pause();
//            mUnityPlayer.stop();
//        }
        mUnityPlayer.destroy();
        mUnityPlayer = null;
        System.gc();
    }

    @Override
    protected void initOtherViews() {
        mEditText = findViewById(R.id.input);
        mSubmit = findViewById(R.id.submit);
        mSegmentWords = findViewById(R.id.segment_words);

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = mEditText.getText().toString();
                cutWords(s);
                translate(s);
                if (!mPainter.isPlaying()) {
                    mPainter.startAndPlay();
                    playing = true;
                }
            }
        });
    }

    private void cutWords(String s){
        if(mSegmentThread == null) {
            mSegmentThread = new Handler(getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    mSegmentWords.removeAllViews();
                    List<TextView> seg = generateBubbles(mSegments);
                    for (TextView button : seg) {
                        mSegmentWords.addView(button);
                    }
                }
            };
        }
        mSegmentThread.post(new Runnable() {
            @Override
            public void run() {
                mSegments = mWordUtil.cutSpecial(s);
                mSegmentThread.sendEmptyMessage(1);
            }
        });
    }

    private TextView generateBubble(String text) {
        if (mBubbleParams == null) {
            mBubbleParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
            mBubbleParams.setMargins(5, 2, 5, 2);
        }
        TextView bubble = new TextView(this);
        bubble.setText(text);
        bubble.setTextSize(30);
        bubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translate(text);
                if (!mPainter.isPlaying()) {
                    mPainter.startAndPlay();
                    playing = true;
                }
            }
        });

        bubble.setPadding(5, 0, 5, 0);
        bubble.setBackgroundColor(0xFFDED7CD);
        bubble.setLayoutParams(mBubbleParams);
        return bubble;
    }

    private List<TextView> generateBubbles(List<String> texts) {
        List<TextView> res = new ArrayList<>();
        for (String text : texts) {
            res.add(generateBubble(text));
        }
        return res;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleData(BaseEvent event) {
        if (event.getFlag().equals(TAG)) {
            if (event instanceof SignEvent) {
//                toastShort("" + ((SignEvent) event).getFrames().size());
                if (event.isOk()) {
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
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                translate(mFirstWord);
            }
        }
    }

    private void translate(String text) {
        mPainter.checkAndClear();

        if (ActivityCompat.checkSelfPermission(SegmentTranslateActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            mTranslator.translate(text);
        } else {
            PermissionUtils.checkPermissionFirst(SegmentTranslateActivity.this, READ_PHONE_STATE_CODE, new String[]{Manifest.permission.READ_PHONE_STATE});

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
}