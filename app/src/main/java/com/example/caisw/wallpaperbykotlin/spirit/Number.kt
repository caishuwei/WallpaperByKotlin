package com.example.caisw.wallpaperbykotlin.spirit

import android.graphics.*
import com.example.caisw.wallpaperbykotlin.app.MyApplication
import com.example.caisw.wallpaperbykotlin.utils.ScreenInfo


/**
 * Created by caisw on 2018/3/13.
 */
class Number : BaseSpirit {
    private val pointData: Array<MutableList<Point>>
    private val pathData: Array<Path>
    private val paint: Paint

    constructor(pointData: Array<MutableList<Point>>) : super() {
        this.pointData = pointData
        pathData = Array(pointData.size, { x ->
            val path = Path()
            path.reset()
            path.moveTo(pointData[x][0].x.toFloat(), pointData[x][0].y.toFloat())
            for (y in 0 until pointData[x].size) {
                path.lineTo(pointData[x][y].x.toFloat(), pointData[x][y].y.toFloat())
            }
            path.close()
            path
        })
        paint = Paint()
        paint.color = Color.WHITE
//        paint.setMaskFilter(BlurMaskFilter(0.1F, BlurMaskFilter.Blur.INNER))
        paint.setStrokeCap(Paint.Cap.SQUARE)
        paint.setStyle(Paint.Style.FILL)
        paint.setAntiAlias(true)
    }


    override fun drawMySelf(canvas: Canvas) {
        canvas.save()
        canvas.translate(ScreenInfo.WIDTH / 2.toFloat(), ScreenInfo.HEIGHT / 2.toFloat())
        canvas.scale(100F, 100F)
        for (path in pathData) {
            canvas.drawPath(path, paint)
        }
        canvas.restore()
    }


}