package com.example.asus.customviewproject.customview

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.annotation.FloatRange
import com.example.asus.customviewproject.MyUtils
import kotlin.math.abs
import kotlin.math.max

/**
 * 可以拖动关闭的ViewGroup
 * @author chenjianyu
 * @date 2020/6/10
 */
class DragViewGroup @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defAttrs: Int = 0) : ViewGroup(context, attrs, defAttrs){

    companion object {
        private const val SLIDING_BOUNDARY = 200F
        private const val MIN_SCALE = 0.4F
        private const val MIN_ALPHA = 0F
        private const val ANIM_TIME = 100L
    }

    private val TAG = DragViewGroup::class.java.simpleName
    private lateinit var childView: View
    private var isDragStarted = false
    private var isDragging = false
    private var isClosing = false
    private var isResetting = false
    private var isLongPress = false
    private var isPress = false
    private var lastX = 0f
    private var lastY = 0f
    private var lastTranslationY = 0f
    private var lastTranslationX = 0f
    private var firstPointerId = 0
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private val longDelayTimeout = ViewConfiguration.getLongPressTimeout().toLong()
    private val longPressRunnable = LongPressRunnable()
    private val statusBarHeight = MyUtils.getStatusBarHeight(context)

    var minScale = MIN_SCALE
        set(@FloatRange(from = 0.0, to = 1.0) value){
            field = value
        }
    var minAlpha = MIN_ALPHA
        set(@FloatRange(from = 0.0, to = 1.0) value) {
            field = value
        }
    var slidingBoundary = SLIDING_BOUNDARY
        set(value) {
            field = value
            if(field < 0){
                field = 0f
            }
        }
    var isUseSharedElement = false
        set(value) {
            field = value && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        }
    var onSingleClickListener: OnClickListener? = null
    var onLongPressListener: OnLongClickListener? = null
    var onDragListener: OnDragListener? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width = 0
        var height = 0
        if(childCount > 0){
            childView = getChildAt(childCount - 1)
            if(childView.visibility != View.GONE){
                measureChildWithMargins(childView, widthMeasureSpec, 0, heightMeasureSpec, 0)
                width = childView.measuredWidth + paddingLeft + paddingRight
                height = childView.measuredHeight + paddingTop + paddingBottom
                val lp = childView.layoutParams
                if(lp is MarginLayoutParams){
                    width += (lp.leftMargin + lp.rightMargin)
                    height += (lp.topMargin + lp.bottomMargin)
                }
            }
        }
        setMeasuredDimension(
                max(width, suggestedMinimumWidth),
                max(height, suggestedMinimumHeight)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if(childCount > 0){
            childView = getChildAt(childCount - 1)
            if(childView.visibility != View.GONE){
                val lp = childView.layoutParams
                var childLeft = paddingLeft
                var childTop = paddingTop
                if(lp is MarginLayoutParams){
                    childLeft += lp.leftMargin
                    childTop += lp.topMargin
                }
                childView.layout(childLeft, childTop, childLeft + childView.measuredWidth, childTop + childView.measuredHeight)
            }
        }
    }

