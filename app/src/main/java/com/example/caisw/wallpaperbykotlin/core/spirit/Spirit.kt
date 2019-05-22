package com.example.caisw.wallpaperbykotlin.core.spirit

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

/**
 * 精灵类
 * Created by caisw on 2018/3/12.
 */
abstract class Spirit {

    companion object {
        /**
         * 边界绘制画笔
         */
        val BOUNDS_PAINT = Paint()

        init {
            BOUNDS_PAINT.color = Color.RED
            BOUNDS_PAINT.style = Paint.Style.STROKE
            BOUNDS_PAINT.strokeWidth = 1F
        }
    }

    var onSpiritDestroyListener: OnSpiritDestroyListener? = null
    var destroy: Boolean = false
    var x: Float = 0F
    var y: Float = 0F
    var z: Float = 0F

    val boundsRect: RectF

    constructor() : this(0F, 0F, 0F)

    constructor(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
        boundsRect = RectF(0F, 0F, 0F, 0F)
    }

    /**
     * 绘制精灵
     */
    open fun draw(canvas: Canvas) {

    }

    /**
     * 绘制精灵边界
     */
    open fun drawBounds(canvas: Canvas) {
        if (!boundsRect.isEmpty)
            canvas.drawRect(boundsRect, BOUNDS_PAINT)
    }

    open fun destroy() {
        destroy = true
        onSpiritDestroyListener?.onSpiritDestroy(this)
    }

    interface OnSpiritDestroyListener {

        fun onSpiritDestroy(spirit: Spirit)

    }
}