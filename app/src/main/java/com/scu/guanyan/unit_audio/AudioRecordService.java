/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.scu.guanyan.unit_audio;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import okio.ByteString;
import sis.android.sdk.RasrClient;
import sis.android.sdk.SasrWsClient;
import sis.android.sdk.exception.SisException;

/**
 * 功能描述
 *
 * @since 2022-08-12
 */
public class AudioRecordService {
    private AudioRecord audioRecord; // 录音对象
    private int channelInConfig = AudioFormat.CHANNEL_IN_MONO; // 定义采样通道
    private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT; // 定义音频编码（16位）
    private byte[] buffer = null; // 录制的缓冲数组

    private int bufferSize;

    private AtomicBoolean isRecording = new AtomicBoolean(false);

    private ByteArrayOutputStream byteArrayOutputStream = null;

    /**
     * @param frequence 采样率
     */
    @SuppressLint("MissingPermission")
    public AudioRecordService(int frequence) {
        // 设置每次传入sdk服务的数组大小
        bufferSize = 3200;
        // 实例化AudioRecord
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                frequence, channelInConfig, audioEncoding, bufferSize);
    }

    /**
     * 功能描述
     * 开始录制音频
     *
     * @return 返回录制的音频字节数组
     */
    public void startRecording() {
        // 开始录制
        isRecording.set(true);
        audioRecord.startRecording();
        byteArrayOutputStream = new ByteArrayOutputStream();
        // 定义缓冲数组
        buffer = new byte[bufferSize];
        new Thread(() -> {
            while (isRecording.get()) {
                // 录制的内容放置到了buffer中，result代表存储长度
                Log.i("info", "语音录入中....");
                int result = audioRecord.read(buffer, 0, buffer.length);
                if (result > 0) {
                    byteArrayOutputStream.write(buffer, 0, buffer.length);
                }
            }
        }).start();
    }

    public void startSendRecordingData(RasrClient asrClient) {
        // 开始录制
        isRecording.set(true);
        audioRecord.startRecording();
        // 定义缓冲数组
        buffer = new byte[bufferSize];
        new Thread(() -> {
            try {
                while (isRecording.get()) {
                    // 录制的内容放置到了buffer中，result代表存储长度
                    int result = audioRecord.read(buffer, 0, buffer.length);
                    if (result > 0) {
                        asrClient.sendByte(buffer,bufferSize,0);
                    }
                }
            } catch (SisException e) {
                Log.e("error", e.toString());
            }
        }).start();
    }

    public AudioRecord getAudioRecord() {
        return audioRecord;
    }

    public void startSendRecordingData(SasrWsClient sasrWsClient) {
        // 开始录制
        isRecording.set(true);
        // 定义缓冲数组
        buffer = new byte[bufferSize];
        new Thread(() -> {
            try {
                while (isRecording.get()) {
                    // 录制的内容放置到了buffer中，result代表存储长度
                    int result = audioRecord.read(buffer, 0, buffer.length);
                    Log.i("info+录入的音频为:", ByteString.of(buffer).toString());
                    if (result > 0) {
                        sasrWsClient.sendByte(buffer,3200,0);
                    }
                }
            } catch (SisException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    /**
     * 功能描述
     * 停止语音
     */
    public void stopAudioRecord() {
        isRecording.set(false);
        audioRecord.stop();
    }

    /**
     * 释放资源
     */
    public void releaseAudioRecord() {
        audioRecord.release();
        audioRecord = null;
    }

    /**
     * 关闭ByteArrayOutputStream输出流
     *
     * @return
     */
    public void closeByteArrayOutStream() {
        try {
            byteArrayOutputStream.close();
        } catch (IOException e) {
            Log.e("error", e.toString());
        }
    }

    public AtomicBoolean getIsRecording() {
        return isRecording;
    }

    public ByteArrayOutputStream getByteArrayOutputStream() {
        return byteArrayOutputStream;
    }
}
