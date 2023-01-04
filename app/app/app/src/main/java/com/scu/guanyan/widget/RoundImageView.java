package com.scu.guanyan.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import androidx.annotation.Nullable;

/**
 * @program: Toolt
 * @author: 陈博文
 * @create: 2022-07-04 17:16
 * @description: 原型imageView实现
 **/
@SuppressLint("AppCompatCustomView")
public class RoundImageView extends ImageView {
    private static PorterDuffXfermode SRC_IN = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

    private int width;
    private int height;
    private Rect rect;
    private Paint paint;
    private int radius;

    public RoundImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        paint = new Paint();
    }

    public RoundImageView(Context context) {
        super(context);
        paint = new Paint();
    }

    public RoundImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rect = new Rect(0, 0, w, h);
        width = w;
        height = h;
        radius = Math.min(width, height) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // xml中要用src,不能用background,否则这里drawable会为null
        Bitmap bitmap = generateBitMap();
        if (bitmap == null) {
            return;
        }
        //通过给Paint设置BitmapShader的方式画圆形图
        canvas.drawCircle(width / 2, height / 2, radius, getShaderPaint(bitmap));
        //通过设置Xfermode的方式获取圆形Bitmap来实现圆形图
        canvas.drawBitmap(getRoundBitmap(bitmap), rect, rect, paint);
    }

    /**
     * 生成一个BitMap好在上面画圆
     * @return
     */
    private Bitmap generateBitMap() {
        Drawable drawable = getDrawable();
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof ColorDrawable) {
            ((ColorDrawable) drawable).getColor();
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            // 这里将得到类似于:ff ff ff ff的颜色字符串
//            String color = Integer.toHexString(((ColorDrawable) drawable).getColor());
//            System.out.println(color);
            canvas.drawColor(((ColorDrawable) drawable).getColor());
            return bitmap;
        } else if (drawable instanceof VectorDrawable) {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
        return null;
    }

    /**
     * 获取一个bitmap的shader，用来对部分未着色的部分进行着色，防止出现图片的部分未着色
     * see{@linktourl https://blog.csdn.net/zouzoutingting_/article/details/84999133}
     * @param bitmap
     * @return
     */
    private Paint getShaderPaint(Bitmap bitmap) {
        Bitmap scaleBitmap = getScaleBitmap(bitmap, radius);
        BitmapShader bitmapShader = new BitmapShader(scaleBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(bitmapShader);
        paint.setAntiAlias(true);
        return paint;
    }

    /**
     * 将bitmap切割未圆形
     * @param bitmap
     * @return
     */
    private Bitmap getRoundBitmap(Bitmap bitmap) {
        Bitmap scaleBitmap = getScaleBitmap(bitmap, radius);
        Bitmap out = Bitmap.createBitmap(scaleBitmap.getWidth(), scaleBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(out);
        Rect rect = new Rect(0, 0, scaleBitmap.getWidth(), scaleBitmap.getHeight());
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(scaleBitmap.getWidth() / 2, scaleBitmap.getHeight() / 2, scaleBitmap.getWidth() / 2, paint);
        paint.setXfermode(SRC_IN);
        canvas.drawBitmap(scaleBitmap, rect, rect, paint);
        paint.reset();
        return out;
    }

    /**
     * 获取图片最中间的最大的正方形框,将在这个正方形框中画圆
     * @param bitmap
     * @param radius
     * @return
     */
    private Bitmap getScaleBitmap(Bitmap bitmap, int radius) {
        Bitmap scaledSrcBmp;
        // 期望的正方形的边长
        int diameter = radius * 2;

        int bmpWidth = bitmap.getWidth();
        int bmpHeight = bitmap.getHeight();
        int squareWidth = 0, squareHeight = 0;
        int x = 0, y = 0;
        Bitmap squareBitmap;
        if (bmpHeight > bmpWidth) {// 高大于宽
            squareWidth = squareHeight = bmpWidth;
            x = 0;
            y = (bmpHeight - bmpWidth) / 2;
            // 截取正方形图片
            squareBitmap = Bitmap.createBitmap(bitmap, x, y, squareWidth, squareHeight);
        } else if (bmpHeight < bmpWidth) {// 宽大于高
            squareWidth = squareHeight = bmpHeight;
            x = (bmpWidth - bmpHeight) / 2;
            y = 0;
            squareBitmap = Bitmap.createBitmap(bitmap, x, y, squareWidth, squareHeight);
        } else {
            squareBitmap = bitmap;
        }
        if (squareBitmap.getWidth() != diameter || squareBitmap.getHeight() != diameter) {
            scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter, diameter, true);
        } else {
            scaledSrcBmp = squareBitmap;
        }
        return scaledSrcBmp;
    }
}
