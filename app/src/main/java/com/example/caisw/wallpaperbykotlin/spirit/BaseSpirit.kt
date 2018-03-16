package com.example.caisw.wallpaperbykotlin.spirit

import android.graphics.*

/**
 * Created by caisw on 2018/3/12.
 */
abstract class BaseSpirit {

    companion object {

        val BOUNDS_PAINT = Paint()

        init {
            BOUNDS_PAINT.color = Color.RED
            BOUNDS_PAINT.style = Paint.Style.STROKE
            BOUNDS_PAINT.strokeWidth = 1F
        }
    }

    var x: Float = 0F
    var y: Float = 0F
    var z: Float = 0F

    val boundsRect: RectF
    var isRelease: Boolean = false
        private set

    constructor() : this(0F, 0F, 0F)

    constructor(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
        boundsRect = RectF(0F, 0F, 0F, 0F)
    }

    open fun drawMySelf(canvas: Canvas) {

    }

    fun drawBounds(canvas: Canvas) {
        if (!boundsRect.isEmpty)
            canvas.drawRect(boundsRect, BOUNDS_PAINT)
    }

    open fun release() {
        isRelease = true
    }
}