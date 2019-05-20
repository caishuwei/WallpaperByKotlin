package com.example.caisw.wallpaperbykotlin.spirit

import android.graphics.*
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.os.SystemClock
import com.example.caisw.wallpaperbykotlin.entities.MyPointF
import java.util.*


/**
 * Created by caisw on 2018/3/14.
 */
class TouchLine : BaseSpirit, Handler.Callback {

    companion object {
        const val ADD_POINT = 1
        const val CLEAR_POINT = 2
        const val UPDATE_PATH = 3
        const val POINT_LIFE = 500L
        const val DEFAULT_COMET_WIDTH = 50F
        var SIN_HALF_PI = Math.sin(Math.PI / 2).toFloat()
        var SIN_HALF_PI_ = Math.sin(-Math.PI / 2).toFloat()
        var COS_HALF_PI = Math.cos(Math.PI / 2).toFloat()
        var COS_HALF_PI_ = Math.cos(-Math.PI / 2).toFloat()
    }

    private val paint: Paint
    private var path: Path

    private val handlerThreadHandler: Handler
    private val pointList = LinkedList<MyPointF>()

    private val handlerThread: HandlerThread

    private val pathRectF: RectF

    constructor() : super() {
        paint = Paint()
        paint.strokeWidth = 10F
        paint.color = Color.parseColor("#ff87cefa")
        paint.maskFilter = BlurMaskFilter(DEFAULT_COMET_WIDTH / 2, BlurMaskFilter.Blur.INNER)
        paint.strokeCap = Paint.Cap.ROUND
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true

        path = Path()
        pathRectF = RectF()
        handlerThread = HandlerThread("TouchLine_${this.hashCode()}");
        handlerThread.start()
        handlerThreadHandler = Handler(handlerThread.looper, this)
    }

    override fun drawMySelf(canvas: Canvas) {
        if (!path.isEmpty) {
            canvas.drawPath(path, paint)
            boundsRect.set(pathRectF)
        }
    }

    fun clearPoints() {
        handlerThreadHandler.sendEmptyMessage(CLEAR_POINT)
    }

    fun addTouchPoint(point: MyPointF) {
        handlerThreadHandler.obtainMessage(ADD_POINT, point).sendToTarget()
    }

    override fun handleMessage(msg: Message?): Boolean {
        when (msg?.what) {
            ADD_POINT -> {
                val point = msg.obj
                if (point != null && point is MyPointF) {
                    val lastPoint = pointList.peekLast()
                    if (lastPoint != null && !checkDistance(point, lastPoint)) {
                        return true
                    }
                    pointList.offerLast(point)
                    if (handlerThreadHandler.hasMessages(UPDATE_PATH)) {
                        handlerThreadHandler.removeMessages(UPDATE_PATH)
                    }
                    handlerThreadHandler.sendEmptyMessage(UPDATE_PATH)
                }
                return true
            }
            CLEAR_POINT -> {
                path.reset()
                pointList.clear()
                if (handlerThreadHandler.hasMessages(UPDATE_PATH)) {
                    handlerThreadHandler.removeMessages(UPDATE_PATH)
                }
                handlerThreadHandler.sendEmptyMessage(UPDATE_PATH)
                return true
            }
            UPDATE_PATH -> {
                updatePath()
                if (!pointList.isEmpty() && !handlerThreadHandler.hasMessages(UPDATE_PATH)) {
                    handlerThreadHandler.sendEmptyMessageDelayed(UPDATE_PATH, 30)
                }
                return true
            }
        }
        return false
    }


