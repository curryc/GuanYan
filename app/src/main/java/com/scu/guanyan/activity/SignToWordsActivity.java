package com.scu.guanyan.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.google.android.material.snackbar.Snackbar;
import com.huawei.hms.signpal.GeneratorConstants;
import com.scu.guanyan.R;
import com.scu.guanyan.base.BaseActivity;
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

public class SignToWordsActivity extends BaseActivity {
    private static String TAG = "classicalTranslateActivity";
    private int CAMERA_STATE_CODE = 0x00;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_trans_sign;
    }

    @Override
    protected void initView() {

    }
}