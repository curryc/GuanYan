package com.scu.guanyan.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.scu.guanyan.R;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2023/2/16 13:50
 * @description:带有选择框的dialog，适合在设置的时候调出用于选择
 **/
public class DialogSelect extends Dialog {
    private Button yes;//确定按钮
    private Button no;//取消按钮
    private TextView titleTv;//消息标题文本
    private RadioGroup mSelectGroup;
    private String titleStr;//从外界设置的title文本
    //确定文本和取消文本的显示内容
    private String yesStr, noStr;
    private List<String> mItems;
    private String mChecked;

    private DialogSelect.onNoOnclickListener noOnclickListener;//取消按钮被点击了的监听器
    private DialogSelect.onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器

    /**
     * 设置取消按钮的显示内容和监听
     *
     * @param str
     * @param onNoOnclickListener
     */
    public void setNoOnclickListener(String str, DialogSelect.onNoOnclickListener onNoOnclickListener) {
        if (str != null) {
            noStr = str;
        }
        this.noOnclickListener = onNoOnclickListener;
    }

    /**
     * 设置确定按钮的显示内容和监听
     *
     * @param str
     * @param onYesOnclickListener
     */
    public void setYesOnclickListener(String str, DialogSelect.onYesOnclickListener onYesOnclickListener) {
        if (str != null) {
            yesStr = str;
        }
        this.yesOnclickListener = onYesOnclickListener;
    }

    public DialogSelect(Context context) {
        super(context, R.style.CenterDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(true);
        //初始化界面控件
        initView();
        //初始化界面数据
        initData();
        //初始化界面控件的事件
        initEvent();
    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yesOnclickListener != null) {
                    int id = mSelectGroup.getCheckedRadioButtonId();
                    yesOnclickListener.onYesClick(((RadioButton) mSelectGroup.findViewById(id)).getText().toString());
                }
            }
        });
        //设置取消按钮被点击后，向外界提供监听
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noOnclickListener != null) {
                    noOnclickListener.onNoClick();
                }
            }
        });
    }

    /**
     * 初始化界面控件的显示数据
     */
    private void initData() {
        //如果用户自定了title和message
        if (titleStr != null) {
            titleTv.setText(titleStr);
        }
        //如果设置按钮的文字
        if (yesStr != null) {
            yes.setText(yesStr);
        }
        if (noStr != null) {
            no.setVisibility(View.VISIBLE);
            no.setText(noStr);
        } else {
            no.setVisibility(View.GONE);
        }
        int i = 0; // 设置id防止radio button不互斥
        for (String item : mItems) {
            RadioButton button = new RadioButton(getContext());
            button.setText(item);
            button.setId(i++);
            if (item.equals(mChecked)) button.setChecked(true);
            mSelectGroup.addView(button);
        }
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        yes = (Button) findViewById(R.id.yes);
        no = (Button) findViewById(R.id.no);
        titleTv = (TextView) findViewById(R.id.title);
        mSelectGroup = findViewById(R.id.radio_group);
    }

    /**
     * 从外界Activity为Dialog设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        titleStr = title;
    }

    /**
     * 从外界Activity为Dialog设置dialog的radio group的各项
     *
     * @param
     */

    public void setItems(List<String> items, String checked) {
        this.mItems = items;
        this.mChecked = checked;
        Collections.sort(this.mItems);
    }

    public void setItems(Iterator items, String checked) {
        mChecked = checked;
        while (items.hasNext()) {
            String s = (String) items.next();
            mItems.add(s);
        }
        Collections.sort(this.mItems);
    }

    /**
     * 设置确定按钮和取消被点击的接口
     */
    public interface onYesOnclickListener {
        public void onYesClick(String checkedText);
    }

    public interface onNoOnclickListener {
        public void onNoClick();
    }
}
