package com.scu.guanyan.ui.home;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import androidx.core.app.ActivityCompat;

import com.scu.guanyan.R;
import com.scu.guanyan.activity.AudioTranslateActivity;
import com.scu.guanyan.widget.DialogMessage;
import com.scu.guanyan.base.BaseFragment;
import com.scu.guanyan.base.ViewHolder;
import com.scu.guanyan.service.FloatWindowService;

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
                    boolean canFloat;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        canFloat = Settings.canDrawOverlays(getActivity());
                    } else {
                        startActivity(new Intent(getContext(), DialogMessage.class));
                        canFloat = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.SYSTEM_ALERT_WINDOW) == PackageManager.PERMISSION_GRANTED;
                    }

                    if (canFloat) {
                        getActivity().startService(new Intent(getActivity(), FloatWindowService.class));
                        // 设置状态
                        mFloatingFlag = 1;
                        mFloatTrans.setText(getResources().getText(R.string.floating));
                    } else {
                        DialogMessage dialog = new DialogMessage(getContext());
                        dialog.setTitle(getString(R.string.floating_permission));
                        dialog.setMessage(getString(R.string.description_floating_ask));
                        dialog.setNoOnclickListener(getString(R.string.cancel), new DialogMessage.onNoOnclickListener() {
                            @Override
                            public void onNoClick() {
                                dialog.dismiss();
                                return;
                            }
                        });
                        dialog.setYesOnclickListener(getString(R.string.go_setting), new DialogMessage.onYesOnclickListener() {
                            @Override
                            public void onYesClick() {
                                // 开启悬浮窗权限
                                Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getActivity().getPackageName()));
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                } else if (mFloatingFlag == 1) {
                    getActivity().stopService(new Intent(getActivity(), FloatWindowService.class));
                    mFloatingFlag = 0;
                    mFloatTrans.setText(getResources().getText(R.string.float_trans));
                }
            }
        });
    }

}
