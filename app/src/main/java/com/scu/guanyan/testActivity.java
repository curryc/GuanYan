package com.scu.guanyan;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.scu.guanyan.utils.ORC.OCRUtils;
import com.scu.guanyan.utils.word.WordUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class testActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private WordUtil wordUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ivInputImage=findViewById(R.id.imageView2);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // 在主线程中更新 UI
            }
        });

    }
    OCRUtils ocrUtils;
    public void setModel(View view){
        ocrUtils=new OCRUtils(this);
        Toast.makeText(getApplicationContext(), "init", Toast.LENGTH_SHORT).show();
    }
    public void setImg(View view){
        openGallery();
    }
    public void runModel(View view){
        ocrUtils.setImgAndRunModel(cur_predict_image);
        System.out.println(ocrUtils.getModelRunResult());
        TextView textView=findViewById(R.id.result);
        textView.setText( ocrUtils.getRawResult().toString());
       // Toast.makeText(getApplicationContext(), ocrUtils.getModelRunResult(), Toast.LENGTH_SHORT).show();

    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, 0);

    }
    private Bitmap cur_predict_image = null;
    protected ImageView ivInputImage;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        else
            try {
            ContentResolver resolver = getContentResolver();
            Uri uri = data.getData();
            Bitmap image = MediaStore.Images.Media.getBitmap(resolver, uri);
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(uri, proj, null, null, null);
            cursor.moveToFirst();
            if (image != null) {
                cur_predict_image = image;
                ivInputImage.setImageBitmap(image);
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

    }

    public void testWorkUtil(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                wordUtil=new WordUtil();
                String str = "2022年度四川大学“青马工程”弘毅班（第十五期）拟推荐共青团工作先进个人名单如上，请大家注意查收";
                List<String> list=wordUtil.cutSpecial(str);
            }
        }).start();
        }


}