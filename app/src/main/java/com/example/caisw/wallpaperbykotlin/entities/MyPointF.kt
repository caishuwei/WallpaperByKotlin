package com.example.caisw.wallpaperbykotlin.entities

import android.graphics.PointF
import android.os.SystemClock


/**
 * Created by caisw on 2018/3/15.
 */
class MyPointF : PointF {
    val createTime: Long

    constructor(x: Float, y: Float) : super(x, y) {
        createTime = SystemClock.elapsedRealtime();
    }

    /**
     * 与另一个点的中心点
     * @param p 另一个点
     * @return 中心点坐标
     */
    fun getCenterWithOtherPoi(p: PointF): MyPointF {
        return MyPointF((p.x + this.x) / 2, (p.y + this.y) / 2)
    }

    /**
     * 获取以此点为起点，与另一个点组成的向量
     * @param endPoint 结束点
     * @param resultContainer 向量容器
     */
    fun getVectorWithOtherPoi(endPoint: PointF, resultContainer: PointF) {
        resultContainer.x = (endPoint.x - x)
        resultContainer.y = (endPoint.y - y)
    }

    fun isOverTime(limitTime: Long): Boolean {
        return createTime < limitTime
    }

}