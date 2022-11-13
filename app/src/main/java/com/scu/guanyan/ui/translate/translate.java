package com.scu.guanyan.ui.translate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.huawei.hms.signpal.GeneratorConstants;
import com.huawei.hms.signpal.GeneratorSetting;
import com.huawei.hms.signpal.SignGenerator;
import com.huawei.hms.signpal.common.agc.SignPalApplication;
import com.scu.guanyan.R;

public class translate extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

    }

    private void InitHuawei(){

        String token="CB8AFD8B9AAD3C251F2ABF44D8F876874086C92472D7C26582E3F39510B024B8";
        GeneratorSetting setting = new GeneratorSetting().setLanguage(GeneratorConstants.CN_CSL);
        SignGenerator signGenerator  = new SignGenerator(setting);
        // 过程中更新
        signGenerator.updateSetting(setting);
        SignPalApplication.getInstance().setAccessToken(token);
    }
}