package com.example.asus.customviewproject.customview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.asus.customviewproject.R;

/**
 * 用于显示长文本，可以展开的TextView
 * Create by 陈健宇 at 2018/7/19
 */
public class PullTextView extends LinearLayout implements View.OnClickListener{

    private TextView mTextView;
    private ImageButton mButton;
    private TextView mTextViewBottom;
    private Context mContext;

    private boolean isAnimator ;        //是否处于动画当中
    private boolean isMaxHeightMeasure;     //是否进行了TextView最大行数的测量
    private boolean isMinHeightMeasure;     //是否进行了TexView可见行数的测量
    private boolean isPull;//是否处于展开状态。默认为隐藏

    /** 位置大小相关属性 */
    private int mTextViewPullHeight ;//textView显示全部也就是下拉状态的高度
    private int mTextViewNotPullHeight;//textView没有显示全部的高度
    private int mTextVisibilityCount = 3;  //隐藏时TextView可以显示的最大的行数
    private OnTextViewPullListener mOnTextViewPullListener;

    public PullTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //没有内容的时候
        if(mTextView == null || mButton == null){
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return ;
        }

        //有内容，但是内容比较短的时候，正常显示TextView，但是相应的隐藏Button
        if(mTextView.getLineCount() <= mTextVisibilityCount){
            mTextView.setVisibility(View.VISIBLE);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        //有内容，并且显示的内容比较长的时候，这里我们显示TextView、Button。
        mButton.setVisibility(View.VISIBLE);
        mTextView.setVisibility(VISIBLE);
        if(!isMaxHeightMeasure && mTextViewPullHeight == 0){
            mTextView.setMaxLines(Integer.MAX_VALUE);
            mTextView.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            mTextViewPullHeight = mTextView.getMeasuredHeight() ;//测量出最大行数
            isMaxHeightMeasure = true ;
        }

        if(!isMinHeightMeasure && mTextViewNotPullHeight == 0){
            mTextView.setMaxLines(mTextVisibilityCount);
            mTextView.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            mTextViewNotPullHeight = mTextView.getMeasuredHeight();//测量出最小行数
            isMinHeightMeasure = true ;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void setOrientation(int orientation) {
        if(orientation == LinearLayout.HORIZONTAL){
            throw new IllegalArgumentException("参数错误：当前控件，不支持水平");
        }
        super.setOrientation(orientation);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mTextView.getLineCount() < mTextVisibilityCount){
            mTextViewBottom = new TextView(mContext);
            ViewGroup.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
            mTextViewBottom.setLayoutParams(params);
            addView(mTextViewBottom);
        }else {
            if(mTextViewBottom != null){
                mTextViewBottom.setVisibility(GONE);
            }
        }
        super.onDraw(canvas);
    }

    /**
     * 加载xml布局后回调
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setOrientation( LinearLayout.VERTICAL);
        mTextView = (TextView) this.getChildAt(0);
        mButton = (ImageButton) this.getChildAt(1);
        ViewGroup.LayoutParams params = mButton.getLayoutParams();
        params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
        params.height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
        mButton.setLayoutParams(params);
        //默认隐藏
        mTextView.setVisibility(GONE);
        mButton.setVisibility(GONE);
        mButton.setOnClickListener(this);
        mButton.setBackgroundResource(isPull ? R.drawable.up : R.drawable.pull);
    }

    @Override
    public void onClick(View view) {
        if(isAnimator){
            return;
        }
        if(isPull){
            startAnimator(mTextView, mTextViewPullHeight, mTextViewNotPullHeight);
        } else {
            startAnimator(mTextView, mTextViewNotPullHeight, mTextViewPullHeight);
        }
        isPull = !isPull ;
        mButton.setBackgroundResource(isPull ? R.drawable.up : R.drawable.pull);
        //下拉，或者上拉的时候的回调
        if(this.mOnTextViewPullListener != null){
            this.mOnTextViewPullListener.textViewPull(mTextView, isPull);
        }
    }

    /**
     * 开始动画
     */
    private void startAnimator(final TextView view, int startHeight, int endHeight){
        ValueAnimator valueAnimator = ValueAnimator.ofInt(startHeight , endHeight ).setDuration(500);
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimator = false ;
            }
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.height = animatedValue;
                //这句，让TextView文本的高度随TextView高度进行变化
                view.setMaxHeight(animatedValue);
                view.setLayoutParams(params);
            }
        });
        isAnimator = true ;
        valueAnimator.start();
    }

    /** TextView展开回调 */
    public interface OnTextViewPullListener{
        void textViewPull(TextView textView, boolean isPull) ;
    }

    public void setOnTextViewPullListener(OnTextViewPullListener listener){
        this.mOnTextViewPullListener = listener ;
    }
}

