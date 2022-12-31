package com.scu.guanyan.ui.setting;

import android.view.View;
import android.widget.LinearLayout;

import com.scu.guanyan.R;
import com.scu.guanyan.activity.AboutAppActivity;
import com.scu.guanyan.activity.FeedbackActivity;
import com.scu.guanyan.activity.ShareAppActivity;
import com.scu.guanyan.base.BaseFragment;
import com.scu.guanyan.base.ViewHolder;
import com.scu.guanyan.widget.MenuListItem;

/**
 * @program: Guanyan
 * @author: 陈博文
 * @create: 2022-11-07 17:05
 * @description:
 **/
public class SettingFragment extends BaseFragment {
    private final String TAG = "SettingFragment";

    private LinearLayout mMenuList;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void initView(ViewHolder viewHolder, View root) {
        mMenuList = viewHolder.getViewById(R.id.menu_list);
        handleMenuList();
    }

    private void handleMenuList(){
        mMenuList.addView(new MenuListItem(getContext(), R.string.about_app,R.drawable.setting_about_app, AboutAppActivity.class));
        mMenuList.addView(new MenuListItem(getContext(), R.string.share_app,R.drawable.setting_share_app, ShareAppActivity.class));
        mMenuList.addView(new MenuListItem(getContext(), R.string.feedback,R.drawable.setting_feedback, FeedbackActivity.class));
        mMenuList.addView(new MenuListItem(getContext(), R.string.contact_us,R.drawable.setting_contact, AboutAppActivity.class));
    }
}
