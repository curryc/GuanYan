package com.scu.guanyan.event;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2022/11/22 16:35
 * @description:基础事件
 **/
public class BaseEvent {
    private String flag;
    private String msg;
    private boolean ok;

    public BaseEvent(String flag, String msg, boolean ok) {
        this.flag = flag;
        this.msg = msg;
        this.ok = ok;
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
}
