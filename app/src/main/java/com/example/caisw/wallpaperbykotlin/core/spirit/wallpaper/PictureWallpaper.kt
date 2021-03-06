package com.example.caisw.wallpaperbykotlin.core.spirit.wallpaper

import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import com.example.caisw.wallpaperbykotlin.R
import com.example.caisw.wallpaperbykotlin.app.MyApplication
import com.example.caisw.wallpaperbykotlin.core.spirit.SpiritGroup
import com.example.caisw.wallpaperbykotlin.utils.MatrixUtils
import com.example.caisw.wallpaperbykotlin.utils.ScreenInfo

class PictureWallpaper : SpiritGroup() {
    private val bitmapMatrix = Matrix()
    private val bitmap: Bitmap
    private val bitmapRectF = RectF()//图片的大小
    private val visibleRectF: RectF//可视区域
    private val pictureRectF = RectF()//图片在场景中的位置
    private val pictureDisplayRectF = RectF()//图片可以显示的区域
    private var offsetX = 0f//画布偏移量x
    private var offsetY = 0f//画布偏移量y
    private val scaledTouchSlop: Float
    private val animateHelper: AnimateHelper = AnimateHelper()

    private val textPaint: Paint

    init {
        textPaint = Paint()
        textPaint.color = Color.WHITE
        textPaint.isAntiAlias = true
        textPaint.textSize = 25F
        visibleRectF = RectF(0f, 0f, ScreenInfo.WIDTH.toFloat(), ScreenInfo.HEIGHT.toFloat())
        //直接载入图片
        bitmap = BitmapFactory.decodeResource(MyApplication.instance.resources, R.raw.hate)
//        bitmap = readWaiting()
//        bitmap = readKing()

        bitmapRectF.set(0F, 0F, bitmap.width.toFloat(), bitmap.height.toFloat())
        scaledTouchSlop = ViewConfiguration.get(MyApplication.instance).scaledTouchSlop.toFloat()
    }

    private fun readKing(): Bitmap {
        val brd = BitmapRegionDecoder.newInstance(MyApplication.instance.resources.openRawResource(R.raw.king), false)
        val length = Math.min(brd.width, brd.height)
        val rect = Rect(
                0,
                0,
                brd.width,
                Math.min(brd.height, (brd.width / visibleRectF.width() * visibleRectF.height()).toInt())
        )
        return brd.decodeRegion(rect, BitmapFactory.Options())
    }

    private fun readWaiting(): Bitmap {
        //            //加载图片的一部分区域(waiting这张图是比较宽，我想要切出人物的那一块做壁纸)
        val brd = BitmapRegionDecoder.newInstance(MyApplication.instance.resources.openRawResource(R.raw.waiting), false)
        val length = Math.min(brd.width, brd.height)
        val rect = Rect(
                (brd.width - length) / 2,
                (brd.height - length) / 2,
                (brd.width - length) / 2 + length,
                (brd.height - length) / 2 + length
        )
        //再去掉左边的1/4就差不多了
        rect.set(rect.left + length / 4, rect.top, rect.right, rect.bottom)
        return brd.decodeRegion(rect, BitmapFactory.Options())
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        canvas.save()
        animateHelper.updateCurrOffset()
        canvas.translate(offsetX, offsetY)
        canvas.drawBitmap(bitmap, bitmapMatrix, null)
        canvas.restore()
//        canvas.drawText(getInfo(), 50f, 150f, textPaint)
    }

