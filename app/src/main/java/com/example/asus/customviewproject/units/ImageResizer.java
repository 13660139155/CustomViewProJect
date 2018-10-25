package com.example.asus.customviewproject.units;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 一个图片压缩工具类
 * Create by 陈健宇 at 2018/8/23
 */
public class ImageResizer {
    private static final String TAG = "ImageResizer";

    /**
     * 从资源文件解析图片
     * @param resources 资源文件
     * @param resId 资源文件id
     * @param reWidth 图片希望的宽
     * @param reHeight 图片希望的高
     * @return 返回压缩后的图片
     */
    public static Bitmap decodeBitmapFromResources(Resources resources, int resId, int reWidth, int reHeight){
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, resId, options);
        //计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reWidth, reHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(resources, resId, options);
    }

    /**
     * 从文件加载图片
     * @param pathName 文件路径
     */
    public static Bitmap decodeSampledBitmapFromFile(String pathName, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }

    /**
     * 从文件输入流解析图片
     * @param fileInputStream 文件输入流
     * @throws IOException
     */
    public static Bitmap decodeBitmapFromFileInputStream(FileInputStream fileInputStream, int reWidth, int reHeight) throws IOException {
        return decodeBitmapFromFileDescriptor(fileInputStream.getFD(), reWidth, reHeight);
    }

    /**
     * 通过文件描述符加载图片
     * @param fileDescriptor 文件描述符
     */
    public static Bitmap decodeBitmapFromFileDescriptor(FileDescriptor fileDescriptor, int reWidth, int reHeight){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
        options.inSampleSize = calculateInSampleSize(options, reWidth, reHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
    }

    /**
     * 从文件输入流解析图片，不要求宽高
     * @param fileInputStream 文件输入流
     * @throws IOException
     */
    public static Bitmap decodeBitmapFromFileInputStream(FileInputStream fileInputStream) throws IOException {
        return decodeBitmapFromFileDescriptor(fileInputStream.getFD());
    }

    /**
     * 通过文件描述符加载图片，不要求宽高
     * @param fileDescriptor 文件描述符
     */
    public static Bitmap decodeBitmapFromFileDescriptor(FileDescriptor fileDescriptor){
        return BitmapFactory.decodeFileDescriptor(fileDescriptor);
    }

    /**
     * 计算图片的inSampleSize
     * @param options BitmapFactory.Options
     * @param reWidth 希望图片的宽
     * @param reHeight 希望图片的高
     * @return 计算后的inSampleSize
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reWidth, int reHeight){
        if(reHeight == 0 || reHeight == 0){
            return 1;
        }
        //得到原始宽高
        final int originalWidth = options.outWidth;
        final int originalHeight = options.outHeight;
        Log.d(TAG, "图片原始大小，originalWidth: " + originalWidth + " originalHeight: " + originalHeight);
        int inSampleSize = 1;//大于1图片才有缩放效果
        if(originalHeight > reHeight || originalWidth > reWidth){
            final int halfHeight = originalHeight / 2;
            final int halfWidth = originalWidth / 2;
            //计算出最大inSampleSize保证originalWidth和originalHeight都不会小于reWidth和reHeight
            while (halfWidth / inSampleSize >= reWidth && halfHeight / inSampleSize >= reHeight){
                inSampleSize *= 2;
            }
            Log.d(TAG, "计算后的inSampleSize：" + inSampleSize);
        }
        return inSampleSize;
    }

}
