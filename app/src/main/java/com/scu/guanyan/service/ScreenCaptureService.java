package com.scu.guanyan.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.scu.guanyan.R;
import com.scu.guanyan.event.BaseEvent;
import com.scu.guanyan.event.ScreenCaptureResultEvent;
import com.scu.guanyan.utils.ORC.OCRUtils;
import com.scu.guanyan.utils.screen.ScreenCapture;
import com.scu.guanyan.widget.BoxDrawingView;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2023/4/13 17:34
 * @description:
 **/
public class ScreenCaptureService extends Service {
    private final String TAG = "screenCaptureService";

    private static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "screen_recorder";


    private int[] mPos;
    private ScreenCapture mCapture;
    private OCRUtils mOcr;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        start();
    }

    private void start(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "screen_recorder", importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Screen Recorder")
                .setContentText("Recording screen...")
                .setSmallIcon(R.drawable.record)
                .build();
        startForeground(NOTIFICATION_ID, notification);

        mCapture = new ScreenCapture(getApplicationContext());
        mOcr = new OCRUtils(this);

        mCapture.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        float[] data = (float[])intent.getExtras().get(FloatWindowService.BOX_TAG);
        mPos = new int[4];
        mPos[0] = (int)data[0];
        mPos[1] = (int)data[1];
        mPos[2] = (int)(data[2] - data[0]);
        mPos[3] = (int)(data[3] = data[1]);
        Log.i(TAG, "bind");
        mCapture.setCaptureListener(new ScreenCapture.OnCaptureListener() {
            @Override
            public void onScreenCaptureSuccess(Bitmap bitmap) {
                System.out.println(mPos[0]);
                Bitmap res = Bitmap.createBitmap(bitmap, mPos[0],mPos[1],mPos[2],mPos[3]);
                String s = mOcr.setImgAndRunModel(res);
                Log.i(TAG, s == null ? "null" : s);
                EventBus.getDefault().post(new ScreenCaptureResultEvent(FloatWindowService.TAG, s,"screen capture" ,true));
            }

            @Override
            public void onScreenCaptureFailed(String errorMsg) {
                Toast.makeText(getApplicationContext(), "获取屏幕信息失败", Toast.LENGTH_SHORT).show();
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
        mCapture.end();
        mCapture.cleanup();
        mCapture = null;
    }
}
