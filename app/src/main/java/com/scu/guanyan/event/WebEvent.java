package com.scu.guanyan.event;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2023/4/13 17:54
 * @description:
 **/
public class WebEvent extends BaseEvent{
    private String data;

    public WebEvent(String flag, String data,String msg, boolean ok){
        super(flag, msg, ok);
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
