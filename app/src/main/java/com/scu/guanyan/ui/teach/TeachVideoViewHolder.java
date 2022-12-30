package com.scu.guanyan.ui.teach;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;

import com.scu.guanyan.R;
import com.scu.guanyan.activity.VideoPlayActivity;
import com.scu.guanyan.base.RecyclerViewHolder;
import com.scu.guanyan.bean.TeachVideo;

import org.jetbrains.annotations.NotNull;

/**
 * @program: Guanyan
 * @author: 陈博文
 * @create: 2022-11-12 16:37
 * @description:
 **/
public class TeachVideoViewHolder extends RecyclerViewHolder {
    public TeachVideoViewHolder(Context context, @NonNull @NotNull View itemView) {
        super(context, itemView);
    }

    @Override
    public void bind(Object data) {
        TeachVideo video = (TeachVideo) data;
        // 待写,绑定view
        loadImage(mContext, video.getCover(), R.id.cover);
        setText(R.id.title, ((TeachVideo) data).getTitle());
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(VideoPlayActivity.createActivity(mContext,video));
            }
        }, itemView);
    }
}
