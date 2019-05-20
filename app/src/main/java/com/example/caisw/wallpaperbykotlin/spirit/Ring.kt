package com.example.caisw.wallpaperbykotlin.spirit

import android.graphics.*
import android.os.SystemClock
import com.example.caisw.wallpaperbykotlin.R
import com.example.caisw.wallpaperbykotlin.app.MyApplication
import com.example.caisw.wallpaperbykotlin.utils.ScreenInfo

/**
 * Created by caisw on 2018/3/12.
 */
class Ring : BaseSpirit {

    private val frameUpdateTime = 50L
    private val ringW = 75//圆环图元宽度
    private val ringH = 75//圆环图元高度
    private val ringArr: Bitmap//圆环图组
    private var ring: Bitmap//当前要绘制的圆环
    private val ringMatrix = Matrix()//圆环绘制变换矩阵
    private val intArr = IntArray(75 * 75)//圆环颜色存储
    private var createTime: Long

    constructor() : super() {
        ringArr = BitmapFactory.decodeResource(MyApplication.instance.resources, R.drawable.ring)
        ring = Bitmap.createBitmap(ringW, ringH, Bitmap.Config.ARGB_8888)
        createTime = SystemClock.uptimeMillis()
    }

    override fun drawMySelf(canvas: Canvas) {
        val scaleX = ScreenInfo.WIDTH * 1F / ringW
        val scaleY = ScreenInfo.HEIGHT * 1F / ringH

        val scale = Math.min(scaleX, scaleY)
        ringMatrix.reset()
        ringMatrix.postScale(scale, scale)
        ringMatrix.postTranslate((ScreenInfo.WIDTH - scale * ringW) / 2, (ScreenInfo.HEIGHT - scale * ringH) / 2)
        canvas.save()
        canvas.drawFilter = PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(getCurrDrawableBitmap(), ringMatrix, null)
        canvas.restore()
    }

    private fun getCurrDrawableBitmap(): Bitmap {
        //6x8
        val pictureIndex = ((SystemClock.uptimeMillis() - createTime) / frameUpdateTime).toInt() % 48
        val x = pictureIndex % 6
        val y = pictureIndex / 6
        ringArr.getPixels(intArr, 0, ringW, x * ringW, y * ringH, ringW, ringH)
        ring.setPixels(intArr, 0, ringW, 0, 0, ringW, ringH)
        return ring
    }
}