package com.example.caisw.wallpaperbykotlin.core.spirit.group

import android.graphics.Canvas
import android.graphics.Matrix
import com.example.caisw.wallpaperbykotlin.core.spirit.SpiritGroup

/**
 * 场景
 * <br/>
 * 实现场景偏移与缩放
 */
open class Scene : SpiritGroup() {

    private val matrix = Matrix()

    override fun draw(canvas: Canvas) {
        canvas.save()
        canvas.concat(matrix)
        super.draw(canvas)
        canvas.restore()
    }

    override fun drawBounds(canvas: Canvas) {
        canvas.save()
        canvas.concat(matrix)
        super.drawBounds(canvas)
        canvas.restore()
    }

    /**
     * 位移
     * @param dx x轴位移量
     * @param dy y轴位移量
     */
    fun translate(dx: Float, dy: Float) {
        matrix.postTranslate(dx, dy)
    }

    /**
     * 缩放
     *  @param sx x轴缩放比例
     *  @param sy y轴缩放比例
     *  @param px 缩放中心点x坐标
     *  @param py 缩放中心点y坐标
     */
    fun scale(sx: Float, sy: Float, px: Float, py: Float) {
        matrix.postScale(sx, sy, px, py)
    }

}