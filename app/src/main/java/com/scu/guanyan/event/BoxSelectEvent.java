package com.scu.guanyan.event;

import com.scu.guanyan.widget.BoxDrawingView;
import com.scu.guanyan.widget.BoxDrawingView.Box;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2023/4/12 9:53
 * @description:
 **/
public class BoxSelectEvent extends BaseEvent {
    private Box box;

    public BoxSelectEvent(String tag, String msg, Box box) {
        super(tag, msg, true);
        this.box = box;
    }

    public Box getBox() {
        return box;
    }
}
