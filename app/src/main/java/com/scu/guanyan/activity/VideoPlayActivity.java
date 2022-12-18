package com.scu.guanyan.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.scu.guanyan.R;
import com.scu.guanyan.base.BaseActivity;
import com.scu.guanyan.base.FragmentAdapter;
import com.scu.guanyan.bean.TeachVideo;
import com.scu.guanyan.ui.setting.SettingFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2022/12/13 15:59
 * @description:
 **/
public class VideoPlayActivity extends BaseActivity {
    public static String VIDEO_DATA = "video_data";
    private ViewPager2 mViewPager;
    private TabLayout mTab;
    private VideoView mVideoView;

private MediaController mVideoController;
    private FragmentAdapter mAdapter;

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
    protected void initData() {
        super.initData();

        mAdapter = new FragmentAdapter(getSupportFragmentManager(), getLifecycle(), getTwoPages());
        mVideoController = new MediaController(this);
    }

    @Override
    protected void initView() {
        mVideoView = findViewById(R.id.video);
        mViewPager = findViewById(R.id.view_pager);
        mTab = findViewById(R.id.tab);

        mViewPager.setAdapter(mAdapter);
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            mTab.addTab(mTab.newTab());
        }
        TabLayoutMediator mediator = new TabLayoutMediator(mTab, mViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(mAdapter.getData().get(position).getTitle());
            }
        });
        mediator.attach();

        mVideoView.setVideoPath("https://cf4741e7bbba31365fe0dc37a3abd4f6.free-lbv26.idouyinvod.com:443/v26-web.douyinvod.com/1d53e7cc8bf784cc009081e12fc19156/6398a1db/video/tos/cn/tos-cn-ve-15/oAO9UlDEsHv4VuCRAKxpfrgQnbA2eAgmoB7j3o/?a=6383&ch=5&cr=3&dr=0&lr=all&cd=0%7C0%7C0%7C3&cv=1&br=2153&bt=2153&cs=0&ds=3&ft=bvTKJbQQqUuxfdo0Vo0OqY8hFgpivGnw~jKJxF4D1N0P3-A&mime_type=video_mp4&qs=0&rc=PDkzaGk4NGZmNGhnOmc6ZEBpam1zc2Q6ZnRxaDMzNGkzM0BhXi81NTIyXzMxLy8xNmMxYSMwNV5fcjRfbF9gLS1kLS9zcw%3D%3D&l=202212132301150101511801070C3502AB&btag=10000&hon_cdn_source=MCAwDQYJKoZIhvcNAQEB,1192422066,1670943763,dCLKIiWxkar5gAneg5CF7ryxpdKx0c612TJTr7M5D5o&hon_cdn_info=39.135.207.12_39.137.105.86_aa65df1923f4d614d49ee09397dea13c_a15864f24c336b041538fb4abc093419");
        mVideoView.start();
    }

    private List<FragmentAdapter.PagerInfo> getTwoPages(){
        List<FragmentAdapter.PagerInfo> l = new ArrayList<>();
        l.add(new FragmentAdapter.PagerInfo("介绍", SettingFragment.class, null));
        l.add(new FragmentAdapter.PagerInfo("详情", SettingFragment.class, null));
        return l;
    }
}