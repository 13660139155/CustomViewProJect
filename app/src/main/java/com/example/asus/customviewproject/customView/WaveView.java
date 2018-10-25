package com.example.asus.customviewproject.customView;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.example.asus.customviewproject.R;

import androidx.annotation.Nullable;

/**
 *波浪控件
 * Created by 陈健宇 at 2018/10/23
 */
public class WaveView extends View {

    private Paint mWavePaint, mTextPaint;
    private int mItemWaveLength;
    private int dx = 0;
    private Bitmap mDstBitmap, mSrcBitmap;

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mDstBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.wave);
        mWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mItemWaveLength = mDstBitmap.getWidth();
        mTextPaint.setColor(Color.RED);
        mTextPaint.setTextSize(10);
        startAnim();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int h = Math.min(getWidth(), getHeight());
        mSrcBitmap = zoomBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.cricle_shape), h, h);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        int x = getWidth() / 2 - mSrcBitmap.getWidth() / 2;
        int y = getHeight() / 2 - mSrcBitmap.getHeight() / 2;
        int tx = 0;
        int ty = 100;
        //先画上圆形
        canvas.drawBitmap(mSrcBitmap, x, y, mWavePaint);
        //离屏绘制
        int layerId = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        canvas.drawBitmap(mDstBitmap,
                new Rect(dx, 0, dx + mSrcBitmap.getWidth(), mSrcBitmap.getHeight()),
                new Rect(x, y, mSrcBitmap.getWidth() + x, mSrcBitmap.getHeight() + y), mWavePaint);
        mWavePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(mSrcBitmap, x, y, mWavePaint);
        mWavePaint.setXfermode(null);
        canvas.restoreToCount(layerId);
        //画文字
        canvas.drawText("loading..." , tx, ty, mTextPaint);
    }

    /**
     * 启动波纹动画
     */
    @SuppressLint("WrongConstant")
    private void startAnim() {
        ValueAnimator animator = ValueAnimator.ofInt(0, mItemWaveLength);
        animator.setDuration(1500);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(valueAnimate -> {
            dx = (int) valueAnimate.getAnimatedValue();
            postInvalidate();
        });
        animator.start();
    }

    /**
     * 获得合适宽高的Bitmap
     */
    private Bitmap zoomBitmap(Bitmap bm, int newWidth ,int newHeight){
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
         float scaleWidth = ((float) newWidth) / width;
         float scaleHeight = ((float) newHeight) / height;
         // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
         Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
         return newbm;
    }

}
