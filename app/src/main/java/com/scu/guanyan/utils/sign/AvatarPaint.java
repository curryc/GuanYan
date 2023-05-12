/**
 * Copyright 2021 . Huawei Technologies Co., Ltd. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scu.guanyan.utils.sign;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.scu.guanyan.utils.sign.Avatar.boneMap;

import com.huawei.hms.signpal.GeneratorConstants;
import com.scu.guanyan.utils.base.SharedPreferencesHelper;


public class AvatarPaint {
    private final static String TAG = "AvatarPaint";
    public final static String ANIM_SPEED = "anim_speed_key";

    private Queue<FrameData> mFrameDataQueue = new ConcurrentLinkedQueue<>();
    private Queue<Integer> mFrameDrawQueue = new ConcurrentLinkedQueue<>();

    private Context mContext;
    private int mSpeed;
    private int mMode;
    private boolean playing;

    private Handler mFrameCreator, mFrameDrawer;
    private HandlerThread mFrameCreatorThread;
    private Runnable mFrameCreatorTask, mFrameDrawerTask;
    private SignPlayer mUnityPlayer;

    public AvatarPaint(SignPlayer player, int mode) {
        this(player, mode, false);
    }

    public AvatarPaint(SignPlayer player, int mode, boolean startup) {
        this.mContext = player.getContext();
        this.mUnityPlayer = player;
        this.mMode = mode;
        this.mFrameCreatorThread = new HandlerThread("frame creator");
        mFrameCreatorThread.start();
        this.mFrameCreator = new Handler(mFrameCreatorThread.getLooper());
        this.mFrameDrawer = new Handler(mFrameCreatorThread.getLooper());
        this.playing = startup;
        if (startup) startAndPlay();
        init();
    }

    public synchronized void addFrameDataList(List<FrameData> frameDataList) {
        this.mFrameDataQueue.addAll(frameDataList);
    }

    public synchronized void checkAndClear() {
        if (mMode == GeneratorConstants.FLUSH_MODE) {
            this.mFrameDataQueue.clear();
        }
    }

    private void init() {
        mSpeed = 1000 / (int) SharedPreferencesHelper.get(mContext, ANIM_SPEED, 30);
        mFrameCreatorTask = new Runnable() {
            @Override
            public void run() {
                if (!mFrameDataQueue.isEmpty()) {
                    createFrame(mFrameDataQueue.poll());
                }
                if (mFrameCreator != null)
                    mFrameCreator.postDelayed(this, mSpeed);
            }
        };
        mFrameDrawerTask = new Runnable() {
            @Override
            public void run() {
                if (!mFrameDrawQueue.isEmpty()) {
                    drawFrame(mFrameDrawQueue.poll());
                }
                if (mFrameDrawer != null)
                    mFrameDrawer.post(this);
            }
        };
    }

    private synchronized void drawFrame(int faceType) {
        for (String name : Avatar.boneNames) {
            Bone endBone = boneMap.get(name);
            String w = String.valueOf(endBone.worldRotate.w);
            String x = String.valueOf(endBone.worldRotate.x);
            String y = String.valueOf(endBone.worldRotate.y);
            String z = String.valueOf(endBone.worldRotate.z);

            if (!(endBone.worldRotate.w == 1.0f &&
                    endBone.worldRotate.x == 0.0f &&
                    endBone.worldRotate.y == 0.0f &&
                    endBone.worldRotate.z == 0.0f)) {

                mUnityPlayer.sendMessage("kong", "rotates", endBone.parentName + "+" + w + "+" + x + "+" + y + "+" + z);
            }
            //Log.v(TAG,endBone.parentName+"+"+endBone.name+"+"+w+"+"+x+"+"+y+"+"+z);
        }
    }


    private synchronized void createFrame(FrameData data) {
        for (String name : Avatar.boneNames) {
            Bone endBone = boneMap.get(name);
            if (TextUtils.isEmpty(endBone.parentName)) {
                continue;
            }
            Bone startBone = boneMap.get(endBone.parentName);

            // draw bone
            endBone.setRotate(data.getDataByBoneName(startBone.name), startBone);
            boneMap.put(name, endBone); // update endBone pose
        }
        mFrameDrawQueue.offer(data.getFaceType());
    }

    public void startAndPlay() {
        if (!playing) {
            mFrameCreator.post(mFrameCreatorTask);
            mFrameDrawer.post(mFrameDrawerTask);
            setPlaying(true);
        }
    }


    public void destroy() {
        mFrameCreator.removeCallbacks(mFrameCreatorTask);
        mFrameDrawer.removeCallbacks(mFrameDrawerTask);
        mFrameCreator = null;
        mFrameDrawer = null;
        setPlaying(false);
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }
}