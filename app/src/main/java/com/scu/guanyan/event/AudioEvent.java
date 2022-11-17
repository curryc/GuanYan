package com.scu.guanyan.event;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2022/11/15 15:48
 * @description:语音识别转文字的Event
 **/
public class AudioEvent {
    private String flag;
    private String msg;
    private boolean ok;
    private String data;

    public AudioEvent(String flag, String msg, boolean ok, String data) {
        this.flag = flag;
        this.msg = msg;
        this.ok = ok;
        this.data = data;
    }

    public String getFlag() {
        return flag;
    }


    public String getMsg() {
        return msg;
    }


    public boolean isOk() {
        return ok;
    }


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
