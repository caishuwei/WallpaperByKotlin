package com.example.caisw.wallpaperbykotlin.core.spirit

import android.graphics.Canvas
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 精灵组
 */
open class SpiritGroup : Spirit() {
    val childSpirits = CopyOnWriteArrayList<Spirit>()

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        val iterator = childSpirits.iterator()
        while (iterator.hasNext()) {
            iterator.next().draw(canvas);
        }
    }

    override fun drawBounds(canvas: Canvas) {
        boundsRect.setEmpty()
        val iterator = childSpirits.iterator()
        var spirit: Spirit
        while (iterator.hasNext()) {
            spirit = iterator.next()
            spirit.drawBounds(canvas)
            boundsRect.union(spirit.boundsRect)
        }
        super.drawBounds(canvas)
    }

    /**
     * 添加精灵
     */
    fun addSpirit(spirit: Spirit) {
        childSpirits.add(spirit)
    }

    /**
     * 移除精灵
     */
    fun removeSpirit(spirit: Spirit) {
        childSpirits.remove(spirit)
    }
}