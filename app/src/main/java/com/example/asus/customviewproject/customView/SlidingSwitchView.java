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

import com.example.asus.customviewproject.R;

/**
 * 仿微信滑动开关
 * Create by 陈健宇 at 2018/7/31
 */
public class SlidingSwitchView extends View {

    private int mWidth = 60, mHeight = 28;//控件默认宽高

    private boolean isRight = true, isLeft = false, isAnimate = false;

    private int mLeftRoundRadius;//左圆半径
    private int mRectangle_x;//矩形x坐标

    private int mSmallRoundRadius;//小圆半径
    private float mSmallCenter_x; //小圆圆心x坐标

    private int mid_x; //左圆圆心和右圆圆心中间的坐标

    private float startx; //按下的x坐标
    private float endx;//移动的结束坐标

    private Paint mPaint;//画笔
    private Paint mSmallPaint;//小圆画笔

    private int mMeasuredWidth;//控件宽
    private int mMeasuredHeight; //控件高

    private int mColorBackground;//按钮的背景色
    private int mColorForeground;//按钮的前景色(按钮关闭时)
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
                    mPaint.setColor(mColorForeground);
                    if(mSmallCenter_x > mLeftRoundRadius){
                        mSmallCenter_x -= 5;
                        mHandler.sendEmptyMessageDelayed(TO_LEFT, 1);
                        isAnimate = true;
                    }else {
                        mSmallCenter_x = mLeftRoundRadius;
                        //设置滑动不可点击
                        setEnabled(true);
                        isAnimate = false;
                    }
                    break;
                case 1://往右
                    mPaint.setColor(mColorBackground);
                    if(mSmallCenter_x < mRectangle_x){
                        mSmallCenter_x += 5;
                        mHandler.sendEmptyMessageDelayed(TO_RIGHT, 1);
                        isAnimate = true;
                    }else {
                        mSmallCenter_x = mRectangle_x;
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

    public SlidingSwitchView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedValue = context.obtainStyledAttributes(attrs, R.styleable.SlidingSwitchView);
        mColorBackground = typedValue.getColor(R.styleable.SlidingSwitchView_colorBackground, 0xFF5000);
        mColorForeground = typedValue.getColor(R.styleable.SlidingSwitchView_colorForeground, Color.GRAY);
        typedValue.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mColorBackground);

        mSmallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSmallPaint.setStyle(Paint.Style.FILL);
        mSmallPaint.setColor(Color.WHITE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measuredWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);

        if(measuredWidthMode == MeasureSpec.EXACTLY){
            mMeasuredWidth = measuredWidth;
        }else {
            mMeasuredWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mWidth, getResources().getDisplayMetrics());
        }

        if(measuredHeightMode == MeasureSpec.EXACTLY){
            mMeasuredHeight = measureHeight;
        }else {
            mMeasuredHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mHeight, getResources().getDisplayMetrics());
        }

        //得出左圆的半径
        mLeftRoundRadius = mMeasuredHeight / 2;
        //小圆的半径 = 大圆半径减5
        mSmallRoundRadius = mLeftRoundRadius - 5;
        //长方形右边的坐标
        mRectangle_x = mMeasuredWidth - mLeftRoundRadius;
        //小圆的圆心x坐标一直在变化
        mSmallCenter_x = mRectangle_x;

        setMeasuredDimension(mMeasuredWidth, mMeasuredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //左圆
        canvas.drawCircle(mLeftRoundRadius, mLeftRoundRadius, mLeftRoundRadius, mPaint);
        //矩形
        canvas.drawRect(mLeftRoundRadius, 0, mRectangle_x, mMeasuredHeight, mPaint);
        //右圆
        canvas.drawCircle(mRectangle_x, mLeftRoundRadius, mLeftRoundRadius, mPaint);
        //小圆
        canvas.drawCircle(mSmallCenter_x, mLeftRoundRadius, mSmallRoundRadius, mSmallPaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //开始的x坐标
                startx = event.getX();
                endx = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float distance = event.getX() - endx;
                mSmallCenter_x += distance;
                //控制范围
                if (mSmallCenter_x > mRectangle_x) {
                    isRight = true;
                    mSmallCenter_x = mRectangle_x;
                    mPaint.setColor(mColorBackground);
                } else if (mSmallCenter_x < mLeftRoundRadius) {
                    //最左
                    mSmallCenter_x = mLeftRoundRadius;
                    isRight = false;
                    mPaint.setColor(Color.GRAY);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                //分2种情况，1.点击 2.没滑过中点
                float up_x = event.getX();
                if (Math.abs(up_x - Math.abs(startx)) < 3) { //1.点击, 按下和抬起的距离小于1确定是点击了
                    //不在动画的时候可以点击
                    if (!isAnimate) {
                        startGO();
                    }
                } else { //2.没滑过中点,回归原点
                    //滑到中间的x坐标
                    mid_x = (mLeftRoundRadius + (mRectangle_x - mLeftRoundRadius) / 2);
                    if (mSmallCenter_x < mid_x) {
                        //最左
                        isRight = false;
                        mSmallCenter_x = mLeftRoundRadius;
                        mPaint.setColor(Color.GRAY);
                        setEnabled(true);
                    } else {
                        //最右
                        isRight = true;
                        mSmallCenter_x = mRectangle_x;
                        mPaint.setColor(mColorBackground);
                        setEnabled(true);
                    }
                    invalidate();
                }
                //到了两端都有点击事件
                if(mOnClickListener != null){
                    if (mSmallCenter_x == mRectangle_x) {
                        mOnClickListener.onRightClick();
                    }else if(mSmallCenter_x == mLeftRoundRadius){
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
