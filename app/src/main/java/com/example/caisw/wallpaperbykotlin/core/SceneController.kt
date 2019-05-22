package com.example.caisw.wallpaperbykotlin.core

import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import com.example.caisw.wallpaperbykotlin.core.base.IMotionEventHandler
import com.example.caisw.wallpaperbykotlin.core.base.ISceneLifeCycle
import com.example.caisw.wallpaperbykotlin.core.draw.impl.SurfaceDrawController
import com.example.caisw.wallpaperbykotlin.core.spirit.group.Scene
import com.example.caisw.wallpaperbykotlin.core.spirit.group.TouchLine
import com.example.caisw.wallpaperbykotlin.core.spirit.impl.BFSSearch
import com.example.caisw.wallpaperbykotlin.core.spirit.impl.Frame
import com.example.caisw.wallpaperbykotlin.core.surface.SurfaceHolderProvider
import com.example.caisw.wallpaperbykotlin.utils.MyGestureDetector

/**
 * Created by caisw on 2018/3/16.
 */
class SceneController(private var surfaceHolderProvider: SurfaceHolderProvider) : ISceneLifeCycle, IMotionEventHandler {


    companion object {
        var DEBUG = false
    }

    private var surfaceDrawController: SurfaceDrawController? = null
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
    private val bfsSearch = BFSSearch()
    override fun onCreate() {
        Log.e("SceneController", "onCreate->${this.hashCode()}")
        val sdc = SurfaceDrawController(surfaceHolderProvider)
//        sdc.spiritHolder.addSpirit(Ring())
//        sdc.spiritHolder.addSpirit(Number(Constants.Number_1))
//        scene.addSpirit(Picture())
        scene.addSpirit(bfsSearch)

        sdc.spiritHolder.addSpirit(scene)
        sdc.spiritHolder.addSpirit(touchLine)
        sdc.spiritHolder.addSpirit(Frame())
        surfaceDrawController = sdc
    }

    override fun onResume() {
        Log.e("SceneController", "onResume->${this.hashCode()}")
        surfaceDrawController?.startDraw()
    }

    override fun onPause() {
        Log.e("SceneController", "onPause->${this.hashCode()}")
        surfaceDrawController?.stopDraw()
    }

    override fun onDestroy() {
        Log.e("SceneController", "onDestroy->${this.hashCode()}")
        if (surfaceDrawController != null) {
            surfaceDrawController?.stopDraw()
            surfaceDrawController = null
        }
    }

    override fun needHandleMotionEvent(): Boolean {
        return true
    }

    override fun handleMotionEvent(event: MotionEvent) {
        touchLine.onTouchEvent(event)
//        myGestureDetector.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                bfsSearch.destroySearch()
                bfsSearch.setObstacle(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                bfsSearch.setObstacle(event.x, event.y)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                bfsSearch.setObstacle(event.x, event.y)
                bfsSearch.startSearch()
            }
        }
    }

}