/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.scu.guanyan.utils.audio;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.huaweicloud.sdk.core.utils.JsonUtils;

import sis.android.sdk.RasrClient;
import sis.android.sdk.bean.AuthInfo;
import sis.android.sdk.bean.SisHttpConfig;
import sis.android.sdk.bean.request.RasrRequest;
import sis.android.sdk.bean.response.AsrResponse;
import sis.android.sdk.exception.SisException;
import sis.android.sdk.listeners.RasrResponseListener;
import sis.android.sdk.listeners.process.RasrConnProcessListener;

/**
 * 实现语音功能的类
 * @浦博威 2022/11/14
 */

public class real_time_words {
    private RasrClient rasrClient;
    // 实时显示识别的结果
    private StringBuffer realTimeResult;
    Activity activity;
    private com.scu.guanyan.utils.audio.AudioRecordService audioRecordService;
    private AuthInfo authInfo;
    public String words="";
    TextView view;

    /**
     * 构造函数
     */
    public real_time_words(Activity act,Context context,TextView text){
        view=text;
        activity=act;
        checkAudioRecordingPermission(activity,  context);
        initResources();
    }


    /**
     * 释放资源，销毁，在activity使用onDestroy之前一定要用
     */
    public void destroy(){
        if (rasrClient != null) {
            rasrClient.close();
        }
        if (audioRecordService != null && audioRecordService.getIsRecording().get()) {
            audioRecordService.stopAudioRecord();
            audioRecordService.releaseAudioRecord();
        }
        System.gc();
    }

    /**
     * 监听类
     */
    public RasrConnProcessListener rasrConnProcessListener = new RasrConnProcessListener() {
        /**
         * 连接关闭后回调
         */
        @Override
        public void onTranscriptionClose() {
            Log.i("info", "长连接关闭");
        }

        /**
         * 连接建立后回调
         */
        @Override
        public void onTranscriptionConnect() {
            Log.i("info", "长连接开始");
        }

        /**
         * 长连接连接失败是回调
         *
         * @param asrResponse 返回体
         */
        @Override
        public void onTranscriptionFail(AsrResponse asrResponse) {
            Log.i("info", "长连接异常");
            // 调用失败给用户提示
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity.getApplicationContext(), "实时语音单句模式长连接失败" + JsonUtils.toJSON(asrResponse), Toast.LENGTH_SHORT).show();
                }
            });
            rasrClient.close();
        }
    };

    /**
     * 获取语音转化为
     */
    public RasrResponseListener rasrResponseListener = new RasrResponseListener() {
        /**
         * 检测到句子开始事件
         */
        @Override
        public void onVoiceStart() {
        }

        /**
         * 检测到句子结束事件
         */
        @Override
        public void onVoiceEnd() {
        }

        /**
         * 返回识别的信息
         * @param message
         */
        @Override
        public void onResponseMessage(AsrResponse message) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < message.getSegments().size(); i++) {
                        AsrResponse.Segment segment = message.getSegments().get(i);
                        // 实时语音识别连续模式 回调结果更新到界面UI中
                        // 这里很重要
                        words=realTimeResult.toString() + segment.getResult().getText();
                        view.setText(segment.getResult().getText());
                    }
                }
            });
        }

        /**
         *
         * 静音超长，也即没有检测到声音。响应事件
         */
        @Override
        public void onExcceededSilence() {
        }

        /**
         * 返回识别的信息
         * @param response
         */
        @Override
        public void onResponseBegin(AsrResponse response) {
        }

        @Override
        public void onResponseEnd(AsrResponse response) {
        }

        @Override
        public void onResponseError(AsrResponse response) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity.getApplicationContext(), "实时语音识别连续模式，错误响应:" + JsonUtils.toJSON(response), Toast.LENGTH_SHORT).show();
                }
            });
            rasrClient.close();
        }
    };

    /**
     * 开始监听
     */
    public void start() {
        realTimeResult = realTimeResult.delete(0, realTimeResult.length());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    rasrClient = new RasrClient(authInfo, rasrResponseListener, rasrConnProcessListener, new SisHttpConfig());
                    rasrClient.rasrContinueStreamConnect();
                    // 建立连接
                    rasrClient.connect();
                    rasrClient.sendStart(getStartRequest());
                    audioRecordService.startSendRecordingData(rasrClient);
                } catch (SisException e) {
                    Log.e("error", e.getErrorCode() + e.getErrorMsg());
                }
            }
        }).start();
       Toast.makeText(activity.getApplicationContext(), "正在进行录音中...", Toast.LENGTH_SHORT).show();
    }

    /**
     * 识别结束
     */
    public void end() {
        if (audioRecordService.getIsRecording().get()) {
            audioRecordService.stopAudioRecord();
            Toast.makeText(activity.getApplicationContext(), "识别结束...", Toast.LENGTH_SHORT).show();
        }
        try {
            rasrClient.sendEnd();
        } catch (SisException e) {
            Log.e("error", e.getErrorCode() + e.getErrorMsg());
        }
        rasrClient.close();
    }

    /**
     * 初始化设置资源
     */
    private void initResources() {
        authInfo = new AuthInfo(com.scu.guanyan.utils.audio.Config.AK, com.scu.guanyan.utils.audio.Config.SK, com.scu.guanyan.utils.audio.Config.REGION, com.scu.guanyan.utils.audio.Config.PROJECT_ID);
        audioRecordService = new com.scu.guanyan.utils.audio.AudioRecordService(16000);
        realTimeResult = new StringBuffer();
    }

    /**
     * 开始请求
     *
     * @return 返回请求体内容
     */
    private RasrRequest getStartRequest() {
        RasrRequest rasrRequest = new RasrRequest();
        rasrRequest.setCommand("START");
        RasrRequest.Config config = new RasrRequest.Config();
        config.setAudioFormat("pcm16k16bit");
        config.setProperty("chinese_16k_general");
        config.setAddPunc("yes");
        config.setInterimResults("yes");
        rasrRequest.setConfig(config);
        return rasrRequest;
    }

    private final String[] permissions = {Manifest.permission.RECORD_AUDIO};
    public void checkAudioRecordingPermission(Activity activity, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            int state = ContextCompat.checkSelfPermission(context, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (state != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                activity.requestPermissions(permissions, 321);
            }
            while (true) {
                state = ContextCompat.checkSelfPermission(context, permissions[0]);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (state == PackageManager.PERMISSION_GRANTED) {
                    break;
                }
            }
        }
    }
}
