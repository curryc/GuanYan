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

import android.util.Log;

import com.huawei.hms.scene.math.Quaternion;

import java.util.HashMap;
import java.util.Map;

public class FrameData {
    private int faceType = 0;

    private float mouthType = 0;

    private int frameIdx;
    private Map<String, Quaternion> motionData = new HashMap<>();

    public int getFaceType() {
        return faceType;
    }

    public void setFaceType(int faceType) {
        this.faceType = faceType;
    }

    public float getMouthType() {
        return mouthType;
    }

    public void setMouthType(float mouthType) {
        this.mouthType = mouthType;
    }

    public int getFrameIdx() {
        return frameIdx;
    }

    public void setFrameIdx(int frameIdx) {
        this.frameIdx = frameIdx;
    }

    public void setMotionData(Map<String, float[]> motionData) {
        for (String k : motionData.keySet()) {
            this.motionData.put(k, new Quaternion(motionData.get(k)[0],


                    -motionData.get(k)[1],  // X-Axis Flip
                    motionData.get(k)[2],
                    motionData.get(k)[3]).normalize());

        }

    }

    public Quaternion getDataByBoneName(String boneName) {
        try {
            return motionData.get(boneName);
        } catch (NullPointerException e) {
            Log.d("FrameData", e.getMessage());
            return null;
        }
    }

}
