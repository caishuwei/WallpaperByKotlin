package com.example.caisw.wallpaperbykotlin.core.project.impl

import android.util.Log
import android.view.MotionEvent
import com.example.caisw.wallpaperbykotlin.core.draw.impl.SurfaceDrawController
import com.example.caisw.wallpaperbykotlin.core.project.IProject
import com.example.caisw.wallpaperbykotlin.core.surface.SurfaceHolderProvider

abstract class BaseProject(val surfaceHolderProvider: SurfaceHolderProvider) : IProject {

    val drawController = SurfaceDrawController(surfaceHolderProvider)
    val spiritHolder = drawController.spiritHolder

    override fun onCreate() {
        drawController.startDraw()
    }

    override fun onResume() {
        drawController.startDraw()
    }

    override fun onPause() {
        drawController.startDraw()
    }

    override fun onDestroy() {
        drawController.stopDraw()
    }

    override fun needHandleMotionEvent(): Boolean {
        return false
    }

    override fun handleMotionEvent(event: MotionEvent) {
    }
}