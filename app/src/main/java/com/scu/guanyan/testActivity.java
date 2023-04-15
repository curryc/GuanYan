package com.scu.guanyan;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.scu.guanyan.utils.ORC.OCRUtils;

import java.io.IOException;

public class testActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ivInputImage=findViewById(R.id.imageView2);
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



}