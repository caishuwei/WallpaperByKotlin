package com.example.caisw.wallpaperbykotlin.core.spirit.group

import android.view.MotionEvent
import com.example.caisw.wallpaperbykotlin.core.spirit.Spirit
import com.example.caisw.wallpaperbykotlin.core.spirit.SpiritGroup
import com.example.caisw.wallpaperbykotlin.core.spirit.impl.Comet
import com.example.caisw.wallpaperbykotlin.entities.MyPointF

class TouchLine : SpiritGroup(), Spirit.OnSpiritDestroyListener {


    /**
     * 目前的设备基本都是支持10指触摸，那么这里建立一个10长度数组用于存储手势过程中的彗星路径
     */
    private val gestureCometHolder: Array<Comet?> = Array<Comet?>(10) { null }

    fun onTouchEvent(ev: MotionEvent): Boolean {
        val pointerId = ev.getPointerId(ev.actionIndex)
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                getComet(pointerId)?.addPoint(MyPointF(ev.x, ev.y))
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                getComet(pointerId)?.addPoint(MyPointF(ev.x, ev.y))
            }
            MotionEvent.ACTION_MOVE -> {
                for (i in 0 until ev.pointerCount) {
                    getComet(ev.getPointerId(i))?.addPoint(MyPointF(ev.getX(i), ev.getY(i)))
                }
            }
            MotionEvent.ACTION_POINTER_UP -> {
                getComet(pointerId)?.addPoint(MyPointF(ev.x, ev.y))
                gestureCometHolder[pointerId] = null
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                for (i in 0 until 10) {
                    gestureCometHolder[i] = null
                }
            }
        }
        return true
    }

    private fun getComet(pointerId: Int): Comet? {
        val tla = gestureCometHolder
        return if (pointerId >= 0 && pointerId < tla.size) {
            var comet = tla[pointerId]
            if (comet == null || comet.destroy) {
                comet = Comet()
                comet.onSpiritDestroyListener = this
                addSpirit(comet)
                tla[pointerId] = comet
            }
            comet
        } else
            null
    }

    override fun onSpiritDestroy(spirit: Spirit) {
        removeSpirit(spirit)
    }
}