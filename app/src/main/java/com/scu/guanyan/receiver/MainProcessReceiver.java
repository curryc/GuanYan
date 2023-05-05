package com.scu.guanyan.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.scu.guanyan.event.FloatClosingEvent;
import com.scu.guanyan.ui.home.HomeFragment;

import org.greenrobot.eventbus.EventBus;

public class MainProcessReceiver extends BroadcastReceiver {
    private final String TAG = "GuanyanReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, intent.getComponent().toString());

        String action = intent.getAction();
        switch (action){
            case "service closed":
                EventBus.getDefault().post(new FloatClosingEvent(HomeFragment.TAG, "float closed",true));
                break;
        }
    }
}