package com.example.asus.customviewproject.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.asus.customviewproject.R;

/**
 * Create by 陈健宇 at 2018/7/29
 */
public class RedPointView extends FrameLayout {

    private PointF mStartPoint, mCurPoint;//起始点坐标，移动点坐标
    private float mRadius = 20f;
    private Paint mPaint;
    private Path mPath;
    private boolean isTouch;//是否按下
    private TextView mTextView;



    public RedPointView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mStartPoint = new PointF(100, 100);
        mCurPoint = new PointF();
        mPaint = new Paint();
        mPath = new Path();
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);

        mTextView = new TextView(context);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mTextView.setLayoutParams(params);
        mTextView.setPadding(10, 10, 10, 10);
        mTextView.setBackgroundResource(R.drawable.tv_bg);
        mTextView.setTextColor(Color.GREEN);
        mTextView.setText("99+");

        addView(mTextView);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        canvas.saveLayer(new RectF(0, 0, getWidth(), getHeight()), mPaint, Canvas.ALL_SAVE_FLAG);
        canvas.drawCircle(mStartPoint.x, mStartPoint.y, mRadius, mPaint);
        if (isTouch) {
            calculatePath();
            caculateRadius();
            canvas.drawCircle(mCurPoint.x, mCurPoint.y, mRadius, mPaint);
            canvas.drawPath(mPath, mPaint);
            //将textview的中心放在当前手指位置
            mTextView.setX(mCurPoint.x - mTextView.getWidth() / 2);
            mTextView.setY(mCurPoint.y - mTextView.getHeight() / 2);
        }else {
            mTextView.setX(mStartPoint.x - mTextView.getWidth() / 2);
            mTextView.setY(mStartPoint.y - mTextView.getHeight() / 2);
        }
        canvas.restore();

        super.dispatchDraw(canvas);

        super.dispatchDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                // 判断触摸点是否在tipImageView中
                Rect rect = new Rect();
                int[] location = new int[2];
                mTextView.getLocationOnScreen(location);
                rect.left = location[0];
                rect.top = location[1];
                rect.right = mTextView.getWidth() + location[0];
                rect.bottom = mTextView.getHeight() + location[1];
                if (rect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    isTouch = true;
                }else {
                    isTouch = false;
                }
            }
                break;
            case MotionEvent.ACTION_UP:
                //抬起手指时还原位置
                 isTouch = false;
                break;
            default:
                break;
        }
        mCurPoint.set(event.getX(), event.getY());
        postInvalidate();
        return true;
    }

    /** 计算出贝塞尔曲线的四个点坐标 */
    private void calculatePath() {

        float x = mCurPoint.x;
        float y = mCurPoint.y;
        float startX = mStartPoint.x;
        float startY = mStartPoint.y;
        // 根据角度算出四边形的四个点
        float dx = x - startX;
        float dy = y - startY;
        double a = Math.atan(dy / dx);
        float offsetX = (float) (mRadius * Math.sin(a));
        float offsetY = (float) (mRadius * Math.cos(a));

        // 根据角度算出四边形的四个点
        float x1 = startX + offsetX;
        float y1 = startY - offsetY;

        float x2 = x + offsetX;
        float y2 = y - offsetY;

        float x3 = x - offsetX;
        float y3 = y + offsetY;

        float x4 = startX - offsetX;
        float y4 = startY + offsetY;

        float anchorX = (startX + x) / 2;
        float anchorY = (startY + y) / 2;

        mPath.reset();
        mPath.moveTo(x1, y1);
        mPath.quadTo(anchorX, anchorY, x2, y2);
        mPath.lineTo(x3, y3);
        mPath.quadTo(anchorX, anchorY, x4, y4);
        mPath.lineTo(x1, y1);
    }

    /** 动态计算圆的半径 */
    private void caculateRadius(){

        float x = mCurPoint.x;
        float y = mCurPoint.y;
        float startX = mStartPoint.x;
        float startY = mStartPoint.y;
        float distance = (float) Math.sqrt(Math.pow(y - startY, 2) + Math.pow(x - startX, 2));
        mRadius = mRadius - distance / 15;
        if(mRadius < 9){
            mRadius = 9;
        }
        if(distance > 500){
            mRadius = 0;
        }
        if(distance > 600){
            mTextView.setVisibility(GONE);
        }
    }
}
