package com.scu.guanyan.event;

import java.util.List;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2023/4/13 17:51
 * @description:
 **/
public class ScreenCaptureResultEvent extends BaseEvent{
    private List<String> data;

    public ScreenCaptureResultEvent(String flag, List<String> data,String msg, boolean ok){
        super(flag, msg, ok);
        this.data = data;
    }

    public List<String> getData() {
        return data;
    }
}
