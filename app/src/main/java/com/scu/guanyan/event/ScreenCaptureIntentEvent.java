package com.scu.guanyan.event;

import android.content.Intent;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2023/4/13 9:39
 * @description:
 **/
public class ScreenCaptureIntentEvent extends BaseEvent{
    private int mResultCode;
    private Intent mData;

    public ScreenCaptureIntentEvent(String tag, String msg, boolean ok, int resultCode, Intent data){
        super(tag, msg, ok);
        this.mResultCode = resultCode;
        this.mData = data;
    }

    public Intent getData() {
        return mData;
    }

    public int getResultCode() {
        return mResultCode;
    }
}
