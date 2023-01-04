package com.scu.guanyan.utils.sign;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import com.huawei.hms.signpal.GeneratorCallback;
import com.huawei.hms.signpal.GeneratorConstants;
import com.huawei.hms.signpal.GeneratorSetting;
import com.huawei.hms.signpal.SignGenerator;
import com.huawei.hms.signpal.SignMotionFragment;
import com.huawei.hms.signpal.SignPalError;
import com.huawei.hms.signpal.SignPalWarning;
import com.huawei.hms.signpal.common.agc.SignPalApplication;
import com.scu.guanyan.event.SignEvent;
import com.scu.guanyan.utils.base.PermissionUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2022/11/15 14:59
 * @description:调用华为翻译api的工具，对外接口
 **/
public class SignTranslator {
    private String apiKEY = "DAEDAKyby4AgjiQeVTi5JI4MP/pU9g7YZeCRaD3qXyLI1ckglxYM0Wavtg3RZ0fbKUpsLYoPVXJk4V6rDzfiD4xZ78loPchRlWXfWQ==";

    private String TAG = "signTranslate";

    private long mStartTime, mCostTime;
    private SignGenerator mSignGenerator;
    private GeneratorSetting mSetting;
    private Context mContext;
    private String mFlag;
    private int mMode;

    public int getMode() {
        return mMode;
    }

    public void setMode(int mode) {
        mMode = mode;
    }

    public SignTranslator(Context context, String flag, int mode) {
        mMode = mode;
        mContext = context;
        this.mFlag = flag;
        SignPalApplication.getInstance().setApiKey(apiKEY);
        //SignPalApplication.getInstance().setAccessToken(token);
        mSetting = new GeneratorSetting().setLanguage(GeneratorConstants.CN_CSL);
        mSignGenerator = new SignGenerator(mSetting);
        mSignGenerator.setCallback(callback);
    }


    public SignTranslator(Context context, String flag) {
        this(context, flag, GeneratorConstants.QUEUE_MODE);
    }

    private GeneratorCallback callback = new GeneratorCallback() {
        @Override
        public void onError(String s, SignPalError signPalError) {

        }

        @Override
        public void onWarning(String s, SignPalWarning signPalWarning) {

        }

        @Override
        public void onSignDataAvailable(String taskId, SignMotionFragment signFragment, Pair<Integer, Integer> range, Bundle bundle) {
            // 获取手语动作数据
            ArrayList<Map<String, float[]>> motionDataList = signFragment.getSignMotionDataMap();
            // 获取表情数据
            int[] faceArr = signFragment.getFaceMotion();
            // 获取口型BlendShape驱动数据，如果不设置开启则为空数组
            float[] faceBlendShape = signFragment.getFaceBlendShapeArray();
            // 获取骨骼数据
            ArrayList<FrameData> frameDataList = new ArrayList<>();
            for (int i = 0; i < signFragment.getFrameCount(); i++) {
                FrameData frameData = new FrameData();
                frameData.setMotionData(motionDataList.get(i));
                frameData.setMouthType(faceBlendShape[i]);
                frameData.setFaceType(faceArr[i]);
                frameData.setFrameIdx(i);
                frameDataList.add(frameData);
            }
            EventBus.getDefault().post(new SignEvent(mFlag, "available", true, frameDataList));
        }

        @Override
        public void onEvent(String taskId, int eventId, Bundle bundle) {
            switch (eventId) {
                case GeneratorConstants.EVENT_START:
                    mStartTime = System.currentTimeMillis();
                    break;
                case GeneratorConstants.EVENT_STOP:
                    boolean isInterrupted = bundle.getBoolean(GeneratorConstants.EVENT_STOP_INTERRUPTED);
                    break;
                case GeneratorConstants.EVENT_DOWNLOADING:
                    // 任务下载中
                    mCostTime = System.currentTimeMillis() - mStartTime;
                    Log.d(TAG, String.format("task: %s ,time cost:%s", taskId, mCostTime));

                    break;
                default:
                    break;

            }
        }
    };

    public void translate(String text, int mode) {
        mMode = mode;
        mSignGenerator.text2SignMotion(text, mode);
        Avatar.getInstance().initBone();
    }

    public void translate(String text) {
//        Avatar.getInstance().initBone();
        mSignGenerator.text2SignMotion(text, mMode);
        Avatar.getInstance().initBone();
    }

    public void destroy() {
        mSignGenerator.shutdown();
    }
}
