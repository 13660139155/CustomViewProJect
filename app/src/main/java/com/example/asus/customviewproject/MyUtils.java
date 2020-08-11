package com.example.asus.customviewproject;

import android.content.Context;

/**
 * @author chenjianyu
 * @date 2020/8/11
 */
public class MyUtils {

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context){
        int height = 0;
        int resourceId = context.getResources().getIdentifier(
                "status_bar_height",
                "dimen",
                "android"
        );
        if (resourceId > 0) {
            height = context.getResources().getDimensionPixelSize(resourceId);
        }
        return height;
    }

}
