package com.curry.toolt.activity;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import androidx.appcompat.widget.Toolbar;
import com.curry.function.App;
import com.curry.toolt.R;
import com.curry.toolt.base.TopToolbarActivity;
import com.curry.util.view.ColorPickerView;
import com.curry.util.cache.SharedPreferencesHelper;

public class ThemeActivity extends TopToolbarActivity implements View.OnClickListener, TextWatcher, View.OnFocusChangeListener {
    private final String TAG = "themeActivity";

    //    private ImageView mColorCircle;
    private ColorPickerView mColorPicker;
    private EditText mRed, mGreen, mBlue;
    private Button mSave;

    private int mInitColor;
    private Mode mChangeMode;

    enum Mode {
        Touch,
        Edit
    }


    @Override
    protected void initContainer(FrameLayout container) {
        System.out.println(App.getThemeColor("colorPrimaryDark"));

        View root = getLayoutInflater().inflate(R.layout.activity_theme, container, false);
        container.addView(root);

        mInitColor = App.getThemeColor();

//        mColorCircle = findViewById(R.id.color_circle);
        mRed = findViewById(R.id.red);
        mGreen = findViewById(R.id.green);
        mBlue = findViewById(R.id.blue);
        mSave = findViewById(R.id.save);

        mSave.setBackgroundColor(App.getThemeColor());
        mRed.setText(String.valueOf(Color.red(mInitColor)));
        mGreen.setText(String.valueOf(Color.green(mInitColor)));
        mBlue.setText(String.valueOf(Color.blue(mInitColor)));

        mSave.setOnClickListener(this::onClick);
        mRed.addTextChangedListener(this);
        mGreen.addTextChangedListener(this);
        mBlue.addTextChangedListener(this);

        mRed.setOnFocusChangeListener(this);
        mGreen.setOnFocusChangeListener(this);
        mBlue.setOnFocusChangeListener(this);


        int size = getResources().getDimensionPixelSize(R.dimen.theme_color_picker_size);
        mColorPicker = new ColorPickerView(this, size, size, mInitColor);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size, Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        mColorPicker.setLayoutParams(params);
        container.addView(mColorPicker);
        mColorPicker.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
            @Override
            public void colorChanged(int color) {
                mSave.setBackgroundColor(color);
                mRed.setText(String.valueOf(Color.red(color)));
                mGreen.setText(String.valueOf(Color.green(color)));
                mBlue.setText(String.valueOf(Color.blue(color)));
            }
        });
        mColorPicker.setOnFocusChangeListener(this);
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        toolbar.setTitle(getString(R.string.drawer_theme));
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mSave)) {
            SharedPreferencesHelper.put(ThemeActivity.this,
                    "colorPrimary",
                    Color.rgb(Integer.parseInt(mRed.getText().toString()),
                            Integer.parseInt(mGreen.getText().toString()),
                            Integer.parseInt(mBlue.getText().toString())));
            toastShort(getString(R.string.save_theme_hint));
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.equals(mColorPicker)) {
            mChangeMode = Mode.Touch;
        } else {
            if (hasFocus) mChangeMode = Mode.Edit;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mChangeMode == Mode.Edit && !mRed.getText().toString().equals("") && !mGreen.getText().toString().equals("") && !mBlue.getText().toString().equals("")) {
            int color = Color.rgb(Integer.parseInt(mRed.getText().toString()),
                    Integer.parseInt(mGreen.getText().toString()),
                    Integer.parseInt(mBlue.getText().toString()));
            mSave.setBackgroundColor(color);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}