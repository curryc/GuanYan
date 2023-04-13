package com.scu.guanyan.event;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2023/4/13 17:51
 * @description:
 **/
public class ScreenCaptureResultEvent extends BaseEvent{
    private String data;

    public ScreenCaptureResultEvent(String flag, String data,String msg, boolean ok){
        super(flag, msg, ok);
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
