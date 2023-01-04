package com.scu.guanyan.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.scu.guanyan.R;
import com.scu.guanyan.base.BaseActivity;
import com.scu.guanyan.bean.TeachVideo;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2022/12/13 15:59
 * @description:
 **/
public class VideoPlayActivity extends BaseActivity {
    public static String VIDEO_DATA = "video_data";

    public static Intent createActivity(Context context, TeachVideo video){
        Bundle bundle = new Bundle();
        bundle.putSerializable(VIDEO_DATA, video);
        Intent intent = new Intent(context, VideoPlayActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_play_video;
    }

    @Override
    protected void initView() {

    }
}
