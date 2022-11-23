package com.scu.guanyan.ui.home;


import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.scu.guanyan.R;
import com.scu.guanyan.activity.AudioTranslateActivity;
import com.scu.guanyan.base.BaseFragment;
import com.scu.guanyan.base.ViewHolder;
import com.scu.guanyan.service.FloatWindowService;
import com.scu.guanyan.utils.base.PermissionUtils;

/**
 * @program: Guanyan
 * @author: 陈博文
 * @create: 2022-11-07 17:03
 * @description:
 **/
public class HomeFragment extends BaseFragment {
    private Button mWordTrans, mAudioTrans, mFloatTrans;
    private int mFloatingFlag;// 0:未悬浮，1：悬浮手势， 2：悬浮文字

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(ViewHolder viewHolder, View root) {
        mWordTrans = viewHolder.getViewById(R.id.translate);
        mAudioTrans = viewHolder.getViewById(R.id.audio_trans);
        mFloatTrans = viewHolder.getViewById(R.id.floating_window);

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
                if (mFloatingFlag == 0) {
                    // 调用悬浮窗服务
                    Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getActivity().getPackageName()));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    boolean canFloat;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        canFloat = Settings.canDrawOverlays(getActivity());
                    } else {
                        canFloat = PermissionUtils.checkPermissionSecond(getActivity(), 0, Manifest.permission.SYSTEM_ALERT_WINDOW, i);
                    }
                    if (canFloat) {
                        getActivity().startService(new Intent(getActivity(), FloatWindowService.class));
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                        startActivity(i);
                    }
                    // 设置状态
                    mFloatTrans.setText("FLOATING");
                    mFloatingFlag = 1;
                } else if (mFloatingFlag == 1) {
                    getActivity().stopService(new Intent(getActivity(), FloatWindowService.class));
                    mFloatingFlag = 0;
                }
            }
        });
    }
}
