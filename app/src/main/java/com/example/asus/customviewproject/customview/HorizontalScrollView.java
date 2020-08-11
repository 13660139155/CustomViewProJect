package com.example.asus.customviewproject.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;


/**
 * 类似于ViewPager，实现水平滑动，解决嵌套滑动（左右滑动与上下滑动冲突）
 * Create by 陈健宇 at 2018/8/11
 */
public class HorizontalScrollView extends ViewGroup {

    private final String TAG = HorizontalScrollView.class.getSimpleName();

    private int mChildrenSize;
    private int mChildWidth;
    private int mChildIndex;

    // 分别记录上次滑动的坐标
    private int mLastX = 0;
    private int mLastY = 0;

    // 分别记录上次滑动的坐标(onInterceptTouchEvent)
    private int mLastXIntercept = 0;
    private int mLastYIntercept = 0;

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;//追踪手指在滑动过程中的速度

    public HorizontalScrollView(Context context) {
        super(context);
        init();
    }

    public HorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth = 0;
        int measureHeight = 0;
        final int childCount = getChildCount();

        //在此方法中会遍历调用所有子元素的measure方法
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        //以下是系统测量出的宽高和模式
        int widthSpaceSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpaceSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);

        //以下我们自己要做wrap_content的适配
        final View view = getChildAt(0);
        if(childCount == 0){//可以完善 - 没有子元素时，应该根据LayoutParam中的宽/高做相应处理
            setMeasuredDimension(0, 0);
        }else if(widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST){//可以完善 - 要考虑自己的padding和子View的margin
            measureWidth = view.getMeasuredWidth() * childCount;
            measureHeight = view.getMeasuredHeight();
            setMeasuredDimension(measureWidth, measureHeight);
        }else if(heightSpecMode == MeasureSpec.AT_MOST){
            measureWidth = widthSpaceSize;
            measureHeight = view.getMeasuredHeight();
            setMeasuredDimension(measureWidth, measureHeight);
        }else if (widthSpecMode == MeasureSpec.AT_MOST){
            measureWidth = view.getMeasuredWidth() * childCount;
            measureHeight = heightSpaceSize;
            setMeasuredDimension(measureWidth, measureHeight);
        }
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        if(b){
            int childLeft = 0;
            final int childCount = getChildCount();
            mChildrenSize = childCount;
            for(int index  = 0; index < childCount; index++){
                final View childView = getChildAt(index);
                if(childView.getVisibility() != GONE){//可以完善 - 放置子元素时要考虑自身的padding和子元素的margin
                    final int childWidth = childView.getMeasuredWidth();
                    mChildWidth = childWidth;
                    //让子元素自己确定位置
                    childView.layout(childLeft, 0, childLeft + childWidth, childView.getMeasuredHeight());
                    childLeft += childWidth;
                }
            }
        }
    }

    /** 外部拦截法 */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = false;
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN://不能拦截，不然所有事件都会交给父布局处理
                intercepted = false;
                if(!mScroller.isFinished()){
                    mScroller.abortAnimation();
                    intercepted = true;
                }
                Log.d(TAG, "onInterceptTouchEvent: down");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onInterceptTouchEvent: move");
                int deltaX = x - mLastXIntercept;
                int deltaY = y - mLastYIntercept;
                if(Math.abs(deltaX) > Math.abs(deltaY)){
                    intercepted = true;
                }else {
                    intercepted = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onInterceptTouchEvent: up");
                intercepted = false;
                break;
            default:
                break;
        }
        mLastXIntercept = (int) ev.getX();
        mLastYIntercept = (int) ev.getY();
        mLastX = (int) ev.getX();
        mLastY = (int) ev.getY();
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVelocityTracker.addMovement(event);//追踪当前单击事件的速度
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onTouchEvent: down");
                if(!mScroller.isFinished()){
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onTouchEvent: move");
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;
                scrollBy(-deltaX, 0);
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onTouchEvent: up");
                int scrollX = getScrollX();
                mVelocityTracker.computeCurrentVelocity(1000);
                float velocityX = mVelocityTracker.getXVelocity();
                if(Math.abs(velocityX) >= 50){
                    mChildIndex = velocityX > 0 ? mChildIndex - 1 : mChildIndex + 1;
                }else {//恢复原位
                    mChildIndex = (scrollX + mChildWidth / 2) / mChildWidth;
                }
                mChildIndex = Math.max(0, Math.min(mChildIndex, mChildrenSize - 1));
                int dx = mChildIndex * mChildWidth - scrollX;
                smoothScrollBy(dx, 0);
                mVelocityTracker.clear();
                break;
            default:
                break;
        }
        mLastX = (int) event.getX();
        mLastY = (int) event.getY();
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        //重置并回收内存，防止内存泄漏
        if(mVelocityTracker != null){
            mVelocityTracker.recycle();
        }
        super.onDetachedFromWindow();
    }

    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }else {
            Log.d(TAG, "computeScroll: ");
            Log.d(TAG, "getMesureWidth: " + getMeasuredWidth());
            Log.d(TAG, "getLeft: " + getLeft());
            Log.d(TAG, "getRight: " + getRight());
            Log.d(TAG, "getWidth: " + getWidth());
            Log.d(TAG, "getScrollX: " + getScrollX());
            Log.d(TAG, "getChildAt(0).getLeft: " + getChildAt(0).getLeft());
            Log.d(TAG, "getChildAt(1).getRight: " + getChildAt(mChildrenSize - 1).getRight());
        }
    }

    private void smoothScrollBy(int dx, int dy){
        mScroller.startScroll(getScrollX(), 0, dx, 0, 500);
        invalidate();
    }

    private void init(){
        if(mScroller == null){
            mScroller = new Scroller(getContext());
            mVelocityTracker =  VelocityTracker.obtain();
        }
    }
}
