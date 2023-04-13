package com.scu.guanyan.event;

import androidx.annotation.NonNull;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2022/11/22 16:35
 * @description:基础事件
 **/
public abstract class BaseEvent {
    private String flag;
    private String msg;
    private boolean ok;

    public BaseEvent(String flag, String msg, boolean ok) {
        this.flag = flag;
        this.msg = msg;
        this.ok = ok;
    }

    public BaseEvent(){}

    public String getFlag() {
        return flag;
    }

    public String getMsg() {
        return msg;
    }

    public boolean isOk() {
        return ok;
    }

    @NonNull
    @Override
    public String toString() {
        return "flag: " + flag + ",msg: " + msg + "," + ok;
    }
}
