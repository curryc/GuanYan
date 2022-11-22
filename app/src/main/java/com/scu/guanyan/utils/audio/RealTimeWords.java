/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.scu.guanyan.utils.audio;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.huaweicloud.sdk.core.utils.JsonUtils;
import com.scu.guanyan.event.AudioEvent;

import org.greenrobot.eventbus.EventBus;

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
 *
 * @浦博威 2022/11/14
 */

public class RealTimeWords {
    private RasrClient rasrClient;
    // 实时显示识别的结果
    private StringBuffer realTimeResult;
    private Context mContext;
    private AudioRecordService audioRecordService;
    private AuthInfo authInfo;
    public String words = "";

    private String flag;


    /**
     * 构造函数
     */
    public RealTimeWords(Context context, String flag) {
        mContext = context;
        this.flag = flag;
        initResources();
    }

    /**
     * 释放资源，销毁，在activity使用onDestroy之前一定要用
     */
    public void destroy() {
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
//            mContext.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(mContext.getApplicationContext(), "实时语音单句模式长连接失败" + JsonUtils.toJSON(asrResponse), Toast.LENGTH_SHORT).show();
//                }
//            });
            EventBus.getDefault().post(new AudioEvent(flag, "error", false, "实时语音单句模式长连接失败"));
            rasrClient.close();
        }
    };

    /**
     * 获取语音转化为文字
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
//            mContext.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    for (int i = 0; i < message.getSegments().size(); i++) {
//                        AsrResponse.Segment segment = message.getSegments().get(i);
//                        // 实时语音识别连续模式 回调结果更新到界面UI中
//                        // 这里很重要
//                        words = realTimeResult.toString() + segment.getResult().getText();
//                        if (segment.getIsFinal()) {
//                            realTimeResult.append(segment.getResult().getText());
//                        }
//                    }
//                }
//            });
            for (int i = 0; i < message.getSegments().size(); i++) {
                AsrResponse.Segment segment = message.getSegments().get(i);
                // 实时语音识别连续模式 回调结果更新到界面UI中
                // 这里很重要
                words = realTimeResult.toString() + segment.getResult().getText();
                if (segment.getIsFinal()) {
                    realTimeResult.append(segment.getResult().getText());
                }
            }
            EventBus.getDefault().post(new AudioEvent(flag, "done", true, realTimeResult.toString()));
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
//            mContext.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(mContext.getApplicationContext(), "实时语音识别连续模式，错误响应:" + JsonUtils.toJSON(response), Toast.LENGTH_SHORT).show();
//                }
//            });
            EventBus.getDefault().post(new AudioEvent(flag, "error",false, "实时语音识别连续模式，错误响应:" + JsonUtils.toJSON(response)));
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
    }

    /**
     * 识别结束
     */
    public void end() {
        if (audioRecordService.getIsRecording().get()) {
            audioRecordService.stopAudioRecord();
            Toast.makeText(mContext, "识别结束...", Toast.LENGTH_SHORT).show();
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
}
