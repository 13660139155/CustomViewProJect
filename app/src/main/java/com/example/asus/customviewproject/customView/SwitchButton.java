package com.example.asus.customviewproject.customView;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

import com.example.asus.customviewproject.R;

/**
 * 仿微信滑动开关
 * Create by 陈健宇 at 2018/7/31
 */
public class SwitchButton extends View {

    private static final String TAG = SwitchButton.class.getSimpleName();
    private static final int OFFER = 6;
    private static final int MIN_DISTANCE = 1;

    private float mRatio = 0.5f;//整个控件的长宽比例
    private int mLeftSemiCircleRadius;//左半圆半径
    private int mRightRectangleBolder;//矩形右边界x坐标
    private int mLeftRectangleBolder;//矩形左边界x坐标
    private float mCircleCenter; //小圆圆心x坐标
    private float mPreAnimatedValue;
    private int midX; //左圆圆心和右圆圆心中间的坐标
    private float startX; //按下的x坐标
    private Paint mPathWayPaint;//轨道画笔
    private Paint mCirclePaint;//小圆画笔
    private boolean isAnim;
    private ValueAnimator mValueAnimator;

    private int mOpenBackground;//按钮打开后背景色
    private int mCloseBackground;//按钮关闭后的背景色
    private int mCircleColor;//圆形按钮颜色
    private float mCircleRadius;//小圆半径
    private boolean isOpen;//按钮状态

    private OnClickListener mOnClickListener;

    public SwitchButton(Context context) {
        super(context, null);
    }

