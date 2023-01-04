package com.scu.guanyan.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.scu.guanyan.R;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2022/11/23 14:17
 * @description:一个菜单项
 **/
public class MenuListItem extends FrameLayout {
    private Class<? extends AppCompatActivity> mClx;
    private View mRootView;
    private TextView mTitle;
    private String mTitleString;
    private ImageView mIcon;
    private int mIconResource;

    public MenuListItem(Context context, String titleString,int iconResource, Class<? extends AppCompatActivity> clx) {
        super(context);
        this.mTitleString = titleString;
        this.mIconResource = iconResource;
        this.mClx = clx;
        init();
    }

    public MenuListItem(Context context, int titleResource,int iconResource, Class<? extends AppCompatActivity> clx) {
        super(context);
        this.mTitleString = getResources().getString(titleResource);
        this.mIconResource = iconResource;
        this.mClx = clx;
        init();
    }

    private void init() {
        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.widget_menu_list_item, this, true);
        mTitle = mRootView.findViewById(R.id.title);
        mTitle.setText(mTitleString);
        mIcon = mRootView.findViewById(R.id.icon);
        mIcon.setImageResource(mIconResource);
        mRootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().startActivity(new Intent(getContext(), mClx));
            }
        });
    }
}
