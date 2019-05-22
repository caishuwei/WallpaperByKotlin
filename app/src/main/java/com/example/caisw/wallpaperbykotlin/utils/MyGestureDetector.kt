package com.example.caisw.wallpaperbykotlin.utils

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ViewConfiguration
import com.example.caisw.wallpaperbykotlin.app.MyApplication


/**
 * 手势解析
 */
open class MyGestureDetector : GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, ScaleGestureDetector.OnScaleGestureListener {

    private var gestureDetector = GestureDetector(MyApplication.instance, this)
    private var scaleGestureDetector: ScaleGestureDetector? = null
    private var downX: Float = 0F
    private var downY: Float = 0F
    private var gestureHandler: Any? = null
    private val scaledTouchSlop = ViewConfiguration.get(MyApplication.instance).scaledTouchSlop

    /**
     * 处理触摸手势
     */
    fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = ev.rawX
                downY = ev.rawY
                //滑动或者多次点击的手势使用一个实例即可，这样才能处理多次点击
                gestureDetector.onTouchEvent(ev)
                //缩放手势最好还是每次都重新new一个，因为上次手势的数据残留好似会影响本次的判断
                scaleGestureDetector = ScaleGestureDetector(MyApplication.instance, this)
                scaleGestureDetector?.onTouchEvent(ev)
            }
            MotionEvent.ACTION_MOVE -> {
                if (gestureHandler == null) {
                    val dx = Math.abs(ev.rawX - downX)
                    val dy = Math.abs(ev.rawY - downY)
                    if (dx > scaledTouchSlop || dy > scaledTouchSlop) {
                        //用户手指滑动
                        if (ev.pointerCount == 1) {//此时只有一根手指
                            gestureDetector.onTouchEvent(ev)
                            gestureHandler = gestureDetector
                        } else {
                            scaleGestureDetector?.onTouchEvent(ev)
                            gestureHandler = scaleGestureDetector
                        }
                    }
                } else {
                    if (gestureHandler == gestureDetector) {
                        gestureDetector.onTouchEvent(ev)
                    } else if (gestureHandler == scaleGestureDetector) {
                        scaleGestureDetector?.onTouchEvent(ev)
                    }
                }
            }
            else -> {
                if (gestureHandler == gestureDetector) {
                    gestureDetector.onTouchEvent(ev)
                } else if (gestureHandler == scaleGestureDetector) {
                    scaleGestureDetector?.onTouchEvent(ev)
                }
            }
        }
        if (ev.action == MotionEvent.ACTION_UP
                || ev.action == MotionEvent.ACTION_CANCEL) {
            if (gestureHandler == null) {
                //本次事件无任何滑动手势处理
                //将抬起事件传递给点击处理
                gestureDetector.onTouchEvent(ev)
            }
            gestureHandler = null
            scaleGestureDetector = null
        }
        return true
    }

    // GestureDetector.OnDoubleTapListener----------------------------------------------------------
    override fun onDoubleTap(e: MotionEvent?): Boolean {
        return false
    }

    override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
        return false
    }

    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        return false
    }

    // GestureDetector.OnGestureListener------------------------------------------------------------
    override fun onShowPress(e: MotionEvent?) {
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return true
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        return false
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        return true
    }

    override fun onLongPress(e: MotionEvent?) {
    }

    // ScaleGestureDetector.OnScaleGestureListener--------------------------------------------------
    override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector?) {

    }

    override fun onScale(detector: ScaleGestureDetector?): Boolean {
        return true
    }


}