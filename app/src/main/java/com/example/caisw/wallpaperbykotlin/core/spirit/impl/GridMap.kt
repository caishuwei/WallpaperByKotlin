package com.example.caisw.wallpaperbykotlin.core.spirit.impl

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.caisw.wallpaperbykotlin.core.spirit.Spirit
import com.example.caisw.wallpaperbykotlin.utils.ScreenInfo

open class GridMap(val count: Int) : Spirit() {

    companion object {
        const val FLAG_OBSTACLE = -1//障碍物
        const val FLAG_START = -2//开始位置
        const val FLAG_END = -3//结束位置
        const val FLAG_WAITING_CHECK = -4//等待检查的点
        const val FLAG_CHECKED = -5//检查过的点
    }

    val unit = ScreenInfo.WIDTH * 1F / count
    private val mapPaint = Paint()
    val map = Array(count) { IntArray(count) }
    val obstaclePaint = Paint()
    val commonPaint = Paint()

    init {
        mapPaint.strokeCap = Paint.Cap.SQUARE
        mapPaint.strokeWidth = 2F
        mapPaint.style = Paint.Style.FILL
        mapPaint.color = Color.WHITE
        obstaclePaint.strokeCap = Paint.Cap.SQUARE
        obstaclePaint.strokeWidth = 1F
        obstaclePaint.style = Paint.Style.FILL
        obstaclePaint.color = Color.parseColor("#ff87cefa")
        commonPaint.strokeCap = Paint.Cap.SQUARE
        commonPaint.strokeWidth = 1F
        commonPaint.style = Paint.Style.FILL
        commonPaint.color = Color.WHITE
    }

    /**
     * 清除地图
     */
    fun clearMap() {
        for (x in 0 until count) {
            for (y in 0 until count) {
                map[x][y] = 0
            }
        }
    }

    /**
     * 添加障碍物
     */
    fun setObstacle(x: Float, y: Float) {
        val posX = (x / unit).toInt()
        val posY = (y / unit).toInt()
        if (posX in 0 until count
                && posY in 0 until count
                && map[posX][posY] == 0) {
            map[posX][posY] = -1
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        //绘制网格
        for (i in 0..count) {
            canvas.drawLine(unit * i, 0F, unit * i, unit * count, mapPaint)
            canvas.drawLine(0F, unit * i, unit * count, unit * i, mapPaint)
        }
    }

    fun drawRect(x: Int, y: Int, canvas: Canvas, paint: Paint) {
        canvas.drawRect(x * unit, y * unit, (x + 1) * unit, (y + 1) * unit, paint)
    }

}