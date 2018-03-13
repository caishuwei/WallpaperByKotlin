package com.example.caisw.wallpaperbykotlin.spirit

import android.graphics.Canvas

/**
 * Created by caisw on 2018/3/12.
 */
abstract class BaseSpirit {

    var x: Float = 0F
    var y: Float = 0F
    var z: Float = 0F

    constructor() : this(0F, 0F, 0F)

    constructor(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }

    abstract fun drawMySelf(canvas: Canvas);
}