    private fun getInfo(): String {
        val floatArr = FloatArray(9)
        bitmapMatrix.getValues(floatArr);
        return "offsetX = $offsetX,offsetY = $offsetY,scaleX = ${floatArr[Matrix.MSCALE_X]},scaleY = ${floatArr[Matrix.MSCALE_Y]}"
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
                toNewTouchPoint(event.x, event.y)
                lastX = event.x
            }
            MotionEvent.ACTION_MOVE -> {
                toNewTouchPoint(event.x, event.y)
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                lastX = -scaledTouchSlop
                lastY = -scaledTouchSlop
                animationToCenter()
            }
        }
    }

    private fun animationToCenter() {
        if (!visibleRectF.isEmpty && !pictureDisplayRectF.isEmpty) {
            animateHelper.updateCurrOffset()
            animateHelper.startAnimate(offsetX, offsetY, 0f, 0f)
        }
    }

    private var lastX = 0f
    private var lastY = 0f
    private fun toNewTouchPoint(x: Float, y: Float) {
        //即使手指不动还是会不停触发move事件，导致不停的创建新动画，这里要判断距离上个点已经发生滑动
        lastX = x
        lastY = y
        if (!visibleRectF.isEmpty && !pictureDisplayRectF.isEmpty) {
            //根据比例计算可视区域的触摸位置对应图片哪个点
            val xPercent = x / visibleRectF.width()
            val yPercent = y / visibleRectF.height()
            animateHelper.updateCurrOffset()
            animateHelper.startAnimate(
                    offsetX,
                    offsetY,
                    x - (pictureDisplayRectF.left + pictureDisplayRectF.width() * xPercent),
                    y - (pictureDisplayRectF.top + pictureDisplayRectF.height() * yPercent)
            )
        }
    }

    fun setVisibleRectF(l: Float, t: Float, r: Float, b: Float) {
        visibleRectF.set(l, t, r, b)
        Log.i(javaClass.simpleName, visibleRectF.toString());
        bitmapMatrix.reset()
        //进行矩阵变换，使图片呈现居中裁减状态
        MatrixUtils.setRectToRect(
                bitmapMatrix,
                bitmapRectF,
                visibleRectF,
                ImageView.ScaleType.CENTER_CROP
        )
        //再放大1.2倍
        bitmapMatrix.postScale(1.2f, 1.2f, visibleRectF.centerX(), visibleRectF.centerY())
        //获取图片变换后的实际区域
        bitmapMatrix.mapRect(pictureRectF, bitmapRectF)
//        pictureDisplayRectF.set(pictureRectF)
        //设置图片可显示的区域
        val x = pictureRectF.right - visibleRectF.right
        val y = pictureRectF.bottom - visibleRectF.bottom
        val min = Math.min(x, y)
        pictureDisplayRectF.set(
                visibleRectF.left - min,
                visibleRectF.top - min,
                visibleRectF.right + min,
                visibleRectF.bottom + min
        )
    }

    private inner class AnimateHelper {
        private var startTime = 0L
        private var duration = 0L
        private var startX = 0F
        private var startY = 0F
        private var endX = 0F
        private var endY = 0F
        var completed = true
            private set

        fun startAnimate(startX: Float, startY: Float, endX: Float, endY: Float) {
            completed = false
            this.startX = startX
            this.startY = startY
            this.endX = endX
            this.endY = endY
            startTime = System.currentTimeMillis()
            //平滑动画，一屏幕的距离3000毫秒
            duration = (Math.sqrt(Math.pow(endX - startX + 0.0, 2.0) + Math.pow(endY - startY + 0.0, 2.0)) / ScreenInfo.WIDTH * 3000).toLong()
            duration = Math.min(duration, 3000)//动画时间不要超出3秒
            if (duration == 0L) {
                offsetX = endX
                offsetY = endY
                completed = true
            }
        }

        private val interpolator = DecelerateInterpolator()
        fun updateCurrOffset() {
            if (startTime == 0L) {
                return
            }
            if (completed) {
                return
            }
            val currTime = System.currentTimeMillis()
            var progress = (currTime - startTime) * 1f / duration
            progress = Math.min(1f, progress)
            if (progress == 1f) {
                completed = true
            }
            progress = interpolator.getInterpolation(progress)
            offsetX = startX + (endX - startX) * progress
            offsetY = startY + (endY - startY) * progress
        }
    }

}