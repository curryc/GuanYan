package com.scu.guanyan.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.scu.guanyan.R;
import com.scu.guanyan.base.BaseActivity;
import com.scu.guanyan.event.BoxSelectEvent;
import com.scu.guanyan.service.FloatWindowService;
import com.scu.guanyan.widget.BoxDrawingView;

import org.greenrobot.eventbus.EventBus;

public class DragToBoxActivity extends BaseActivity {
    private BoxDrawingView mBox;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_drag_to_box;
    }

    @Override
    protected void initView() {
        mBox = findViewById(R.id.box);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new BoxSelectEvent(FloatWindowService.TAG, "", mBox.getCurrentBox()));
                toastShort("setting successfully");
            }
        });
    }
}