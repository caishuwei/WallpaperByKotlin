package com.example.caisw.wallpaperbykotlin.core.spirit.search

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.os.Handler
import android.os.Looper
import com.example.caisw.wallpaperbykotlin.core.spirit.Spirit
import com.example.caisw.wallpaperbykotlin.utils.ScreenInfo

open class SearchBase(val count: Int) : Spirit() {

    companion object {
        val COLOR_START = Color.parseColor("#ffff0000")//起点颜色
        val COLOR_END = Color.parseColor("#ffff8000")//终点颜色
        val COLOR_PATH = Color.RED//路径颜色
        val COLOR_WAITING_CHECK = Color.GRAY//路径颜色
        val COLOR_OBSTACLE = Color.parseColor("#ff87cefa")//障碍物颜色

        const val FLAG_DEFAULT = 0//空白点的状态值
        const val FLAG_OBSTACLE = -1//障碍物
        const val FLAG_START = -2//开始位置
        const val FLAG_END = -3//结束位置
        const val FLAG_WAITING_CHECK = -4//等待检查的点
    }

    val unit = ScreenInfo.WIDTH * 1F / count
    private val mapPaint = Paint()
    val map = Array(count) { IntArray(count) }
    val commonPaint = Paint()

    private val mainHandler = Handler(Looper.getMainLooper())
    private val updateTask = UpdateTask()


    var startPoint = Point()
    var endPoint = Point()

    init {
        mapPaint.strokeCap = Paint.Cap.SQUARE
        mapPaint.strokeWidth = 2F
        mapPaint.style = Paint.Style.FILL
        mapPaint.color = Color.WHITE
        commonPaint.strokeCap = Paint.Cap.SQUARE
        commonPaint.strokeWidth = 1F
        commonPaint.style = Paint.Style.FILL
        commonPaint.color = Color.WHITE
        randomStart()
        randomEnd()
    }

    /**
     * 随机一个结束位置
     */
    private fun randomEnd() {
        val x = (Math.random() * count).toInt()
        val y = (Math.random() * count).toInt()
        if (map[x][y] == 0) {
            map[x][y] = FLAG_END
            endPoint = Point(x, y)
        } else {
            randomEnd()
        }
    }

    /**
     * 随机一个开始位置
     */
    private fun randomStart() {
        val x = (Math.random() * count).toInt()
        val y = (Math.random() * count).toInt()
        if (map[x][y] == 0) {
            map[x][y] = FLAG_START
            startPoint = Point(x, y)
        } else {
            randomStart()
        }
    }


    /**
     * 计算下一步
     */
    open fun nextStep(): Boolean {
        return false
    }

    /**
     * 开始检索
     */
    open fun start() {
        mainHandler.removeCallbacks(updateTask)
        mainHandler.post(updateTask)
    }

    /**
     * 结束本次检索
     */
    open fun end() {
        mainHandler.removeCallbacks(updateTask)
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

    private inner class UpdateTask : Runnable {
        override fun run() {
            if (nextStep()) {
                //每隔10毫秒执行下一步
                mainHandler.postDelayed(this@UpdateTask, 5)
            }
        }
    }
}