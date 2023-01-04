package com.scu.guanyan.event;

import com.scu.guanyan.utils.sign.FrameData;

import java.util.List;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2022/11/15 15:37
 * @description:手语动作的event
 **/
public class SignEvent extends BaseEvent{
    private List<FrameData> frames;

    public SignEvent(String flag, String msg, boolean ok, List<FrameData> frames) {
        super(flag, msg, ok);
        this.frames = frames;
    }

    public List<FrameData> getFrames() {
        return frames;
    }

    public void setFrames(List<FrameData> frames) {
        this.frames = frames;
    }
}
