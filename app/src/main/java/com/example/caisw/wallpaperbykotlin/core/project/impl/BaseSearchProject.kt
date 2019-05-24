package com.example.caisw.wallpaperbykotlin.core.project.impl

import android.view.MotionEvent
import com.example.caisw.wallpaperbykotlin.core.spirit.group.TouchLine
import com.example.caisw.wallpaperbykotlin.core.spirit.impl.Frame
import com.example.caisw.wallpaperbykotlin.core.spirit.search.SearchBase
import com.example.caisw.wallpaperbykotlin.core.surface.SurfaceHolderProvider

abstract class BaseSearchProject(surfaceHolderProvider: SurfaceHolderProvider) : BaseProject(surfaceHolderProvider) {
    private var touchLine = TouchLine()
    private val searchBase = getSearchImpl()

    abstract fun getSearchImpl(): SearchBase

    init {
        spiritHolder.addSpirit(searchBase)
        spiritHolder.addSpirit(touchLine)
        spiritHolder.addSpirit(Frame())
    }

    override fun needHandleMotionEvent(): Boolean {
        return true
    }

    override fun handleMotionEvent(event: MotionEvent) {
        super.handleMotionEvent(event)
        touchLine.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                searchBase.end()
                searchBase.setObstacle(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                searchBase.setObstacle(event.x, event.y)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                searchBase.setObstacle(event.x, event.y)
                searchBase.start()
            }
        }
    }


    override fun onDestroy() {
        searchBase.end()
        super.onDestroy()
    }
}