package com.scu.guanyan.utils.sign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnityMap {
    static Map<String,String> table=new HashMap<>();
    UnityMap(){
        table.put("Hips","Spine1");
        table.put("Spine","Spine2");
        table.put("Chest","Spine3");
        table.put("Chest.001","Spine4");
        table.put("Left shoulder","LeftShoulder");
        table.put("Left arm","LeftArm");
        table.put("Left elbow","LeftForeArm");
        table.put("Left wrist","LeftHand");
        table.put("Thumb0_L","LeftHandThumb1");
        table.put("Thumb1_L","LeftHandThumb2");
        table.put("Thumb2_L","LeftHandThumb3");
        table.put("Thumb2_L_end","LeftHandThumb4");
        table.put("IndexFinger1_L","LeftHandIndex1");
        table.put("IndexFinger2_L","LeftHandIndex2");
        table.put("IndexFinger3_L","LeftHandIndex3");
        table.put("IndexFinger3_L_end","LeftHandIndex4");
        table.put("MiddleFinger1_L","LeftHandMiddle1");
        table.put("MiddleFinger2_L","LeftHandMiddle2");
        table.put("MiddleFinger3_L","LeftHandMiddle3");
        table.put("MiddleFinger3_L_end","LeftHandMiddle4");
        table.put("RingFinger1_L","LeftHandRing1");
        table.put("RingFinger2_L","LeftHandRing2");
        table.put("RingFinger3_L","LeftHandRing3");
        table.put("RingFinger3_L_end","LeftHandRing4");
        table.put("LittleFinger1_L","LeftHandPinky1");
        table.put("LittleFinger2_L","LeftHandPinky2");
        table.put("LittleFinger3_L","LeftHandPinky3");
        table.put("LittleFinger3_L_end","LeftHandPinky4");
        table.put("Right shoulder","RightShoulder");
        table.put("Right arm","RightArm");
        table.put("Right elbow","RightForeArm");
        table.put("Right wrist","RightHand");
        table.put("Thumb0_R","RightHandThumb1");
        table.put("Thumb1_R","RightHandThumb2");
        table.put("Thumb2_R","RightHandThumb3");
        table.put("Thumb2_R_end","RightHandThumb4");
        table.put("IndexFinger1_R","RightHandIndex1");
        table.put("IndexFinger2_R","RightHandIndex2");
        table.put("IndexFinger3_R","RightHandIndex3");
        table.put("IndexFinger3_R_end","RightHandIndex4");
        table.put("MiddleFinger1_R","RightHandMiddle1");
        table.put("MiddleFinger2_R","RightHandMiddle2");
        table.put("MiddleFinger3_R","RightHandMiddle3");
        table.put("MiddleFinger3_R_end","RightHandMiddle4");
        table.put("RingFinger1_R","RightHandRing1");
        table.put("RingFinger2_R","RightHandRing2");
        table.put("RingFinger3_R","RightHandRing3");
        table.put("RingFinger3_R_end","RightHandRing4");
        table.put("LittleFinger1_R","RightHandPinky1");
        table.put("LittleFinger2_R","RightHandPinky2");
        table.put("LittleFinger3_R","RightHandPinky3");
        table.put("LittleFinger3_R_end","RightHandPinky4");
        table.put("Neck","Neck");
        table.put("Head","Head");

    }

    public static String getKey(String value){

        for(String key: table.keySet()){
            if(table.get(key).equals(value))
                return key;
        }
        return null;
    }

}
