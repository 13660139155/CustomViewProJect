package com.example.asus.customviewproject.customView;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.OverScroller;

import com.example.asus.customviewproject.MyConstant;
import com.example.asus.customviewproject.R;
import com.example.asus.customviewproject.view.ViewPagerFragment;


/**
 * 一个滑动控件
 * Create by 陈健宇 at 2018/8/18
 */
public class StickNavLayout extends LinearLayout {

    private String TAG = MyConstant.SL_TAG;
    private View mTopView;
    private View mTabView;
    private ViewPager mViewPager;
    private int mTopViewHeight;
    private int mTabViewHeight;
    private ListView mInnerListView;
    private FragmentPagerAdapter mFragmentPagerAdapter;
    private OverScroller mScroller;//滑动处理类
    private VelocityTracker mVelocityTracker;
    private int mPosition;
    private int mTouchSlop;
    private int mMaximumVelocity, mMinimumVelocity;
    private float mLastY;
    private boolean isDragging = false;
    boolean isTopHidden = false;

    public StickNavLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        mScroller = new OverScroller(context);
        mVelocityTracker = VelocityTracker.obtain();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMaximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        mMinimumVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ViewGroup.LayoutParams params = mViewPager.getLayoutParams();
        params.height = getMeasuredHeight() - mTopView.getMeasuredHeight();
        setMeasuredDimension(getMeasuredWidth(), mTopView.getMeasuredHeight() + mTabView.getMeasuredHeight() + mViewPager.getMeasuredHeight());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d(TAG, "onFinishInflate: ");
        mTopView = findViewById(R.id.relative_details);
        mTabView = findViewById(R.id.tab_layout);
        mViewPager = findViewById(R.id.view_pager);
        mFragmentPagerAdapter = (FragmentPagerAdapter) mViewPager.getAdapter();
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPosition = position;
                if(mFragmentPagerAdapter == null){
                    mFragmentPagerAdapter = (FragmentPagerAdapter) mViewPager.getAdapter();
                }
                ViewPagerFragment fragment = (ViewPagerFragment) mFragmentPagerAdapter.getItem(position);
                View view = fragment.getmView();
                mInnerListView = view.findViewById(R.id.list_view);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTopViewHeight = mTopView.getMeasuredHeight();
        mTabViewHeight = mTabView.getMeasuredHeight();
        Log.d(TAG, "onSizeChanged: mTopViewHeight: " + mTopViewHeight + " mTabViewHeight: " + mTabViewHeight);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onInterceptTouchEvent: down");
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onInterceptTouchEvent: move");
                float dy = y - mLastY;
                if (Math.abs(dy) > mTouchSlop) {
                    isDragging = true;
                    if(mInnerListView == null){
                        if(mFragmentPagerAdapter == null){
                            mFragmentPagerAdapter = (FragmentPagerAdapter) mViewPager.getAdapter();
                        }
                        ViewPagerFragment fragment = (ViewPagerFragment) mFragmentPagerAdapter.getItem(mPosition);
                        View view = fragment.getmView();
                        mInnerListView = view.findViewById(R.id.list_view);
                    }
                    if (!isTopHidden || (mInnerListView.getFirstVisiblePosition() == 0 && isTopHidden && dy > 0)) {
                        return true;
                    }
                }
            break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onInterceptTouchEvent: up");
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVelocityTracker.addMovement(event);
        float y = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onTouchEvent: down");
                if(!mScroller.isFinished()){
                    mScroller.abortAnimation();
                }
                mVelocityTracker.clear();
                mVelocityTracker.addMovement(event);
                mLastY = y;
                return true;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onTouchEvent: move");
                float dy = y - mLastY;
                if(!isDragging && Math.abs(dy) > mTouchSlop){
                    isDragging = true;
                }
                if(isDragging){
                    scrollBy(0, (int) -dy);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                isDragging = false;
                if (!mScroller.isFinished()){
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_UP:
                isDragging = false;
                Log.d(TAG, "onTouchEvent: up");
                mVelocityTracker.computeCurrentVelocity(1000);
                int velocityY = (int) mVelocityTracker.getYVelocity();
                if (Math.abs(velocityY) > mMinimumVelocity){
                    mScroller.fling(0, getScrollY(), 0, -velocityY, 0, 0, 0, mTopViewHeight);
                    invalidate();
                }
                mVelocityTracker.clear();
                break;
            default:
                break;
        }
        mLastY = y;
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mVelocityTracker != null){
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            invalidate();
        }
    }


    @Override
    public void scrollTo(int x, int y)
    {
        if (y < 0)
        {
            y = 0;
        }
        if (y > mTopViewHeight)
        {
            y = mTopViewHeight;
        }
        if (y != getScrollY())
        {
            super.scrollTo(x, y);
        }
        isTopHidden = getScrollY() == mTopViewHeight;
    }
}
