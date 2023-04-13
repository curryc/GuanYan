package com.scu.guanyan.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;

import com.scu.guanyan.R;
import com.scu.guanyan.event.ScreenCaptureIntentEvent;
import com.scu.guanyan.utils.screen.ScreenCapture;

import org.greenrobot.eventbus.EventBus;

public class ScreenCaptureIntentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transparent);

        MediaProjectionManager mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        ActivityResultLauncher launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                EventBus.getDefault().post(new ScreenCaptureIntentEvent(ScreenCapture.TAG, "", true, result.getResultCode(), result.getData()));
                finish();
            }
        });

        launcher.launch(mMediaProjectionManager.createScreenCaptureIntent());
    }
}