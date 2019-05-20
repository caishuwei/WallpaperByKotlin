package com.example.caisw.wallpaperbykotlin.core

import android.util.Log
import android.view.MotionEvent
import com.example.caisw.wallpaperbykotlin.core.base.IMotionEventHandler
import com.example.caisw.wallpaperbykotlin.core.base.IWallpaperLifeCycle
import com.example.caisw.wallpaperbykotlin.core.base.SurfaceHolderProvider
import com.example.caisw.wallpaperbykotlin.entities.MyPointF
import com.example.caisw.wallpaperbykotlin.spirit.Picture
import com.example.caisw.wallpaperbykotlin.spirit.TouchLine

/**
 * Created by caisw on 2018/3/16.
 */
class WallpaperController : IWallpaperLifeCycle, IMotionEventHandler {


    companion object {
        var DEBUG = true
    }

    private var surfaceHolderProvider: SurfaceHolderProvider

    private var surfaceDrawController: SurfaceDrawController? = null
    private var touchLineArray: Array<TouchLine>? = null

    constructor(surfaceHolderProvider: SurfaceHolderProvider) {
        this.surfaceHolderProvider = surfaceHolderProvider
    }

    override fun onCreate() {
        Log.e("WallpaperController", "onCreate->${this.hashCode()}")
        val sdc = SurfaceDrawController(surfaceHolderProvider)
//        sdc.spiritHolder.addSpirit(Ring())
//        sdc.spiritHolder.addSpirit(Number(Constants.Number_1))
        sdc.spiritHolder.addSpirit(Picture())
        touchLineArray = Array(10) {
            val touchLine = TouchLine()
            sdc.spiritHolder.addSpirit(touchLine)
            touchLine
        }
        surfaceDrawController = sdc
    }

    override fun onResume() {
        Log.e("WallpaperController", "onResume->${this.hashCode()}")
        surfaceDrawController?.startDraw()
    }

    override fun onPause() {
        Log.e("WallpaperController", "onPause->${this.hashCode()}")
        surfaceDrawController?.stopDraw()
    }

    override fun onDestroy() {
        Log.e("WallpaperController", "onDestroy->${this.hashCode()}")
        if (surfaceDrawController != null) {
            surfaceDrawController?.stopDraw()
            surfaceDrawController = null
        }

        val tla = touchLineArray
        if (tla != null) {
            touchLineArray = null
            val iterator = tla.iterator()
            while (iterator.hasNext()) {
                iterator.next().release()
            }
        }

    }

    override fun needHandleMotionEvent(): Boolean {
        return true
    }

    override fun handleMotionEvent(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                findTouchLine(event.getPointerId(event.actionIndex))?.clearPoints()
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                findTouchLine(event.getPointerId(event.actionIndex))?.clearPoints()
            }
            MotionEvent.ACTION_MOVE -> {
                for (i in 0 until event.pointerCount) {
                    findTouchLine(event.getPointerId(i))?.addTouchPoint(MyPointF(event.getX(i), event.getY(i)))
                }
            }
        }
    }

    private fun findTouchLine(pointerId: Int): TouchLine? {
        val tla = touchLineArray
        if (tla != null && pointerId >= 0 && pointerId < tla.size)
            return tla[pointerId]
        else
            return null
    }

}