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
import android.graphics.*;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import static com.scu.guanyan.utils.sign.Avatar.boneMap;

import com.scu.guanyan.utils.base.SharedPreferencesHelper;


public class AvatarPaint {
    private final static String TAG = "AvatarPaint";
    private final static String ANIM_SPEED = "anim_speed";

    private Queue<Pair<HashMap, Integer>> frameQueue = new ConcurrentLinkedQueue<>();
    private Queue<FrameData> frameDataQueue = new ConcurrentLinkedQueue<>();

    private Context mContext;
    private final int mSpeed= 1000/30;
    private int mMode;
    private boolean playing;

    private Timer mFrameCreator;
    private Runnable mFrameCreatorThread;
    private SignPlayer mUnityPlayer;

    public AvatarPaint(SignPlayer player, int mode){
        this(player, mode, false);
    }

    public AvatarPaint(SignPlayer player,int mode,  boolean startup) {
        this.mContext = player.getContext();
        this.mUnityPlayer = player;
        this.mMode = mode;
        this.mFrameCreator = new Timer();
        this.playing = startup;
        if (startup) startAndPlay();
        init();
    }

    public synchronized void addFrameDataList(List<FrameData> frameDataList){
            this.frameDataQueue.addAll(frameDataList);
    }

    public synchronized void clearFrameData(){
        this.frameDataQueue.clear();
        this.frameQueue.clear();
    }

    private void init(){
//        mAnimatorThread = new Runnable() {
//            @Override
//            public void run() {
//                if (!frameQueue.isEmpty()) {
//                    Pair<HashMap, Integer> frameDataPair = frameQueue.poll();
//                    for (String name : Avatar.boneNames){
//                        Bone endBone = (Bone) frameDataPair.first.get(name);
//                        String w = String.valueOf(endBone.worldRotate.w);
//                        String x = String.valueOf(endBone.worldRotate.x);
//                        String y = String.valueOf(endBone.worldRotate.y);
//                        String z = String.valueOf(endBone.worldRotate.z);
//                        mUnityPlayer.sendMessage("kong","rotates",endBone.parentName+"+"+w+"+"+x+"+"+y+"+"+z);
//                    }
//
//                }
//                mAnimator.post(this);
//            }
//        };
//        mFrameCreatorThread = new Runnable() {
//            @Override
//            public void run() {
//                if (!frameDataQueue.isEmpty()) {
//                    drawFrame(frameDataQueue.poll());
//                }
//                mFrameCreator.postDelayed(this, 100);
//            }
//        };
//        mSpeed = SharedPreferencesHelper.getObject(mContext, ANIM_SPEED);
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
            mUnityPlayer.sendMessage("kong","rotates",endBone.parentName+"+"+w+"+"+x+"+"+y+"+"+z);

            // draw bone
            endBone.setRotate(data.getDataByBoneName(startBone.name), startBone);
            boneMap.put(name, endBone); // update endBone pose
        }
    }

    public void startAndPlay() {
        mFrameCreator.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!frameDataQueue.isEmpty()) {
                    Log.e(TAG, "timer" + new Date().getTime());
                    drawFrame(frameDataQueue.poll());
                }
            }
        }, 1000,100);
//        mAnimator.post(mAnimatorThread);
        setPlaying(true);
    }


    public void destroy() {
        mFrameCreator.cancel();
        mFrameCreator = null;
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
