package com.example.asus.customviewproject.customView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

/**
 * 圆形波浪控件
 * Created by 陈健宇 at 2018/10/25
 */
public class CircleWaveView extends View {

    private Paint mWavePaint, mCirclePaint;
    private Path mPath;
    private int mItemWaveLength = 1000;//波浪波长
    private int mHalfWave = mItemWaveLength / 2;//波浪半波长
    private int dx = 0;
    private int mRadius = 800;
    private ValueAnimator mAnimator;

    public CircleWaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mWavePaint = new Paint();
        mCirclePaint = new Paint();
        mPath = new Path();
        mWavePaint.setColor(Color.RED);
        mWavePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mCirclePaint.setColor(Color.GREEN);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(5);
        startAnim();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mRadius = getWidth() / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPath.reset();
        //波浪Y坐标
        int waveY = 300;
        mPath.moveTo(-mItemWaveLength + dx, getHeight() / 2);//mPath的起始位置向左移一个波长
        for (int i = -mItemWaveLength; i <= getWidth() + mItemWaveLength; i += mItemWaveLength){
            mPath.rQuadTo(mHalfWave / 2, -200, mHalfWave, 0);//一个波长中的前半个波
            mPath.rQuadTo(mHalfWave / 2, 200, mHalfWave,0);//一个波长中的后半个波
        }
        //把整体波形闭合起来
        mPath.lineTo(getWidth() / 2, getHeight() / 2 + mRadius / 2);
        mPath.close();
        canvas.drawPath(mPath, mWavePaint);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mRadius, mCirclePaint);
    }

    private void startAnim(){
        mAnimator = ValueAnimator.ofInt(0, mItemWaveLength);
        mAnimator.setDuration(1000);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                dx = (int)animation.getAnimatedValue();
                postInvalidate();
            }
        });
        mAnimator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        mAnimator.cancel();
        super.onDetachedFromWindow();
    }
}
