package com.example.asus.customviewproject.customView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;

import java.util.ArrayList;


/**
 * 可插入图片的EditText
 * Create by 陈健宇 at 2018/7/23
 */
public class SpanEditText extends androidx.appcompat.widget.AppCompatEditText {

    private final String TAG = "rain";
    private String tag = "☆";
    private float oldY;

    public SpanEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void insertDrawable(Bitmap bitmap, String imagePath) {

        String path = tag + imagePath + tag;

        //光标的位置
        Editable edit_text = getEditableText();
        int start = getSelectionStart();

        //插入换行符，使图片单独占一行
        SpannableString newLine = new SpannableString("\n\n");
        edit_text.insert(start, newLine);

        final SpannableString s = new SpannableString(path);
        //得到drawable对象，即所要插入的图片
        Drawable drawable = new BitmapDrawable(bitmap);
        drawable.setBounds(200, 0, drawable.getIntrinsicWidth() + 200, drawable.getIntrinsicHeight());
        //用这个drawable对象代替字符串imagepath
        ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
        //当我们复制这个图片的时候，实际是复制了imagepath这个字符串。
        s.setSpan(span, 0, path.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 将选择的图片追加到EditText中光标所在位置
        if (start < 0 || start >= edit_text.length()) {
            edit_text.append(s);
        } else {
            edit_text.insert(start, s);
        }

        //插入图片后换行
        edit_text.insert(start, newLine);

        Log.d(TAG, "insertDrawable:  " + getText());
    }

    public String getEditContent(){
        return getText().toString();
    }

    public ArrayList<String> getEditContentList(String content){
        ArrayList<String> arrayList = new ArrayList<>();
        if(content.length() != 0 && content.contains(tag)){
            String[] strings = content.split(tag);
            for(String s : strings){
                arrayList.add(s);
            }
        }else {
            arrayList.add(content);
        }
        return arrayList;
    }

    /**
     * 获取屏幕宽度
     * @return 屏幕宽度
     */
    private int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return windowManager.getDefaultDisplay().getWidth();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                oldY = event.getY();
                requestFocus();
                break;
            case MotionEvent.ACTION_MOVE:
                float newY = event.getY();
                if (Math.abs(oldY - newY) > 20) {
                    clearFocus();
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }
}
