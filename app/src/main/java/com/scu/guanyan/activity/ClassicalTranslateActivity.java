package com.scu.guanyan.activity;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
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

import com.google.android.material.snackbar.Snackbar;
import com.huawei.hms.signpal.GeneratorConstants;
import com.scu.guanyan.R;
import com.scu.guanyan.base.BaseUnityActivity;
import com.scu.guanyan.event.BaseEvent;
import com.scu.guanyan.event.SignEvent;
import com.scu.guanyan.event.WebEvent;
import com.scu.guanyan.utils.base.PermissionUtils;
import com.scu.guanyan.utils.base.SharedPreferencesHelper;
import com.scu.guanyan.utils.base.Web;
import com.scu.guanyan.utils.sign.AvatarPaint;
import com.scu.guanyan.utils.sign.SignPlayer;
import com.scu.guanyan.utils.sign.SignTranslator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

public class ClassicalTranslateActivity extends BaseUnityActivity {
    private static String TAG = "classicalTranslateActivity";
    private int READ_PHONE_STATE_CODE = 0x001;

    private EditText mEditText;
    private TextView mTextView;
    private Button mSubmit;
    private String mFirstWord;

    private SignTranslator mTranslator;
    private AvatarPaint mPainter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_trans_classical;
    }

    @Override
    protected void initData() {
        super.initData();

        mTranslator = new SignTranslator(this, TAG, (int) SharedPreferencesHelper.get(this, SignTranslator.FLASH_KEY, GeneratorConstants.FLUSH_MODE));
        mUnityPlayer = SignPlayer.with(this, findViewById(R.id.sign));
        mPainter = new AvatarPaint(mUnityPlayer, mTranslator.getMode());
    }

    @Override
    protected int getUnityContainerId() {
        return R.id.sign;
    }

    @Override
    protected void destroyAll() {
        EventBus.getDefault().unregister(this);
        mTranslator.destroy();
        mPainter.destroy();
        mPainter = null;
        mTranslator = null;
//        if(playing){
//            mUnityPlayer.pause();
//            mUnityPlayer.stop();
//        }
        mUnityPlayer.destroy();
        mUnityPlayer = null;
        ((ViewGroup)findViewById(getUnityContainerId())).removeView(mUnityPlayer.getView());
        System.gc();
    }

    @Override
    protected void initOtherViews() {
        mEditText = findViewById(R.id.input);
        mSubmit = findViewById(R.id.submit);
        mTextView = findViewById(R.id.words);

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mUnityPlayer.loading();
                    Web.postClassicalWords(TAG, mEditText.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleData(BaseEvent event) {
        if (event.getFlag().equals(TAG)) {
            if (event instanceof SignEvent) {
                if (event.isOk()) {
                    mUnityPlayer.prepare();
                    mPainter.addFrameDataList(((SignEvent) event).getFrames());
                } else {
                    Log.e(TAG, event.getMsg());
                }
            }else if(event instanceof WebEvent){
                mTextView.setText(((WebEvent) event).getMsg());
                translate(((WebEvent) event).getMsg());
                if (!mPainter.isPlaying()) {
                    mPainter.startAndPlay();
                    playing = true;
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

        if (ActivityCompat.checkSelfPermission(ClassicalTranslateActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            mTranslator.translate(text);
        } else {
            PermissionUtils.checkPermissionFirst(ClassicalTranslateActivity.this, READ_PHONE_STATE_CODE, new String[]{Manifest.permission.READ_PHONE_STATE});

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