package com.example.caisw.wallpaperbykotlin.core.project.impl

import android.view.MotionEvent
import android.view.ScaleGestureDetector
import com.example.caisw.wallpaperbykotlin.core.spirit.group.Scene
import com.example.caisw.wallpaperbykotlin.core.spirit.group.TouchLine
import com.example.caisw.wallpaperbykotlin.core.spirit.impl.Frame
import com.example.caisw.wallpaperbykotlin.core.spirit.impl.Picture
import com.example.caisw.wallpaperbykotlin.core.surface.SurfaceHolderProvider
import com.example.caisw.wallpaperbykotlin.utils.MyGestureDetector

class BenxiWallpaperProject(surfaceHolderProvider: SurfaceHolderProvider) : BaseProject(surfaceHolderProvider) {
    private var touchLine = TouchLine()
    private val scene = Scene();
    private var myGestureDetector = object : MyGestureDetector() {

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            scene.translate(-distanceX, -distanceY)
            return super.onScroll(e1, e2, distanceX, distanceY)
        }

        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            detector?.let {
                scene.scale(it.scaleFactor, it.scaleFactor, it.focusX, it.focusY)
            }
            return super.onScale(detector)
        }

    }

    init {
        scene.addSpirit(Picture())
        spiritHolder.addSpirit(scene)
        spiritHolder.addSpirit(touchLine)
        spiritHolder.addSpirit(Frame())
    }

    override fun needHandleMotionEvent(): Boolean {
        return true
    }

    override fun handleMotionEvent(event: MotionEvent) {
        super.handleMotionEvent(event)
        touchLine.onTouchEvent(event)
        myGestureDetector.onTouchEvent(event)
    }

}