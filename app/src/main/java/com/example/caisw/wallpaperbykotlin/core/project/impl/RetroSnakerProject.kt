package com.example.caisw.wallpaperbykotlin.core.project.impl

import com.example.caisw.wallpaperbykotlin.core.spirit.impl.Frame
import com.example.caisw.wallpaperbykotlin.core.spirit.snaker.RetroSnaker
import com.example.caisw.wallpaperbykotlin.core.surface.SurfaceHolderProvider

class RetroSnakerProject(surfaceHolderProvider: SurfaceHolderProvider) : BaseProject(surfaceHolderProvider) {

    val retroSnaker = RetroSnaker()

    init {
        spiritHolder.addSpirit(retroSnaker)
        spiritHolder.addSpirit(Frame())
    }

    override fun onCreate() {
        super.onCreate()
        retroSnaker.start()
    }

    override fun onResume() {
        super.onResume()
        retroSnaker.start()
    }

    override fun onPause() {
        retroSnaker.stop()
        super.onPause()
    }

    override fun onDestroy() {
        retroSnaker.stop()
        super.onDestroy()
    }

}