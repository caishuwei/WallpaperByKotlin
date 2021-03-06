package com.example.caisw.wallpaperbykotlin.core.project.impl

import android.view.MotionEvent
import com.example.caisw.wallpaperbykotlin.core.spirit.group.TouchLine
import com.example.caisw.wallpaperbykotlin.core.spirit.lifegame.LifeGameWallPaper
import com.example.caisw.wallpaperbykotlin.core.surface.SurfaceHolderProvider

class LifeGameWallpaperProject(surfaceHolderProvider: SurfaceHolderProvider) : BaseProject(surfaceHolderProvider) {
    private var touchLine = TouchLine()
    private var wallpaper = LifeGameWallPaper()

    init {
//        drawController.clearCanvasBeforeDrawSpirit = false
        spiritHolder.addSpirit(wallpaper)
        spiritHolder.addSpirit(touchLine)
//        spiritHolder.addSpirit(Frame())
    }

    override fun onCreate() {
        super.onCreate()
        surfaceHolderProvider.getSurfaceHolder()?.let {
            val surfaceRect = it.surfaceFrame
            wallpaper.setVisibleRectF(0F, 0F, surfaceRect.right.toFloat(), surfaceRect.bottom.toFloat());
        }
    }

    override fun onDestroy() {
        wallpaper.stopEvolve()
        super.onDestroy()
    }

    override fun onSizeChanged(width: Int, height: Int) {
        super.onSizeChanged(width, height)
        wallpaper.setVisibleRectF(0F, 0F, width.toFloat(), height.toFloat());
    }

    override fun needHandleMotionEvent(): Boolean {
        return true
    }

    override fun handleMotionEvent(event: MotionEvent) {
        super.handleMotionEvent(event)
        wallpaper.onTouchEvent(event)
        touchLine.onTouchEvent(event)
    }
}