package com.example.caisw.wallpaperbykotlin.core.spirit.wallpaper

import android.animation.ValueAnimator
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import com.example.caisw.wallpaperbykotlin.R
import com.example.caisw.wallpaperbykotlin.app.MyApplication
import com.example.caisw.wallpaperbykotlin.core.spirit.SpiritGroup
import com.example.caisw.wallpaperbykotlin.utils.MatrixUtils
import com.example.caisw.wallpaperbykotlin.utils.ScreenInfo

class Wallpaper : SpiritGroup() {
    private val bitmapMatrix = Matrix()
    private val bitmap: Bitmap
    private val bitmapRectF: RectF//图片的大小
    private val visibleRectF: RectF//可视区域
    private val pictureRectF: RectF//图片在场景中的位置
    private var offsetX = 0f//画布偏移量x
    private var offsetY = 0f//画布偏移量y
    private val scaledTouchSlop: Float

    init {
        bitmap = BitmapFactory.decodeResource(MyApplication.instance.resources, R.drawable.waiting)
        bitmapRectF = RectF(0F, 0F, bitmap.width.toFloat(), bitmap.height.toFloat())
        visibleRectF = RectF(0f, 0f, ScreenInfo.WIDTH.toFloat(), ScreenInfo.HEIGHT.toFloat())
        pictureRectF = RectF()
        scaledTouchSlop = ViewConfiguration.get(MyApplication.instance).scaledTouchSlop.toFloat()
    }

    override fun draw(canvas: Canvas) {
        canvas.save()
        super.draw(canvas)
        canvas.translate(offsetX, offsetY)
        canvas.drawBitmap(bitmap, bitmapMatrix, null)
        canvas.restore()
    }

    override fun drawBounds(canvas: Canvas) {
        canvas.save()
        super.drawBounds(canvas)
        canvas.restore()
    }

    fun onTouchEvent(event: MotionEvent) {
        //根据触摸点位置做图片动画
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                toNewTouchPoint(event.x, event.y);
                lastX = event.x
            }
            MotionEvent.ACTION_MOVE -> {
                toNewTouchPoint(event.x, event.y);
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                lastX = -scaledTouchSlop
                lastY = -scaledTouchSlop
                animationToCenter()
            }
        }
    }

    private fun animationToCenter() {
        stopAnimation()
        if (!visibleRectF.isEmpty && !pictureRectF.isEmpty) {
            touchAnimator = MyTranslateAnimator(offsetX, offsetY, 0f, 0f)
            touchAnimator?.start()
        }
    }

    private var lastX = 0f
    private var lastY = 0f
    private fun toNewTouchPoint(x: Float, y: Float) {
        //即使手指不动还是会不停触发move事件，导致不停的创建新动画，这里要判断距离上个点已经发生滑动
//        if (Math.abs(x - lastX) >= scaledTouchSlop || Math.abs(y - lastY) >= scaledTouchSlop) {
            lastX = x;
            lastY = y;
            Log.e("toNewTouchPoint", "x = $x,y = $y")
            stopAnimation()
            if (!visibleRectF.isEmpty && !pictureRectF.isEmpty) {
                //根据比例计算可视区域的触摸位置对应图片哪个点
                val xPercent = x / visibleRectF.width()
                val yPercent = y / visibleRectF.height()
                startAnimation(
                        offsetX,
                        offsetY,
                        x - (pictureRectF.left + pictureRectF.width() * xPercent),
                        y - (pictureRectF.top + pictureRectF.height() * yPercent)
                )
            }
//        }
    }

    private var touchAnimator: MyTranslateAnimator? = null

    private fun startAnimation(startX: Float, startY: Float, endX: Float, endY: Float) {
        Log.e("startAnimation", "startX = $startX,startY = $startY,endX = $endX,endY = $endY")
        touchAnimator = MyTranslateAnimator(startX, startY, endX, endY)
        touchAnimator?.start()
    }

    private fun stopAnimation() {
        //如果动画未结束 更新到当前动画位置
        touchAnimator?.let {
            if (it.isRunning) {
                it.currentPlayTime = it.currentPlayTime
            }
        }
        //取消动画
        touchAnimator?.cancel()
    }

    fun setVisibleRectF(l: Float, t: Float, r: Float, b: Float) {
        visibleRectF.set(l, t, r, b)
        bitmapMatrix.reset()
        //进行矩阵变换，使图片呈现居中裁减状态
        MatrixUtils.setRectToRect(
                bitmapMatrix,
                bitmapRectF,
                visibleRectF,
                ImageView.ScaleType.CENTER_CROP
        )
        //再放大1.1倍
        bitmapMatrix.postScale(1.1f, 1.1f, visibleRectF.centerX(), visibleRectF.centerY())
        //获取图片变换后的实际区域
        bitmapMatrix.mapRect(pictureRectF, bitmapRectF)
    }

    private inner class MyTranslateAnimator(val startX: Float, val startY: Float, val endX: Float, val endY: Float) : ValueAnimator(), ValueAnimator.AnimatorUpdateListener {

        init {
            setFloatValues(0f, 1.0f)
            interpolator = AccelerateInterpolator()
            //平滑动画，一屏幕的距离300毫秒
            duration = (Math.sqrt(Math.pow(endX - startX + 0.0, 2.0) + Math.pow(endY - startY + 0.0, 2.0)) / ScreenInfo.WIDTH * 300).toLong()
            addUpdateListener(this)
            animatedFraction
        }

        override fun onAnimationUpdate(animation: ValueAnimator?) {
            animation?.let {
                val progress = it.animatedValue as Float
                offsetX = startX + (endX - startX) * progress
                offsetY = startY + (endY - startY) * progress
                Log.e("onAnimationUpdate", "offsetX = $offsetX,offsetY = $offsetY,progress = $progress");
            }
        }

    }


}