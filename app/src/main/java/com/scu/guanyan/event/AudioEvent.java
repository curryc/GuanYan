package com.scu.guanyan.event;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2022/11/15 15:48
 * @description:语音识别转文字的Event
 **/
public class AudioEvent extends BaseEvent{
    private String data;

    public AudioEvent(String flag, String msg, boolean ok, String data) {
        super(flag, msg, ok);
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
