package com.scu.guanyan.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.scu.guanyan.R;
import com.scu.guanyan.utils.base.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2023/2/15 23:05
 * @description:一个设置项
 **/
public class SettingEntry extends FrameLayout {
    private String mTitle;
    private String mDescription;
    private String mKey;
    private int mValue;
    private Map<String, Integer> mItems;
    private boolean isBoolean;

    private View mRootView;
    private TextView mTitleView, mDesView;
    private FrameLayout mOperationContainer;

    private Context mContext;

    /**
     * 没有description，可以调用更简单的constructor
     *
     * @param context
     * @param title
     * @param key
     */
    public SettingEntry(Context context, String title, String key, Map items) {
        this(context, -1, title, null, key, items);
    }

    /**
     * 如果为boolean类型的设置项，1为真，0为假
     *
     * @param context
     * @param model
     * @param title
     * @param description
     * @param key
     */
    public SettingEntry(Context context, int model, String title, @Nullable String description, String key, Map items) {
        super(context);
        this.mContext = context;
        this.mTitle = title;
        this.mDescription = description;
        this.mKey = key;
        this.mItems = items;
        fetchData(key);
        initView();
    }

    private int fetchData(String key) {
        mValue = (int) SharedPreferencesHelper.get(mContext, key, -1);
        return mValue;
    }

    private void initView() {
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.widget_setting_list_item, this, true);
        mTitleView = mRootView.findViewById(R.id.title);
        mDesView = mRootView.findViewById(R.id.description);
        mOperationContainer = mRootView.findViewById(R.id.operate);

        mTitleView.setText(mTitle);
        mDesView.setText(mDescription == null ? "" : mDescription);
        mDesView.setVisibility((mDescription == null || mDescription.equals("")) ? View.GONE : View.VISIBLE);

        assert mItems != null;
        isBoolean = mItems.values().size() == 2 && mItems.containsValue(0) && mItems.containsValue(1);
        isBoolean = isBool();

        if (isBoolean) {
            // 真值设置
            Switch s = new Switch(getContext());
            s.setChecked(mValue == 1);
            s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    setValue(b);
                }
            });
            mOperationContainer.addView(s);
        } else {
            // 其他选项设置
            mRootView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogSelect dialog = new DialogSelect(mContext);
                    dialog.setTitle(mTitle);
                    dialog.setItems(getKeys(), getKeyByValue(mValue));
                    dialog.setNoOnclickListener(mContext.getString(R.string.cancel), new DialogSelect.onNoOnclickListener() {
                        @Override
                        public void onNoClick() {
                            dialog.dismiss();
                        }
                    });
                    dialog.setYesOnclickListener(mContext.getString(R.string.confirm), new DialogSelect.onYesOnclickListener() {
                        @Override
                        public void onYesClick(String checkedText) {
                            setValue(mItems.get(checkedText));
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });
        }
    }

    private List<String> getKeys(){
        List<String> l = new ArrayList<>();
        for (String key : mItems.keySet()) {
            l.add(key);
        }
        return l;
    }

    private String getKeyByValue(int value) {
        String key = "";
        for (Map.Entry<String, Integer> m : mItems.entrySet()) {
            if (m.getValue().equals(value)) {
                key = m.getKey();
            }
        }
        return key;
    }


    private boolean isBool() {
        return isBoolean;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getDescription() {
        return mDescription;
    }

    public Map<String, Integer> getItems() {
        return mItems;
    }

    public void setValue(int value) {
        mValue = value;
        SharedPreferencesHelper.put(mContext, mKey, value);
    }

    public void setValue(boolean b) {
        setValue(b ? 1 : 0);
    }

    public boolean isContentTrue() {
        return mValue == 1;
    }
}
