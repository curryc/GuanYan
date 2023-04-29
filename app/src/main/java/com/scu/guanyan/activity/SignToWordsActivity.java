package com.scu.guanyan.activity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.scu.guanyan.R;
import com.scu.guanyan.base.BaseActivity;
import com.scu.guanyan.event.BaseEvent;
import com.scu.guanyan.event.WebEvent;
import com.scu.guanyan.utils.base.Web;
import com.scu.guanyan.utils.ncnn.BlazePoseNcnn;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import java.util.Arrays;

public class SignToWordsActivity extends BaseActivity {
    private static String TAG = "SignToWordsActivity";
    private int CAMERA_STATE_CODE = 0x02;

    private BlazePoseNcnn blazeposencnn = new BlazePoseNcnn();
    private int facing = 1;

    private Spinner spinnerModel;
    private Spinner spinnerCPUGPU;
    private int current_model = 0;
    private int current_cpugpu = 0;

    private SurfaceView cameraView;
    private String mResultString;

    private float[][] mFrames = new float[50][34];
    private long mSendTime;
    private int mFrameIndex = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_trans_sign;
    }

    @Override
    protected void initData() {
        super.initData();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initView() {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        cameraView = (SurfaceView) findViewById(R.id.cameraview);
        cameraView.getHolder().setFormat(PixelFormat.RGBA_8888);
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                blazeposencnn.setOutputWindow(holder.getSurface());
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });

        findViewById(R.id.buttonSwitchCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                int new_facing = 1 - facing;
                blazeposencnn.closeCamera();
                blazeposencnn.openCamera(new_facing);
                facing = new_facing;
            }
        });

        spinnerModel = (Spinner) findViewById(R.id.spinnerModel);
        spinnerModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                if (position != current_model) {
                    current_model = position;
                    reload();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        spinnerCPUGPU = (Spinner) findViewById(R.id.spinnerCPUGPU);
        spinnerCPUGPU.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                if (position != current_cpugpu) {
                    current_cpugpu = position;
                    reload();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        reload();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleData(BaseEvent event){
        if(event.getFlag().equals(TAG)){
            if(event instanceof WebEvent){
                mResultString = ((WebEvent) event).getData();
            }
        }
    }


    private void reload() {
        boolean ret_init = blazeposencnn.loadModel(getAssets(), current_model, current_cpugpu);
        if (!ret_init) {
            Log.e(TAG, "blazeposencnn loadModel failed");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_STATE_CODE);
        }

        blazeposencnn.openCamera(facing);
        blazeposencnn.addKptsCallBack(new BlazePoseNcnn.OnKeypointsCallback() {
            @Override
            public void onKeptsReceived(float[][][] points) {
                long time = System.currentTimeMillis();
                if (points != null && points.length > 0 && points[0].length>23 && points[0][0].length>1 && time - mSendTime > 100) {
                    mSendTime = time;
                    mFrames[mFrameIndex] = generate(points[0]);
                    mFrameIndex++;
                    if(mFrameIndex == 50){
                        try {
                            Web.postPredictSign(TAG,mFrames);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }finally {
                            mFrameIndex = 0;
                        }
                    }
                }
            }
        });
    }

    private void printArray(float[][] points) {
        System.out.println("(" + points.length + "," + points[0].length + ")");
        System.out.print("[ ");
        for (float[] subSubArray : points) {
            System.out.print("[ " + subSubArray[0] + "," + subSubArray[1] + "], ");
        }
    }

    private float[] generate(float[][] source) {
        float[] target = new float[34];
        // 头部和身体
        target[0] = (source[23][0] + source[24][0]) / 2;
        target[1] = (source[23][1] + source[24][1]) / 2;

        target[2] = (source[23][0] + source[24][0] + source[11][0] + source[12][0]) / 4;
        target[3] = (source[23][1] + source[24][1] + source[11][1] + source[12][1]) / 4;

        target[4] = (source[3][0] + source[6][0]) / 2;
        target[5] = (source[3][1] + source[6][1]) / 2;

        target[6] = (source[9][0] + source[10][0]) / 2;
        target[7] = (source[9][1] + source[10][1]) / 2;

        // 双手
        target[8] = source[11][0];
        target[9] = source[11][1];
        target[10] = source[13][0];
        target[11] = source[13][1];
        target[12] = source[15][0];
        target[13] = source[15][1];
        target[14] = source[17][0];
        target[15] = source[17][1];

        target[16] = source[12][0];
        target[17] = source[12][1];
        target[18] = source[14][0];
        target[19] = source[14][1];
        target[20] = source[16][0];
        target[21] = source[16][1];
        target[22] = source[18][0];
        target[23] = source[18][1];

        // 手指和脖子
        target[24] = (source[9][0] + source[10][0] + source[11][0] + source[12][0]) / 4;
        target[25] = (source[9][1] + source[10][1] + source[11][1] + source[12][1]) / 4;


        target[26] = source[19][0];
        target[27] = source[19][1];
        target[28] = source[21][0];
        target[29] = source[21][1];
        target[30] = source[20][0];
        target[31] = source[20][1];
        target[32] = source[22][0];
        target[33] = source[22][1];

//        Log.i(TAG, Arrays.toString(target));
        return target;
    }

    @Override
    public void onPause() {
        super.onPause();
        blazeposencnn.closeCamera();
    }
}