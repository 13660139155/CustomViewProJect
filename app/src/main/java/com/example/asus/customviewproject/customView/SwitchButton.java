package com.example.asus.customviewproject.customView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.asus.customviewproject.R;

/**
 * 仿微信滑动开关
 * Create by 陈健宇 at 2018/7/31
 */
public class SwitchButton extends View {

    private static final String TAG = SwitchButton.class.getSimpleName();
    private static final int OFFER = 6;
    private boolean isRight = true, isLeft = false, isAnimate = false;

    private float mRatio = 0.5f;//整个控件的长宽比例
    private int mLeftSemiCircleRadius;//左半圆半径
    private int mRightRectangleBolder;//矩形右边界x坐标
    private int mLeftRectangleBolder;//矩形左边界x坐标
    private float mCircleCenter; //小圆圆心x坐标

    private int mid_x; //左圆圆心和右圆圆心中间的坐标
    private float startX; //按下的x坐标
    private float endX;//移动的结束坐标

    private Paint mPathWayPaint;//轨道画笔
    private Paint mCirclePaint;//小圆画笔

    private int mOpenBackground;//按钮打开后背景色
    private int mCloseBackground;//按钮后的背景色
    private int mCircleColor;//圆形按钮颜色
    private float mCircleRadius;//小圆半径

    private OnClickListener mOnClickListener;
    private int TO_LEFT = 0;
    private int TO_RIGHT = 1;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0://往左
                    mPathWayPaint.setColor(mCloseBackground);
                    if(mCircleCenter > mLeftSemiCircleRadius){
                        mCircleCenter -= 5;
                        mHandler.sendEmptyMessageDelayed(TO_LEFT, 1);
                        isAnimate = true;
                    }else {
                        mCircleCenter = mLeftSemiCircleRadius;
                        //设置滑动不可点击
                        setEnabled(true);
                        isAnimate = false;
                    }
                    break;
                case 1://往右
                    mPathWayPaint.setColor(mOpenBackground);
                    if(mCircleCenter < mLeftRectangleBolder){
                        mCircleCenter += 5;
                        mHandler.sendEmptyMessageDelayed(TO_RIGHT, 1);
                        isAnimate = true;
                    }else {
                        mCircleCenter = mLeftRectangleBolder;
                        setEnabled(true);
                        isAnimate = false;
                    }
                    break;
                default:
                    break;
            }
            invalidate();
        }
    };

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
        typedValue.recycle();

        mPathWayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPathWayPaint.setStyle(Paint.Style.FILL);
        mPathWayPaint.setColor(mOpenBackground);

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setColor(mCircleColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        //控件默认宽高
        int width = 60;
        int height = 30;
        //wrap_content情况
        setMeasuredDimension(
                measuredWidthMode == MeasureSpec.EXACTLY ? measuredWidth : (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, getResources().getDisplayMetrics()),
                measuredHeightMode == MeasureSpec.EXACTLY ? measureHeight : (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, getResources().getDisplayMetrics())
        );
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //得出左圆的半径
        mLeftSemiCircleRadius = getHeight() / 2;
        //小圆的半径 = 大圆半径减OFFER
        float defaultCircleRadius = mLeftSemiCircleRadius - OFFER;
        if(mCircleRadius <= 0 || mCircleRadius > defaultCircleRadius) mCircleRadius = defaultCircleRadius;
        //长方形左边的坐标
        mLeftRectangleBolder = mLeftSemiCircleRadius;
        //长方形右边的坐标
        mRightRectangleBolder = getWidth() - mLeftSemiCircleRadius;
        //小圆的圆心x坐标一直在变化
        mCircleCenter = mLeftRectangleBolder;
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
                endX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float distance = event.getX() - endX;
                mCircleCenter += distance;
                //控制范围
                if (mCircleCenter > mLeftRectangleBolder) {
                    isRight = true;
                    mCircleCenter = mLeftRectangleBolder;
                    mPathWayPaint.setColor(mOpenBackground);
                } else if (mCircleCenter < mLeftSemiCircleRadius) {
                    //最左
                    mCircleCenter = mLeftSemiCircleRadius;
                    isRight = false;
                    mPathWayPaint.setColor(Color.GRAY);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                //分2种情况，1.点击 2.没滑过中点
                float up_x = event.getX();
                if (Math.abs(up_x - Math.abs(startX)) < 3) { //1.点击, 按下和抬起的距离小于1确定是点击了
                    //不在动画的时候可以点击
                    if (!isAnimate) {
                        startGO();
                    }
                } else { //2.没滑过中点,回归原点
                    //滑到中间的x坐标
                    mid_x = (mLeftSemiCircleRadius + (mLeftRectangleBolder - mLeftSemiCircleRadius) / 2);
                    if (mCircleCenter < mid_x) {
                        //最左
                        isRight = false;
                        mCircleCenter = mLeftSemiCircleRadius;
                        mPathWayPaint.setColor(Color.GRAY);
                        setEnabled(true);
                    } else {
                        //最右
                        isRight = true;
                        mCircleCenter = mLeftRectangleBolder;
                        mPathWayPaint.setColor(mOpenBackground);
                        setEnabled(true);
                    }
                    invalidate();
                }
                //到了两端都有点击事件
                if(mOnClickListener != null){
                    if (mCircleCenter == mLeftRectangleBolder) {
                        mOnClickListener.onRightClick();
                    }else if(mCircleCenter == mLeftSemiCircleRadius){
                        mOnClickListener.onLeftClick();
                    }
                }
                 break;
            default:
                break;
        }
        return true;
    }

    public void setClickListener(OnClickListener onClickListener){
        this.mOnClickListener  = onClickListener;
    }

    public interface OnClickListener{
        void onRightClick();
        void onLeftClick();
    }

    private void startGO() {
        if (isRight) {
            isRight = false;
            mHandler.sendEmptyMessageDelayed(TO_LEFT, 40);
        }else {
            isRight = true;
            mHandler.sendEmptyMessageDelayed(TO_RIGHT, 40);
        }
    }
}
