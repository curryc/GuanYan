package com.scu.guanyan.ui.home;


import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.scu.guanyan.IAidlServiceToMain;
import com.scu.guanyan.R;
import com.scu.guanyan.activity.AudioTranslateActivity;
import com.scu.guanyan.activity.SegmentTranslateActivity;
import com.scu.guanyan.event.BaseEvent;
import com.scu.guanyan.widget.DialogMessage;
import com.scu.guanyan.base.BaseFragment;
import com.scu.guanyan.base.ViewHolder;
import com.scu.guanyan.service.FloatWindowService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @program: Guanyan
 * @author: 陈博文
 * @create: 2022-11-07 17:03
 * @description:
 **/
public class HomeFragment extends BaseFragment {
    public static final String TAG = "homeFragment";
    private Button mWordTrans, mAudioTrans, mFloatTrans, mSegmentTrans;
    private int mFloatingFlag;// 0:未悬浮，1：悬浮手势， 2：悬浮文字
    private IAidlServiceToMain  mProxy;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initView(ViewHolder viewHolder, View root) {
        mWordTrans = viewHolder.getViewById(R.id.translate);
        mAudioTrans = viewHolder.getViewById(R.id.audio_trans);
        mFloatTrans = viewHolder.getViewById(R.id.floating_window);
        mSegmentTrans = viewHolder.getViewById(R.id.segment_trans);

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
                        getActivity().bindService(new Intent(getContext(), FloatWindowService.class), new SignConnection(), Context.BIND_AUTO_CREATE);

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
                    try {
                        mProxy.closeService();
//                    getActivity().stopService(new Intent(getActivity(), FloatWindowService.class));
//                    EventBus.getDefault().post(new FloatCloseEvent(FloatWindowService.TAG, getString(R.string.float_trans), true));
                        mFloatingFlag = 0;
                        mFloatTrans.setText(getResources().getText(R.string.float_trans));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        mSegmentTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SegmentTranslateActivity.class);
                startActivity(intent);
            }
        });
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(BaseEvent event){
        if(event.getFlag().equals(TAG)){
            if(event.getMsg().equals("float closed")) {
                this.mFloatTrans.setText(getString(R.string.float_trans));
                this.mFloatingFlag = 0;
            }
        }
    }

    private class SignConnection implements ServiceConnection {

        //当服务连接成功调用
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //获取中间人对象
            mProxy = IAidlServiceToMain.Stub.asInterface(service);
            Log.i(TAG, mProxy.toString());
        }

        //失去连接
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
}