    public SwitchButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {

        TypedArray typedValue = context.obtainStyledAttributes(attrs, R.styleable.SwitchButton);
        mOpenBackground = typedValue.getColor(R.styleable.SwitchButton_sb_openBackground, Color.BLACK);
        mCloseBackground = typedValue.getColor(R.styleable.SwitchButton_sb_closeBackground, Color.GRAY);
        mCircleColor = typedValue.getColor(R.styleable.SwitchButton_sb_circleColor, Color.WHITE);
        mCircleRadius = typedValue.getDimension(R.styleable.SwitchButton_sb_circleRadius, 0);
        isOpen = typedValue.getInt(R.styleable.SwitchButton_sb_status, 0) != 0;
        typedValue.recycle();

        mPathWayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPathWayPaint.setStyle(Paint.Style.FILL);
        int pathWayColor = isOpen ? mOpenBackground : mCloseBackground;
        mPathWayPaint.setColor(pathWayColor);

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setColor(mCircleColor);

        mValueAnimator = ValueAnimator.ofFloat(mLeftRectangleBolder, mRightRectangleBolder);
        mValueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnim = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnim = false;
                isOpen = !isOpen;
                mPreAnimatedValue = 0;
                toBolder(isOpen);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mValueAnimator.addUpdateListener(animation -> {
            float value = (float)animation.getAnimatedValue();
            mCircleCenter -= mPreAnimatedValue;
            mCircleCenter += value;
            mPreAnimatedValue = value;
            invalidate();
        });
        mValueAnimator.setInterpolator(new OvershootInterpolator());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        int width = Math.max(measuredWidth, measureHeight);
        int height = (int) (width * mRatio);
        //控件默认宽高
        int defaultWidth = 60;
        int defaultHeight = 30;
        //wrap_content情况
        setMeasuredDimension(
                measuredWidthMode == MeasureSpec.EXACTLY ? width : (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, defaultWidth, getResources().getDisplayMetrics()),
                measuredHeightMode == MeasureSpec.EXACTLY ? height : (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, defaultHeight, getResources().getDisplayMetrics())
        );
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //得出左圆的半径
        mLeftSemiCircleRadius = getHeight() / 2;
        //小圆的半径 = 大圆半径减OFFER
        if(!checkCircleRaduis(mCircleRadius)) mCircleRadius = mLeftSemiCircleRadius - OFFER;
        //长方形左边的坐标
        mLeftRectangleBolder = mLeftSemiCircleRadius;
        //长方形右边的坐标
        mRightRectangleBolder = getWidth() - mLeftSemiCircleRadius;
        //小圆的圆心x坐标一直在变化
        mCircleCenter = isOpen ? mRightRectangleBolder : mLeftRectangleBolder;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //左圆
        canvas.drawCircle(mLeftRectangleBolder, mLeftSemiCircleRadius, mLeftSemiCircleRadius, mPathWayPaint);
        //矩形
        canvas.drawRect(mLeftRectangleBolder, 0, mRightRectangleBolder, getMeasuredHeight(), mPathWayPaint);
        //右圆
        canvas.drawCircle(mRightRectangleBolder, mLeftSemiCircleRadius, mLeftSemiCircleRadius, mPathWayPaint);
        //小圆
        canvas.drawCircle(mCircleCenter, mLeftSemiCircleRadius, mCircleRadius, mCirclePaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //开始的x坐标
                startX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float distance = event.getX() - startX;
                mCircleCenter += distance;
                Log.d(TAG, "onTouchEvent: move， distance =  " + distance);
                //控制范围
                if (mCircleCenter > mRightRectangleBolder) {//最右
                    toBolder(true);
                } else if (mCircleCenter < mLeftRectangleBolder) {//最左
                    toBolder(false);
                }else {
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                float offset = Math.abs(event.getX() - Math.abs(startX));
                //分2种情况，1.点击 2.没滑过中点
                if (offset < MIN_DISTANCE) { //1.点击, 按下和抬起的距离小于DISTANCE确定是点击了
                    //不在动画的时候可以点击
                    if (!isAnim) {
                        if(isOpen){
                            float diff = mLeftRectangleBolder - mCircleCenter;
                            mValueAnimator.setFloatValues(0, diff);

                        }else{
                            float diff = mRightRectangleBolder - mCircleCenter;
                            mValueAnimator.setFloatValues(0, diff);
                        }
                        mValueAnimator.start();
                    }
                } else { //2.没滑过中点,回归原点
                    //滑到中间的x坐标
                    midX = getWidth() / 2;
                    if (mCircleCenter > midX) {//最右
                        toBolder(true);
                    } else {//最左
                        toBolder(false);
                    }
                }
                 break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        if(mValueAnimator != null){
            mValueAnimator.cancel();
            mValueAnimator.removeAllUpdateListeners();
            mValueAnimator = null;
        }
        super.onDetachedFromWindow();
    }


    /**
     * 让小圆移动到边界
     * @param isOpen 是否打开状态
     */
    private void toBolder(boolean isOpen) {
        this.isOpen = isOpen;
        if(isOpen){
            mCircleCenter = mRightRectangleBolder;
            mPathWayPaint.setColor(mOpenBackground);
        }else {
            mCircleCenter = mLeftRectangleBolder;
            mPathWayPaint.setColor(mCloseBackground);
        }
        invalidate();
    }

    /**
     * 检查半径的正确性
     * @param radius 小圆半径
     * @return true表示正确
     */
    private boolean checkCircleRaduis(float radius){
        float defaultCircleRadius = mLeftSemiCircleRadius - OFFER;
        return mCircleRadius > 0 && mCircleRadius < defaultCircleRadius;
    }

    /**
     * 打开按钮
     */
    public void open(){
        toBolder(true);
    }

    /**
     * 关闭按钮
     */
    public void  close(){
        toBolder(false);
    }

    public void setOpenBackground(@ColorInt int openBackground) {
        mOpenBackground = openBackground;
        invalidate();
    }

    public void setCloseBackground(@ColorInt int closeBackground) {
        mCloseBackground = closeBackground;
        invalidate();
    }

    public void setCircleColor(@ColorInt int circleColor) {
        mCircleColor = circleColor;
        invalidate();
    }

    public void setCircleRadius(float circleRadius) {
        if(!checkCircleRaduis(circleRadius)) return;
        mCircleRadius = circleRadius;
        invalidate();
    }

    public void setClickListener(OnClickListener onClickListener){
        this.mOnClickListener  = onClickListener;
    }

    public interface OnClickListener{
        void onRightClick();
        void onLeftClick();
    }


}
