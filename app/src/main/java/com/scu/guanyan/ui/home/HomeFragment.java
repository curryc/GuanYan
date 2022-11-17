package com.scu.guanyan.ui.home;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.scu.guanyan.R;
import com.scu.guanyan.activity.AudioTranslateActivity;
import com.scu.guanyan.base.BaseFragment;
import com.scu.guanyan.base.ViewHolder;

/**
 * @program: Guanyan
 * @author: 陈博文
 * @create: 2022-11-07 17:03
 * @description:
 **/
public class HomeFragment extends BaseFragment {
    private Button mWordTrans, mAudioTrans, mFloatTrans;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(ViewHolder viewHolder, View root) {

    }

    /**
     * 设置按钮功能
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        setButton(view);
    }

    private void setButton(View view) {
        mWordTrans = view.findViewById(R.id.translate);
        mAudioTrans = view.findViewById(R.id.audio_trans);
        mFloatTrans = view.findViewById(R.id.floating_window);

        mWordTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), com.scu.guanyan.activity.WordTranslateActivity.class);
                startActivity(intent);
            }
        });

        mAudioTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AudioTranslateActivity.class);
                startActivity(intent);
            }
        });

        mFloatTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 调用悬浮窗服务
            }
        });
    }

}
