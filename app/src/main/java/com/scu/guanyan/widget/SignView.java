package com.scu.guanyan.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.scu.guanyan.R;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2022/11/21 21:23
 * @description:显示一个手语动作的view
 **/
public class SignView extends FrameLayout {
    private final String faceFormat = "FaceType: %d %s";
    private final String[] faceTypes = new String[]{"没表情", "开心", "愤怒", "伤心", "疑惑", "害怕", "讨厌", "惊讶", "痛苦", "失望"};

    private View mRootView;
    private ImageView mImageView;
    private TextView mFace, mMouth;

    public SignView(@NonNull Context context) {
        super(context);
        init();
    }

    public SignView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SignView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.widget_sign, this, true);
        mImageView = mRootView.findViewById(R.id.sign_image);
//        mFace = mRootView.findViewById(R.id.face_type);
//        mMouth = mRootView.findViewById(R.id.mouth_type);

        mImageView.setImageResource(R.color.light_gray);
        mFace.setText("face type");
//        mMouth.setText("mouth type");
    }

    public void setSign(Bitmap bitmap){
        this.mImageView.setImageBitmap(bitmap);
    }

    public void setFace(int faceType){
        this.mFace.setText(String.format(faceFormat, faceType, faceTypes[faceType]));
    }

    public void setMouth(String mouth) {
        mMouth.setText(mouth);
    }
}
