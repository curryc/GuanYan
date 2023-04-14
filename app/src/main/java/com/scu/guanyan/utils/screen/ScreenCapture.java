package com.scu.guanyan.utils.screen;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import com.scu.guanyan.activity.ScreenCaptureIntentActivity;
import com.scu.guanyan.event.BaseEvent;
import com.scu.guanyan.event.ScreenCaptureIntentEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2023/4/12 23:56
 * @description:
 **/

/**
 * Created by panj on 2017/5/22.
 * Modify by jambestwick on 2021/10/05 fix record when close bug
 */

public class ScreenCapture {
    public static final String TAG = ScreenCapture.class.getName();
    private final Context mContext;

    private int mWindowWidth;
    private int mWindowHeight;
    private int mScreenDensity;

    private VirtualDisplay mVirtualDisplay;
    private WindowManager mWindowManager;
    private ImageReader mImageReader;
    private Surface mSurface;
    private MediaCodec mMediaCodec;

    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mMediaProjection;

    private Bitmap mBitmap;
    private Timer mTimer;

    private boolean isRecordOn;

    private OnCaptureListener mCaptureListener = null;

    public interface OnCaptureListener {
        void onScreenCaptureSuccess(Bitmap bitmap);

        void onScreenCaptureFailed(String errorMsg);
    }

    public void setCaptureListener(OnCaptureListener captureListener) {
        this.mCaptureListener = captureListener;
    }

    public ScreenCapture(Context context) {
        this.mContext = context;
        createEnvironment();
    }

    private void createEnvironment() {
        EventBus.getDefault().register(this);

        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mWindowWidth = mWindowManager.getDefaultDisplay().getWidth();
        mWindowHeight = mWindowManager.getDefaultDisplay().getHeight();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);
        mScreenDensity = displayMetrics.densityDpi;
        mImageReader = ImageReader.newInstance(mWindowWidth, mWindowHeight, 0x1, 2); // 为了格式相同，此错误可以忽略

        mMediaProjectionManager = (MediaProjectionManager) mContext.
                getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        mTimer = new Timer();
    }

    public void start() {
        setup();
    }

    private void invokeCapture(){
        configureMedia();
        if (isRecordOn) {
            if (mCaptureListener != null) {
                mCaptureListener.onScreenCaptureFailed("Recording is in progress.");
            }
        }else{
            isRecordOn = true;
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    startCapture();
                }
            }, 200, 1000);
        }
    }

    public void end(){
        if(mTimer != null){
            mTimer.cancel();
        }
        stopScreenCapture();
        isRecordOn = false;
    }

    private void startCapture() {
        Log.i(TAG, "start capture");

        Image image = mImageReader.acquireLatestImage();
        imgToBitmap(image);
        if (mBitmap != null) {
            Log.d(TAG, "bitmap create success");
            if (mCaptureListener != null) {
                mCaptureListener.onScreenCaptureSuccess(mBitmap);
            }
        } else {
            Log.d(TAG, "bitmap is null");
            if (mCaptureListener != null) {
                mCaptureListener.onScreenCaptureFailed("Get bitmap failed.");
            }
        }
        image.close();
    }

    private void stopScreenCapture() {
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }
    }

    private boolean setup() {
        Log.d(TAG, "startScreenCapture");
        if (mMediaProjection != null) {
            setUpVirtualDisplay();
            invokeCapture();
            return true;
        } else {
            Log.d(TAG, "Requesting confirmation");
            // This initiates a prompt dialog for the user to confirm screen projection.
            Intent i = new Intent(mContext,ScreenCaptureIntentActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(i);
            return false;
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void handleEvent(BaseEvent event){
        if(event.getFlag().equals(TAG)){
            if(event instanceof ScreenCaptureIntentEvent){
                setUpMediaProjection(((ScreenCaptureIntentEvent) event).getResultCode(), ((ScreenCaptureIntentEvent) event).getData());
                setUpVirtualDisplay();
                invokeCapture();
            }
        }
    }

    private void setUpVirtualDisplay() {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("ScreenCapture",
                mWindowWidth, mWindowHeight, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);
    }

    private void setUpMediaProjection(int resultCode, Intent data) {
        mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
    }

    private void imgToBitmap(Image image){
        if (image == null) {
            Log.e(TAG, "image is null.");
            return;
        }
        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        mBitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        mBitmap.copyPixelsFromBuffer(buffer);
        mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, width, height);
    }

    private void configureMedia() {
        MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", mWindowWidth, mWindowHeight);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 6000000);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 2);
        try {
            mMediaCodec = MediaCodec.createEncoderByType("video/avc");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mSurface = mMediaCodec.createInputSurface();
        mMediaCodec.start();
    }

    private void release() {
        EventBus.getDefault().unregister(this);
        mTimer.cancel();
        mTimer = null;

        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
        if (mImageReader != null) {
            mImageReader.close();
            mImageReader = null;
        }
    }

    public void cleanup() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
        release();
        if (mMediaProjection != null) {
            mMediaProjection.stop();
        }
    }
}