    private fun updatePath() {
        trimPointList(SystemClock.elapsedRealtime() - POINT_LIFE)
        //1、取得中心路径点
        var centerPois = pointList.toList()
        val size = centerPois.size
        if (centerPois.size < 4) {//小于4个点不用计算直接返回，构不成彗星
            path.reset()
            boundsRect.set(0F, 0F, 0F, 0F)
            return
        }
        val leftPois = mutableListOf<MyPointF>()//左侧路径
        val rightPois = mutableListOf<MyPointF>()//右侧路径
        leftPois.add(centerPois[0])//第一个点不做偏移
        rightPois.add(centerPois[0])
        //其他点偏移
        val vectorPoi = PointF(0f, 0f)
        var currWidth: Float
        var rate: Float
        for (i in 1 until centerPois.size - 1) {
            //取得前后两个点组成的向量
            centerPois[i - 1].getVectorWithOtherPoi(centerPois[i + 1], vectorPoi)
            //向量模缩放，方向维持不变
            rate = 1.0f * i / (size - 1)
            currWidth = DEFAULT_COMET_WIDTH * (rate * rate)
            scaleVector(vectorPoi, currWidth)
            //向量左右旋转90度得到左右路劲的点
            leftPois.add(MyPointF(centerPois[i].x + COS_HALF_PI_ * vectorPoi.x - SIN_HALF_PI_ * vectorPoi.y, centerPois[i].y + SIN_HALF_PI_ * vectorPoi.x + COS_HALF_PI_ * vectorPoi.y))
            rightPois.add(MyPointF(centerPois[i].x + COS_HALF_PI * vectorPoi.x - SIN_HALF_PI * vectorPoi.y, centerPois[i].y + SIN_HALF_PI * vectorPoi.x + COS_HALF_PI * vectorPoi.y))
        }
        //设置起点
        val startPoi = centerPois[0].getCenterWithOtherPoi(centerPois[1])
        //添加左路劲
        val cometPath = Path()
        cometPath.reset()
        cometPath.moveTo(startPoi.x, startPoi.y)
        var centerPoi: MyPointF? = null
        for (i in 1 until leftPois.size - 1) {
            centerPoi = leftPois[i].getCenterWithOtherPoi(leftPois[i + 1])
            cometPath.quadTo(leftPois[i].x, leftPois[i].y, centerPoi.x, centerPoi.y)
        }
        if (centerPoi == null) {
            return
        }
        //圆形头部
        val pL = centerPoi
        val pR = rightPois[rightPois.size - 1].getCenterWithOtherPoi(rightPois[rightPois.size - 2])
        val headCenter = centerPoi.getCenterWithOtherPoi(pR)
        cometPath.quadTo(pL.x + vectorPoi.x, pL.y + vectorPoi.y, headCenter.x + vectorPoi.x, headCenter.y + vectorPoi.y)
        //添加右路径
        for (i in rightPois.size - 1 downTo 2) {
            centerPoi = rightPois[i - 1].getCenterWithOtherPoi(rightPois[i])
            if (i == rightPois.size - 1) {//反向遍历第一个点(完成圆形头部的另一半)
                cometPath.quadTo(pR.x + vectorPoi.x, pR.y + vectorPoi.y, centerPoi.x, centerPoi.y)
            } else if (i == 2) {//反向遍历最后一个点
                cometPath.quadTo(startPoi.x, startPoi.y, centerPoi.x, centerPoi.y)
            } else {
                cometPath.quadTo(rightPois[i].x, rightPois[i].y, centerPoi.x, centerPoi.y)
            }
        }
        cometPath.close()
        cometPath.computeBounds(pathRectF, false)
        pathRectF.inset(-5F, -5F)
        path = cometPath
    }

    private fun scaleVector(vectorPoi: PointF, currWidth: Float) {
        val scale = currWidth / Math.sqrt(vectorPoi.x * vectorPoi.x + vectorPoi.y * vectorPoi.y + 0.0).toFloat()
        vectorPoi.x = vectorPoi.x * scale
        vectorPoi.y = vectorPoi.y * scale
    }


    private fun trimPointList(time: Long) {
        if (!pointList.isEmpty() && pointList.peekFirst().isOverTime(time)) {
            pointList.pollFirst()
            trimPointList(time)
        }
    }

    private fun checkDistance(point: MyPointF, lastPoint: MyPointF): Boolean {
        return Math.max(Math.abs(point.x - lastPoint.x), Math.abs(point.y - lastPoint.y)) > 20
    }

    override fun release() {
        super.release()
        handlerThread.quit()
    }
}