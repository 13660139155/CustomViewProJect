package com.example.asus.customviewproject.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;


/**
 * 正方形的ImageVIew
 * Create by 陈健宇 at 2018/8/26
 */
public class SquareImageView extends AppCompatImageView {

    public SquareImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
