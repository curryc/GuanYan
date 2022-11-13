package com.scu.guanyan.ui.translate;

import static com.scu.guanyan.base.ViewHolder.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;

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

public class translate extends BaseActivity {
    private long starTime ;
    private long costTime ;
    private SignGenerator signGenerator;
    private GeneratorSetting setting;
    //处理 callback
    private GeneratorCallback callback =  new GeneratorCallback() {
        @Override
        public void onError(String s, SignPalError signPalError) {

        }

        @Override
        public void onWarning(String s, SignPalWarning signPalWarning) {

        }

        @Override
        public void onSignDataAvailable(String s, SignMotionFragment signMotionFragment, Pair<Integer, Integer> pair, Bundle bundle) {

        }

        @Override
        public void onEvent(String taskId, int eventId, Bundle bundle) {
            switch (eventId) {
                case GeneratorConstants.EVENT_START:
                    starTime = System.currentTimeMillis();
                    break;
                case GeneratorConstants.EVENT_STOP:
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
    }

    private void InitHuawei(){

        String token="DAEDAKyby4AgjiQeVTi5JI4MP/pU9g7YZeCRaD3qXyLI1ckglxYM0Wavtg3RZ0fbKUpsLYoPVXJk4V6rDzfiD4xZ78loPchRlWXfWQ==";
        SignPalApplication.getInstance().setApiKey(token);
        setting = new GeneratorSetting().setLanguage(GeneratorConstants.CN_CSL);
        signGenerator  = new SignGenerator(setting);
        signGenerator.setCallback(callback);

    }
    private  void permission(){

    }
    public void click(View v){
        try{
            InitHuawei();
        }catch (Exception e){
            this.finish();
        }
    }

}