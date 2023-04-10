package com.scu.guanyan.event;

import com.scu.guanyan.service.FloatWindowService;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2023/4/8 22:56
 * @description:
 **/
public class FloatClosingEvent extends BaseEvent {
    public FloatClosingEvent(String flag, boolean ok) {
        super(flag, "0", ok);
    }
}
