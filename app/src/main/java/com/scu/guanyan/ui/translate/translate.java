package com.scu.guanyan.ui.translate;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.huawei.hms.signpal.GeneratorCallback;
import com.huawei.hms.signpal.GeneratorConstants;
import com.huawei.hms.signpal.GeneratorSetting;
import com.huawei.hms.signpal.SignGenerator;
import com.huawei.hms.signpal.SignMotionFragment;
import com.huawei.hms.signpal.SignPalError;
import com.huawei.hms.signpal.SignPalWarning;
import com.huawei.hms.signpal.common.agc.SignPalApplication;
import com.scu.guanyan.R;
import com.scu.guanyan.base.BaseActivity;
import com.scu.guanyan.utils.audio.real_time_words;

import java.util.ArrayList;
import java.util.Map;

public class translate extends BaseActivity {

    private static String TAG = "TranslateActivity";
    private long starTime ;
    private long costTime ;
    private TextView text;
    private Button start;
    private Button end;
    private SignGenerator signGenerator;
    private GeneratorSetting setting;
    private EditText input;
    private real_time_words real_time_words;
    //处理 callback
    private GeneratorCallback callback =  new GeneratorCallback() {
        @Override
        public void onError(String s, SignPalError signPalError) {

        }

        @Override
        public void onWarning(String s, SignPalWarning signPalWarning) {

        }

        @Override
        public void onSignDataAvailable(String taskId, SignMotionFragment signFragment, Pair<Integer, Integer> range, Bundle bundle) {
            // 获取手语动作数据
            ArrayList<Map<String,float[]>> motionDataList= signFragment.getSignMotionDataMap();
            // 获取表情数据
            int[] faceArr = signFragment.getFaceMotion();
            // 获取口型BlendShape驱动数据，如果不设置开启则为空数组
            float[] faceBlendShape = signFragment.getFaceBlendShapeArray();
            // 手语动作表情绘制，需要您自行实现
            String str="";
            for(Map<String,float[]> mf:motionDataList) {
                for (float f[] :mf.values()){
                    for(float a: f)
                    {
                        str=str+Float.toString(a);
                    }
                }
            }
            Log.e(TAG,"faceBlendShape:"+str);
        }
        @Override
        public void onEvent(String taskId, int eventId, Bundle bundle) {
            switch (eventId) {
                case GeneratorConstants.EVENT_START:
                    starTime = System.currentTimeMillis();
                    break;
                case GeneratorConstants.EVENT_STOP:
                    boolean isInterrupted = bundle.getBoolean(GeneratorConstants.EVENT_STOP_INTERRUPTED);
                    break;
                case GeneratorConstants.EVENT_DOWNLOADING:
                    // 任务下载中
                    costTime = System.currentTimeMillis() - starTime;
                    Log.d(TAG, String.format("task: %s ,time cost:%s", taskId, costTime));

                    break;
                default:
                    break;

            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_translate;
    }

    @Override
    protected void initView() {

        input=findViewById(R.id.input);
        start=findViewById(R.id.start);
        end=findViewById(R.id.end);
        text=findViewById(R.id.text);
        init_audio();
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_PHONE_STATE
        }, 0);
        InitHuawei();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        real_time_words.destroy();
    }

    private void InitHuawei(){
        String apiKEY="DAEDAKyby4AgjiQeVTi5JI4MP/pU9g7YZeCRaD3qXyLI1ckglxYM0Wavtg3RZ0fbKUpsLYoPVXJk4V6rDzfiD4xZ78loPchRlWXfWQ==";
        //String token="";
        SignPalApplication.getInstance().setApiKey(apiKEY);
        //SignPalApplication.getInstance().setAccessToken(token);
        setting = new GeneratorSetting().setLanguage(GeneratorConstants.CN_CSL);
        signGenerator  = new SignGenerator(setting);
        signGenerator.setCallback(callback);
        //handler.post(runnable);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            handler.post(this);
        }
    };
    private Handler handler = new Handler();

    public void click(View v){
        String msg=  input.getText().toString();
        String id = signGenerator.text2SignMotion("我恨你", GeneratorConstants.QUEUE_MODE);
    }

    private  void init_audio(){
        real_time_words=new real_time_words(this,getBaseContext(),text);

    }
    public void start(View v){
        real_time_words.start();
         }
    public void end(View v){
        real_time_words.end();
        }
}