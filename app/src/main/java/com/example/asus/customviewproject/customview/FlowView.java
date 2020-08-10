package com.example.asus.customviewproject.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.example.asus.customviewproject.MyConstant;


/**
 * 自适应的热门标签布局
 * Create by 陈健宇 at 2018/8/13
 */
public class FlowView extends ViewGroup {

    private final String TAG = MyConstant.FV_TAG;

    public FlowView(Context context) {
        super(context);
    }

    public FlowView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        int measureHeightMode = MeasureSpec.getMode(heightMeasureSpec);

        int lineWidth = 0;//记录每一行的宽度
        int lineHeight = 0;//记录没一行的高度
        int totalWidth = 0;//每一行累加的宽度
        int totalHegiht = 0;//每一行累加的高度

        int childCount = getChildCount();
        Log.d(TAG, "onMeasure，childCount：" + childCount);
        for(int i = 0; i < childCount; i++){
            View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
            int childWidth = childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = childView.getMeasuredHeight() + lp.bottomMargin + lp.topMargin;
            if(lineWidth + childWidth > measureWidth){//需要换行
                totalWidth = Math.max(childWidth, lineWidth);
                totalHegiht += lineHeight;
                //重置
                lineHeight = childHeight;
                lineWidth = childWidth;

            }else {//不需要换行
                lineWidth += childWidth;
                lineHeight = Math.max(childHeight, lineHeight);
            }

            //把最后一行加上
            if(i == childCount - 1){
                totalHegiht += lineHeight;
                totalWidth = Math.max(childWidth, lineWidth);
            }
        }
        setMeasuredDimension(
                (measureWidthMode == MeasureSpec.EXACTLY) ? measureWidth : totalWidth,
                (measureHeightMode == MeasureSpec.EXACTLY) ? measureHeight : totalHegiht
        );
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        if(b){
            int lineWidth = 0;//记录每一行的宽度
            int lineHeight = 0;//记录没一行的高度
            int left = 0;//左边界
            int top = 0;//上边界
            int childCount = getChildCount();
            for(int index = 0; index < childCount; index++){
                View childView = getChildAt(index );
                if(childView.getVisibility() != GONE){
                    MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
                    int childWidth = childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
                    int childHeight = childView.getMeasuredHeight() + lp.bottomMargin + lp.topMargin;
                    if(lineWidth + childWidth > getMeasuredWidth()){//需要换行
                        top += lineHeight;
                        left = 0;
                        //重置
                        lineHeight = childHeight;
                        lineWidth = childWidth;

                    }else {//不需要换行
                        lineWidth += childWidth;
                        lineHeight = Math.max(childHeight, lineHeight);
                    }

                    //计算childView的left,top,right,bottom
                    int lc = left + lp.leftMargin;
                    int tc = top + lp.topMargin;
                    int rc = lc + childView.getMeasuredWidth();
                    int bc = tc + childView.getMeasuredHeight();
                    childView.layout(lc, tc, rc, bc);

                    //将left置为下一子控件的起始点
                    left += childWidth;
                }
            }

        }

    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
