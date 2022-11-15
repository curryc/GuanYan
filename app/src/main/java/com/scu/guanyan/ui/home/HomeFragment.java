package com.scu.guanyan.ui.home;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.scu.guanyan.R;
import com.scu.guanyan.base.BaseFragment;
import com.scu.guanyan.base.ViewHolder;

/**
 * @program: Guanyan
 * @author: 陈博文
 * @create: 2022-11-07 17:03
 * @description:
 **/
public class HomeFragment extends BaseFragment {
    private Button translate, real_time_tran, floating_window;

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
        translate = view.findViewById(R.id.translate);
        real_time_tran = view.findViewById(R.id.audio_trans);
        floating_window = view.findViewById(R.id.floating_window);

        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), com.scu.guanyan.activity.translate.class);//想调到哪个界面就把login改成界面对应的activity名
                startActivity(intent);
                int a = 0;
            }
        });

        real_time_tran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getActivity(),translate.class);//想调到哪个界面就把login改成界面对应的activity名
                //startActivity(intent);
            }
        });

        floating_window.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getActivity(),translate.class);//想调到哪个界面就把login改成界面对应的activity名
                //startActivity(intent);
            }
        });
    }

}
