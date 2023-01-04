package com.scu.guanyan.activity;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.scu.guanyan.R;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

public class unity extends UnityPlayerActivity {

    TextView textView;
    SeekBar x_input;
    SeekBar y_input;
    SeekBar z_input;
    @Override protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        String cmdLine = updateUnityCommandLineArguments(getIntent().getStringExtra("unity"));
        getIntent().putExtra("unity", cmdLine);
        mUnityPlayer = new UnityPlayer(this, this);
        setContentView(R.layout.activity_unity);
        LinearLayout ll = (LinearLayout) findViewById(R.id.unity);
        View mView=mUnityPlayer.getView();
        ll.addView(mView);
        mUnityPlayer.requestFocus();
        textView=findViewById(R.id.input);
        x_input=findViewById(R.id.x_input);
        y_input=findViewById(R.id.y_input);
        z_input=findViewById(R.id.z_input);
    }

    public void onclick(View view) {
        String message=getMessage();
        UnityPlayer.UnitySendMessage(
                 "kong",
                "control",
                    message);


    }
    public String getMessage(){
        String str="";
        str= textView.getText().toString();
        float x=x_input.getProgress();
        float y=y_input.getProgress();
        float z=z_input.getProgress();
        str=str+"+"+ x +"+"+ y +"+"+z;
        //Log.e("msg:",str);
        return  str;
    }
    public void show(String str){
        Log.e("unity back:",str);
    }
    @Override
    public void onBackPressed() {
        mUnityPlayer.quit();
        Log.i("unity","quit");
        super.onBackPressed();
    }
}