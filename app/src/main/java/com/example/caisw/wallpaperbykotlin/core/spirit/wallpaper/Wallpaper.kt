package com.example.caisw.wallpaperbykotlin.core.spirit.wallpaper

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
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

    init {
        bitmap = BitmapFactory.decodeResource(MyApplication.instance.resources, R.drawable.waiting)
        bitmapRectF = RectF(0F, 0F, bitmap.width.toFloat(), bitmap.height.toFloat())
        visibleRectF = RectF(0f, 0f, ScreenInfo.WIDTH.toFloat(), ScreenInfo.HEIGHT.toFloat())
        pictureRectF = RectF()
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
            }
            MotionEvent.ACTION_MOVE -> {

            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {

            }
        }
    }

    private fun toNewTouchPoint(x: Float, y: Float) {
        //根据比例计算可视区域的触摸位置对应图片哪个点
        cancelAnimation()
        if (!visibleRectF.isEmpty && !pictureRectF.isEmpty) {
            val xPercent = x / visibleRectF.width()
            val yPercent = y / visibleRectF.height()
            startAnimation(
                    pictureRectF.left + pictureRectF.width() * xPercent,
                    pictureRectF.top + pictureRectF.height() * yPercent,
                    x,
                    y
            )
        }
    }

    private var touchAnimation: Animator? = null

    private fun startAnimation(startX: Float, startY: Float, endX: Float, endY: Float) {
        touchAnimation = MyTranslateAnimation(startX, endX, startY, endY)
        touchAnimation?.start()
    }

    private fun cancelAnimation() {
        touchAnimation?.cancel()
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
        //获取图片变换后的实际区域
        bitmapMatrix.mapRect(pictureRectF, bitmapRectF)
    }

    private inner class MyTranslateAnimation(val startX: Float, val startY: Float, val endX: Float, val endY: Float) : ValueAnimator(), ValueAnimator.AnimatorUpdateListener {
        init {
            setFloatValues(0f, 1.0f)
            interpolator = AccelerateInterpolator()
            duration = 300
            addUpdateListener(this)
        }

        override fun onAnimationUpdate(animation: ValueAnimator?) {
            animation?.let {
                val progress = it.animatedValue as Float
                offsetX = (endX - startX) * progress
                offsetY = (endY - startY) * progress
                Log.e("onAnimationUpdate", "onAnimationUpdate");
            }
        }

    }


}