    override fun checkLayoutParams(p: LayoutParams?): Boolean {
        return p is MarginLayoutParams
    }

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
        return MarginLayoutParams(p)
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when(ev.action){
            MotionEvent.ACTION_DOWN -> {
                isPress = true
                postDelayed(longPressRunnable, longDelayTimeout)
            }
            MotionEvent.ACTION_UP -> {
                if(isPress){
                    if(!isLongPress){
                        removeCallbacks(longPressRunnable)
                    }
                    onSingleClickListener?.onClick(this)
                    isPress = false
                }
            }
            else -> {
                isPress = false
                removeCallbacks(longPressRunnable)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return when(ev.action){
            MotionEvent.ACTION_DOWN -> {
                Log.d(TAG, "onInterceptTouchEvent(), down")
                lastX = ev.x
                lastY = ev.y
                false
            }
            else -> {
                Log.d(TAG, "onInterceptTouchEvent(), event = $ev")
                if(ev.pointerCount > 1){
                    false
                }else{
                    onDragListener?.canIntercept(ev) ?: true
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if(event.pointerCount == 1){
            when(event.action){
                MotionEvent.ACTION_MOVE -> {
                    val curX = event.x
                    val curY = event.y
                    val dX = curX - lastX
                    val dY = curY - lastY

                    if(isInInvalidArea()){
                        return true
                    }

                    if(event.getPointerId(0) != firstPointerId){
                        return true
                    }

                    if(!isDragging && !isClosing){
                        isDragging = dY >= touchSlop
                    }

                    if(isDragging){
                        if(isDragStarted){
                            onDragListener?.onDragging(childView)
                        }
                        if(!isDragStarted){
                            isDragStarted = true
                            onDragListener?.onDragStart(childView)
                        }
                        val curTranslationX = dX + lastTranslationX
                        val curTranslationY = dY + lastTranslationY
                        updateChildView(curTranslationX, curTranslationY)
                        lastTranslationX = curTranslationX
                        lastTranslationY = curTranslationY
                    }

                    lastX = curX
                    lastY = curY
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if(lastTranslationY < slidingBoundary){
                        resetChildView()
                    }else{
                        closeChildView()
                    }

                    if(isDragStarted){
                        isDragStarted = false
                        isDragging = false
                        onDragListener?.onDragEng(childView)
                    }

                    Log.d(TAG, "onTouchEvent(), up or cancel, event = $event")
                }
                else -> {
                    Log.d(TAG, "onTouchEvent(), other, event = $event")
                }
            }
        }
        return true
    }

    private fun closeChildView() {
        if(isClosing || lastTranslationY < slidingBoundary){
            return
        }

        isClosing = true
        if(isUseSharedElement){
            if(context is Activity){
                (context as Activity).onBackPressed()
            }
            isClosing = false
        }else{
            val closeAnim = ObjectAnimator.ofFloat(childView, "translationY", lastTranslationY, childView.height.toFloat())
            closeAnim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    if (context is Activity) {
                        (context as Activity).finish()
                    }
                    isClosing = false
                }
            })
            closeAnim.run {
                duration = ANIM_TIME * 3
                interpolator = LinearInterpolator()
                start()
            }
        }
    }

    private fun resetChildView(){
        if(isResetting || lastTranslationY == 0f){
            return
        }

        val resetAnim = ValueAnimator.ofFloat(lastTranslationY, 0f)
        val radio = lastTranslationX / lastTranslationY
        resetAnim.addUpdateListener {
            val curTranslationY = it.animatedValue as Float
            var curTranslationX = radio * curTranslationY
            updateChildView(curTranslationX, curTranslationY)
            lastTranslationX = curTranslationX
            lastTranslationY = curTranslationY
        }
        resetAnim.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationStart(animation: Animator?) {
                isResetting = true
                Log.d(TAG, "onAnimationStart(), reset start")
            }

            override fun onAnimationEnd(animation: Animator?) {
                isResetting = false
                Log.d(TAG, "onAnimationStart(), reset end, $lastTranslationX, $lastTranslationY")
            }
        })
        resetAnim.run {
            duration = ANIM_TIME
            interpolator = LinearInterpolator()
            start()
        }
    }

    private fun updateChildView(tX: Float, tY: Float){
        var percent = 1 - abs(tY / childView.height)
        if (percent > 1) {
            percent = 1f
        } else if (percent < 0) {
            percent = 0f
        }

        childView.apply {
            translationX = tX
            translationY = tY
        }

        if(tY >= 0){
            var changeScale = percent
            if (percent < minScale) {
                changeScale = minScale;
            }
            childView.apply {
                scaleX = changeScale
                scaleY = changeScale
            }

            var changeAlpha = percent * 255
            if(percent < minAlpha){
                changeAlpha = minAlpha * 255
            }
            background.mutate().alpha = changeAlpha.toInt()
        }
    }

    private fun isInInvalidArea(): Boolean {
        return lastY < statusBarHeight
    }

    private inner class LongPressRunnable : Runnable{
        override fun run() {
            if(isPress){
                isPress = !(onLongPressListener?.onLongClick(this@DragViewGroup) ?: true)
                isLongPress = true
                removeCallbacks(this)
            }
        }
    }

    private abstract class AnimatorListenerAdapter : Animator.AnimatorListener{
        override fun onAnimationRepeat(animation: Animator?) {}
        override fun onAnimationEnd(animation: Animator?) {}
        override fun onAnimationCancel(animation: Animator?) {}
        override fun onAnimationStart(animation: Animator?) {}
    }

    abstract class OnDragListener{
        open fun canIntercept(event: MotionEvent?) = true
        open fun onDragStart(view: View){}
        open fun onDragging(view: View){}
        open fun onDragEng(view: View){}
    }
}