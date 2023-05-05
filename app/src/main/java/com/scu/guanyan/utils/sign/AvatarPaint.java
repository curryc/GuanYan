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
import android.text.TextUtils;

import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.scu.guanyan.utils.sign.Avatar.boneMap;

import com.huawei.hms.signpal.GeneratorConstants;
import com.scu.guanyan.utils.base.SharedPreferencesHelper;


public class AvatarPaint {
    private final static String TAG = "AvatarPaint";
    public final static String ANIM_SPEED = "anim_speed_key";

    private Queue<FrameData> frameDataQueue = new ConcurrentLinkedQueue<>();

    private Context mContext;
    private int mSpeed;
    private int mMode;
    private boolean playing;

    private Timer mFrameCreator;
    private TimerTask mFrameCreatorThread;
    private SignPlayer mUnityPlayer;

    public AvatarPaint(SignPlayer player, int mode) {
        this(player, mode, false);
    }

    public AvatarPaint(SignPlayer player, int mode, boolean startup) {
        this.mContext = player.getContext();
        this.mUnityPlayer = player;
        this.mMode = mode;
        this.mFrameCreator = new Timer();
        this.mFrameCreatorThread = new TimerTask() {
            @Override
            public void run() {
                if (!frameDataQueue.isEmpty()) {
                    drawFrame(frameDataQueue.poll());
                }
            }
        };
        this.playing = startup;
        if (startup) startAndPlay();
        init();
    }

    public synchronized void addFrameDataList(List<FrameData> frameDataList) {
        this.frameDataQueue.addAll(frameDataList);
    }

    public synchronized void checkAndClear() {
        if (mMode == GeneratorConstants.FLUSH_MODE) {
            this.frameDataQueue.clear();
        }
    }

    private void init() {
        mSpeed = 1000 / (int) SharedPreferencesHelper.get(mContext, ANIM_SPEED, 30);
    }


    public synchronized void drawFrame(FrameData data) {
        for (String name : Avatar.boneNames) {
            Bone endBone = boneMap.get(name);
            if (TextUtils.isEmpty(endBone.parentName)) {
                continue;
            }
            Bone startBone = boneMap.get(endBone.parentName);

            String w = String.valueOf(endBone.worldRotate.w);
            String x = String.valueOf(endBone.worldRotate.x);
            String y = String.valueOf(endBone.worldRotate.y);
            String z = String.valueOf(endBone.worldRotate.z);
            mUnityPlayer.sendMessage("kong", "rotates", endBone.parentName + "+" + w + "+" + x + "+" + y + "+" + z);

            // draw bone
            endBone.setRotate(data.getDataByBoneName(startBone.name), startBone);
            boneMap.put(name, endBone); // update endBone pose
        }
    }

    public void startAndPlay() {
        mFrameCreator.schedule(mFrameCreatorThread, 100, mSpeed);
//        mAnimator.post(mAnimatorThread);
        setPlaying(true);
    }


    public void destroy() {
        mFrameCreator.cancel();
        mFrameCreator = null;
//        frameDataQueue.clear();
        frameDataQueue = null;
        mContext = null;
        setPlaying(false);
//        mAnimator.removeCallbacks(mAnimatorThread);
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }
}
