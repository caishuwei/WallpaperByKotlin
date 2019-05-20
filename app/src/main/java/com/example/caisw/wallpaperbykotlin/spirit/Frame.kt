package com.example.caisw.wallpaperbykotlin.spirit

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.SystemClock
import android.util.Log
import com.example.caisw.wallpaperbykotlin.app.MyApplication
import com.example.caisw.wallpaperbykotlin.utils.ScreenInfo

/**
 * Created by caisw on 2018/3/14.
 */
class Frame : BaseSpirit {
    private var rate = 0F
    private var frameCount = 0
    private var timeForStartCount = 0L

    private val textPaint: Paint
    private var displayText = ""
    private var displayTextRect: Rect

    private var marginTop: Int

    constructor() : super() {
        textPaint = Paint()
        textPaint.color = Color.WHITE
        textPaint.isAntiAlias = true
        textPaint.textSize = 25F
        displayTextRect = Rect()
        marginTop = ScreenInfo.dp2px(30F).toInt()
    }

    /**
     * 新的一帧绘制完成<br/>
     *
     * 20帧计算一下帧速，取得平均值
     */
    fun onFrameDrawCompleted() {
        frameCount++
        if (frameCount == 20) {
            frameCount = 0
            val now = SystemClock.elapsedRealtime()
            rate = 1000 / ((now - timeForStartCount) / 20F)
            timeForStartCount = now
            displayText = String.format("fps : %.2f", rate)
            textPaint.getTextBounds(displayText, 0, displayText.length, displayTextRect)
        }
    }


    override fun drawMySelf(canvas: Canvas) {
        canvas.save()
        canvas.drawText(displayText, ScreenInfo.WIDTH * 1F - displayTextRect.width(), displayTextRect.top * -1F + marginTop, textPaint)
        canvas.restore()
        boundsRect.set(displayTextRect)
        boundsRect.offset(ScreenInfo.WIDTH * 1F - displayTextRect.width(), displayTextRect.top * -1F + marginTop)
    }

}